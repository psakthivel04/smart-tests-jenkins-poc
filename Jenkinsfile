pipeline {
  agent any
  stages {
    stage('Jest Tests') {
      steps {
        dir('js-tests') {
          sh 'npm install'
          sh 'JEST_JUNIT_CLASSNAME="{filepath}" npm test'
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
