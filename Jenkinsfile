pipeline {
  agent any
  stages {
    stage('Maven Tests') {
      steps {
        script {
          if (env.SMART_TESTS_ENABLED == 'true') {
            withSmartTests {
              sh '''
                source /Users/psakthivel/IdeaProjects/smart-tests-cli/.venv/bin/activate
                smart-tests subset --from-jenkins --target 80% maven src/test/java > filter.txt
                cat filter.txt
                mvn test -Dtest=$(paste -sd, filter.txt)
              '''
            }
          } else {
            sh 'mvn test'
          }
        }
      }
    }
  }
  post {
    always {
      junit testResults: 'target/surefire-reports/*.xml'
    }
  }
}
