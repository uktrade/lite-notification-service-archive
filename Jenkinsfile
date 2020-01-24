def projectName = 'lite-notification-service'
pipeline {
agent {
  kubernetes {
    defaultContainer 'jnlp'
    yaml """
apiVersion: v1
kind: Pod
metadata:
labels:
  job: ${env.JOB_NAME}
  job_id: ${env.BUILD_NUMBER}
spec:
nodeSelector:
  role: worker
containers:
- name: lite-image-builder
  image: ukti/lite-image-builder
  imagePullPolicy: Always
  command:
  - cat
  tty: true
"""
  }
}

options {
    timestamps()
    ansiColor('xterm')
    buildDiscarder(logRotator(daysToKeepStr: '180'))
  }

  stages {
    stage('prep') {
      steps {
        script {
          deleteDir()
          env.BUILD_VERSION = ''
        }
      }
    }

    //stage('test') {
    //  steps {
    //   container('lite-image-builder'){
    //    script {
    //    checkout([
    //              $class: 'GitSCM', branches: [[name: "${env.GIT_BRANCH}"]],
    //              userRemoteConfigs: [[url: 'https://github.com/uktrade/lite-notification-service.git']]
    //            ])
    //        sh 'chmod 777 gradlew'
    //        try {
    //          sh "./gradlew test -i"
    //        }
    //        finally {
    //          step([$class: 'JUnitResultArchiver', testResults: 'build/test-results/**/*.xml'])
    //        }

    //    }
    //    }
    //  }
    //}

    stage('build') {
      steps {
        script {
          def buildPaasAppResult = build job: 'java-lite-build-paas-app', parameters: [
              string(name: 'BRANCH', value: env.BRANCH_NAME),
              string(name: 'PROJECT_NAME', value: projectName),
              string(name: 'BUILD_TYPE', value: 'jar')
          ]
          env.BUILD_VERSION = buildPaasAppResult.getBuildVariables().BUILD_VERSION
        }
      }
    }

    //stage('sonarqube') {
    //  steps {
    //    container('lite-image-builder'){
    //    script {

    //        withSonarQubeEnv('sonarqube') {
    //          sh 'chmod 777 gradlew'
    //          sh './gradlew compileJava compileTestJava -i'
    //          sh "${env.SONAR_SCANNER_PATH}/sonar-scanner -Dsonar.projectVersion=${env.BUILD_VERSION}"
    //        }

    //     }
    //    }
    //  }
    //}

    stage('deploy') {
      steps {
        sh 'echo $buildVersion'
        build job: 'java-lite-deploy', parameters: [
            string(name: 'PROJECT_NAME', value: projectName),
            string(name: 'BUILD_VERSION', value: env.BUILD_VERSION),
            string(name: 'ENVIRONMENT', value: env.ENVIRONMENT)
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
