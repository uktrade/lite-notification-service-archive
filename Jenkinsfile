def projectName = 'lite-notification-service'
pipeline {
  agent {
    node {
      label 'docker.ci.uktrade.io'
    }
  }

  stages {
    stage('prep') {
      steps {
        script {
          deleteDir()
          checkout scm
          deployer = docker.image("ukti/lite-image-builder")
          deployer.pull()
        }
      }
    }

    stage('test') {
      steps {
        script {
          deployer.inside {
            sh 'chmod 777 gradlew'
            try {
              sh "./gradlew test -i"
            }
            finally {
              step([$class: 'JUnitResultArchiver', testResults: 'build/test-results/**/*.xml'])
            }
          }
        }
      }
    }

    stage('build') {
      steps {
        script {
          def buildPaasAppResult = build job: 'build-paas-app', parameters: [
              string(name: 'BRANCH', value: env.BRANCH_NAME),
              string(name: 'PROJECT_NAME', value: projectName),
              string(name: 'BUILD_TYPE', value: 'jar')
          ]
          env.BUILD_VERSION = buildPaasAppResult.getBuildVariables().BUILD_VERSION
        }
      }
    }

    stage('sonarqube') {
      steps {
        script {
          deployer.inside {
            withSonarQubeEnv('sonarqube') {
              sh 'chmod 777 gradlew'
              sh './gradlew compileJava compileTestJava -i'
              sh "${env.SONAR_SCANNER_PATH}/sonar-scanner -Dsonar.projectVersion=${env.BUILD_VERSION}"
            }
          }
        }
      }
    }

    stage('deploy') {
      steps {
        sh 'echo $buildVersion'
        build job: 'deploy', parameters: [
            string(name: 'PROJECT_NAME', value: projectName),
            string(name: 'BUILD_VERSION', value: env.BUILD_VERSION),
            string(name: 'ENVIRONMENT', value: 'dev')
        ]
      }
    }
  }

  post {
    always {
      script {
        currentBuild.displayName = "#" + currentBuild.number + " - ${env.BUILD_VERSION}"
        deleteDir()
      }
    }
  }
}