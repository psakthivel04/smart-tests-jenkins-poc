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
        wrap([$class: 'SmartTestsSubsetStep']) {
          dir('js-tests') {
            sh 'npm install'
            sh '''
              find src/__tests__ -name "*.test.js" \
                | smart-tests subset --from-jenkins --target 80% --base . jest > filter.txt
              export SMART_TEST_FILTER=$(cat filter.txt)
              JEST_JUNIT_CLASSNAME="{filepath}" npm test -- $SMART_TEST_FILTER
            '''
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
