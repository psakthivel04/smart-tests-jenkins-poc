pipeline {
  agent any
  stages {
    stage('Maven Tests') {
      steps {
        sh 'mvn test'
      }
    }
  }
  post {
    always {
      junit testResults: 'target/surefire-reports/*.xml'
    }
  }
}
