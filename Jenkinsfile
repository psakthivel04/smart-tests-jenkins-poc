pipeline {
  agent any
  stages {
    stage('Maven Tests') {
      steps {
          withSmartTests {
            sh '''
              source /Users/psakthivel/IdeaProjects/smart-tests-cli/.venv/bin/activate
              smart-tests subset --from-jenkins --target 80% maven src/test/java > filter.txt
              cat filter.txt
              mvn test -Dtest=$(paste -sd, filter.txt)
            '''
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
