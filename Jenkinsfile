pipeline {
  agent any
  stages {
    stage('cli-check') {
      steps {
        sh 'which smart-tests'
      }
    }
    stage('Maven Tests') {
      steps {
        wrap([$class: 'SmartTestsSubsetStep']) {
          sh '''
            smart-tests subset --from-jenkins --target 80% maven src/test/java > filter.txt
            export SMART_TEST_FILTER=$(cat filter.txt)
            mvn test -Dtest=$SMART_TEST_FILTER
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
