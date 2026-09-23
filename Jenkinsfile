pipeline {
  agent any
  stages {
    stage('cli-check') {
      steps {
        sh 'which smart-tests'
      }
    }
    stage('Jest Tests') {
      steps {
        wrap([$class: 'SmartTestsSubsetStep',
              target: 80,
              framework: 'jest',
              testPaths: 'js-tests/src/__tests__/**/*.test.js']) {
          dir('js-tests') {
            sh 'npm install'
            sh 'JEST_JUNIT_CLASSNAME="{filepath}" npm test -- $SMART_TEST_FILTER'
          }
        }
      }
    }
  }
  post {
    always {
      junit testResults: 'js-tests/TEST-jest-results.xml'
    }
  }
}
