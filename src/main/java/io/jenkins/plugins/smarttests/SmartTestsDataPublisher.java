package io.jenkins.plugins.smarttests;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestDataPublisher;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * POC: Hooks into the JUnit plugin publishing step to read raw XML files
 * BEFORE Jenkins normalizes them (i.e., before file= attribute is stripped).
 *
 * Usage in Jenkinsfile:
 *   junit testResults: '**\/TEST-*.xml', testDataPublishers: [launchable()]
 */
public class SmartTestsDataPublisher extends TestDataPublisher {

    private static final Pattern FILE_ATTR      = Pattern.compile("\\bfile=\"([^\"]+)\"");
    private static final Pattern CLASSNAME_ATTR = Pattern.compile("\\bclassname=\"([^\"]+)\"");
    private static final Pattern TESTCASE_ELEM  = Pattern.compile("<testcase\\b[^>]*/?>", Pattern.DOTALL);

    private static String detectFramework(String firstClassname, String firstFilePath) {
        if (firstFilePath != null && firstFilePath.endsWith(".rb"))                    return "RUBY (RSpec)";
        if (firstClassname != null && firstClassname.matches(".*\\.(js|ts|jsx|tsx)$")) return "JAVASCRIPT (Jest)";
        if (firstClassname != null && firstClassname.startsWith("github.com/")
                || firstClassname != null && firstClassname.matches("[a-z]+\\..*/.+")) return "GO";
        if (firstClassname != null && firstClassname.contains("test_"))               return "PYTHON (pytest)";
        return "JAVA";
    }

    @DataBoundConstructor
    public SmartTestsDataPublisher() {}

    @Override
    public TestResultAction.Data contributeTestData(
            Run<?, ?> run,
            FilePath workspace,
            Launcher launcher,
            TaskListener listener,
            TestResult testResult) throws IOException, InterruptedException {

        listener.getLogger().println("");
        listener.getLogger().println("========================================");
        listener.getLogger().println("  Smart Tests POC — TestDataPublisher");
        listener.getLogger().println("========================================");

        // 1. Print git environment variables (set by Jenkins Git plugin automatically)
        String gitCommit = run.getEnvironment(listener).get("GIT_COMMIT", "(not set)");
        String gitUrl    = run.getEnvironment(listener).get("GIT_URL",    run.getEnvironment(listener).get("GIT_URL_1", "(not set)"));
        String gitBranch = run.getEnvironment(listener).get("GIT_BRANCH", "(not set)");
        listener.getLogger().println("[SmartTests] GIT_COMMIT : " + gitCommit);
        listener.getLogger().println("[SmartTests] GIT_URL    : " + gitUrl);
        listener.getLogger().println("[SmartTests] GIT_BRANCH : " + gitBranch);
        listener.getLogger().println("[SmartTests] Build      : #" + run.getNumber() + " — " + run.getParent().getFullName());
        listener.getLogger().println("");

        // 2. Derive owner/repo from GIT_URL
        if (!gitUrl.equals("(not set)")) {
            String ownerRepo = gitUrl
                    .replaceAll(".*github\\.com[:/]", "")
                    .replaceAll("\\.git$", "");
            listener.getLogger().println("[SmartTests] Resolved owner/repo: " + ownerRepo);
        }
        listener.getLogger().println("");

        // 3. Walk through every test suite and read the raw XML from disk
        int totalSuites = 0;
        int suitesWithFileAttr = 0;
        int totalCasesWithFileAttr = 0;
        int totalCases = 0;

        for (SuiteResult suite : testResult.getSuites()) {
            // Skip Jenkins plugin harness injected tests — not real user tests
            if (suite.getName() != null && suite.getName().contains("InjectedTest")) continue;

            totalSuites++;
            String xmlPath = suite.getFile(); // path relative to workspace, or null

            listener.getLogger().println(
                    "[SmartTests] Suite: " + suite.getName()
                    + "  cases=" + suite.getCases().size()
                    + "  xmlPath=" + xmlPath);

            totalCases += suite.getCases().size();

            if (xmlPath == null) {
                listener.getLogger().println("             → SuiteResult.getFile() returned null — cannot read raw XML");
                continue;
            }

            // Read raw bytes from the agent workspace (FilePath handles remoting automatically)
            FilePath xmlFile = workspace.child(xmlPath);
            if (!xmlFile.exists()) {
                listener.getLogger().println("             → file not found on agent: " + xmlPath);
                continue;
            }

            String rawXml;
            try (InputStream is = xmlFile.read()) {
                rawXml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            // Extract first classname from raw XML (for framework detection)
            Matcher cm = CLASSNAME_ATTR.matcher(rawXml);
            String firstClassname = cm.find() ? cm.group(1) : null;

            // Count file= occurrences
            Matcher fm = FILE_ATTR.matcher(rawXml);
            int fileAttrsInSuite = 0;
            String firstFilePath = null;
            while (fm.find()) {
                fileAttrsInSuite++;
                if (firstFilePath == null) firstFilePath = fm.group(1);
            }

            String framework = detectFramework(firstClassname, firstFilePath);

            if (fileAttrsInSuite > 0) {
                suitesWithFileAttr++;
                totalCasesWithFileAttr += fileAttrsInSuite;
                listener.getLogger().println(
                        "             → [" + framework + "] file= FOUND  count=" + fileAttrsInSuite
                        + "  first=\"" + firstFilePath + "\"");
            } else {
                listener.getLogger().println(
                        "             → [" + framework + "] file= NOT present");
            }

            // Use regex to find <testcase> elements (handles single-line XML like pytest)
            listener.getLogger().println("             → [" + framework + "] testcase elements:");
            listener.getLogger().println("             ----------------------------------------");
            Matcher tc = TESTCASE_ELEM.matcher(rawXml);
            while (tc.find()) {
                listener.getLogger().println("               " + tc.group().trim());
            }
            listener.getLogger().println("             ----------------------------------------");
            listener.getLogger().println("");
        }

        // 4. Summary
        listener.getLogger().println("========================================");
        listener.getLogger().println("[SmartTests] Summary:");
        listener.getLogger().println("  Total suites     : " + totalSuites);
        listener.getLogger().println("  Suites with file=: " + suitesWithFileAttr + " / " + totalSuites);
        listener.getLogger().println("  Total test cases : " + totalCases);
        listener.getLogger().println("  Cases with file= : " + totalCasesWithFileAttr);
        if (totalCases > 0) {
            int pct = (int) ((double) totalCasesWithFileAttr / totalCases * 100);
            listener.getLogger().println("  Coverage         : " + pct + "%");
        }
        listener.getLogger().println("========================================");
        listener.getLogger().println("");

        // POC: no data stored in Jenkins — production version would upload to S3
        return null;
    }

    @Extension
    @Symbol("launchable")
    public static final class DescriptorImpl extends Descriptor<TestDataPublisher> {
        @Override
        public String getDisplayName() {
            return "Smart Tests (POC — log raw XML)";
        }
    }
}
