pipeline {
    agent any

    environment {
        GIT_URL = 'https://github.com/242lee/skala-practice.git'
        GIT_BRANCH = 'main' // 또는 master
        GIT_ID = 'skala-github-id' // GitHub PAT credential ID
        IMAGE_REGISTRY = 'amdp-registry.skala-ai.com/skala25a'
        IMAGE_NAME = 'sk020-my-app'
        IMAGE_TAG = '1.0.kaniko-docker'
        DOCKER_CREDENTIAL_ID = 'skala-image-registry-id'  // Harbor 인증 정보 ID
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: "${GIT_BRANCH}",
                    url: "${GIT_URL}",
                    credentialsId: "${GIT_ID}"   // GitHub PAT credential ID
            }
        }

        stage('Build with Maven') {
            steps {
                 dir('01.my-app-jpa') {
                sh 'mvn clean package -DskipTests'
                 }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry("https://${IMAGE_REGISTRY}", "${DOCKER_CREDENTIAL_ID}") {
                        def appImage = docker.build("${IMAGE_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}", "--platform=linux/amd64 ./01.my-app-jpa")
                        appImage.push()
                    }
                }
            }
        }

        stage('Update Deployment with Digest') {
            steps {
                dir('01.my-app-jpa') {
                    script {
                        digest = sh(script: "docker inspect --format='{{index .RepoDigests 0}}' ${IMAGE_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}", returnStdout: true).trim()
                        digest = digest.split('@')[1]
                        sh """
                        sed -i 's|image: .*|image: ${IMAGE_REGISTRY}/${IMAGE_NAME}@${digest}|' k8s/deploy.yaml
                        cat k8s/deploy.yaml
                        """
                    }
                }
            }
        }
        
        stage('Git Commit & Push') {
            steps {
                dir('01.my-app-jpa') {
                    script {
                        withCredentials([usernamePassword(credentialsId: "${GIT_ID}", usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                            def cleanGitUrl = "${GIT_URL.replace('https://', '').replace('.git', '')}"
                            sh """
                            git config user.name "${GIT_USERNAME}"
                            git config user.email dlthgml1501@gmail.com
                            git add k8s/deploy.yaml
                            git commit -m "Update image to ${IMAGE_TAG} with digest ${digest}" || echo "No changes to commit"
                            git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${cleanGitUrl} ${GIT_BRANCH}
                            """
                        }
                    }
                }
            }
        }
    }
}
