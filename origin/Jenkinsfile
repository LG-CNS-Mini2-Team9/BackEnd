pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "suhyunkim7288/csrrr:latest"
        EC2_HOST = "ubuntu@13.125.34.183"
        SSH_CRED_ID = "ec2-key"
        OPENAI_API_KEY = credentials('OPENAI_API_KEY')
        CLOUD_AWS_CREDENTIALS_SECRET_KEY = credentials('CLOUD_AWS_CREDENTIALS_SECRET_KEY')
        CLOUD_AWS_CREDENTIALS_ACCESS_KEY = credentials('CLOUD_AWS_CREDENTIALS_ACCESS_KEY')
        CLOUD_AWS_S3_BUCKET = credentials('CLOUD_AWS_S3_BUCKET')
        SPRING_DATA_REDIS_HOST = credentials('SPRING_DATA_REDIS_HOST')
        SPRING_DATA_REDIS_PORT = credentials('SPRING_DATA_REDIS_PORT')
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/lgcnsCampMprjTeam2/BackEnd.git'
            }
        }

        stage('Build JAR') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh '''
                        docker build -t $DOCKER_IMAGE .
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                        docker push $DOCKER_IMAGE
                    '''
                }
            }
        }


        stage('Deploy to EC2') {
            steps {
                sshagent([SSH_CRED_ID]) {
                    sh """
                    ssh -o StrictHostKeyChecking=no $EC2_HOST '
                      CURRENT_PROFILE=\$(curl -s http://localhost/profile)
                      if [ "\$CURRENT_PROFILE" = "deploy1" ]; then
                        IDLE_PORT=8082
                        NEXT_PROFILE=deploy2
                      else
                        IDLE_PORT=8081
                        NEXT_PROFILE=deploy1
                      fi

                      echo "배포할 포트: \$IDLE_PORT, 적용할 프로파일: \$NEXT_PROFILE"

                      docker stop app-\$IDLE_PORT || true
                      docker wait app-\$IDLE_PORT
                      docker rm app-\$IDLE_PORT || true
                      docker pull $DOCKER_IMAGE

                      docker run -d -p \$IDLE_PORT:8080 --name app-\$IDLE_PORT \
                        -e OPENAI_API_KEY=$OPENAI_API_KEY \
                        -e CLOUD_AWS_CREDENTIALS_SECRET_KEY=$CLOUD_AWS_CREDENTIALS_SECRET_KEY \
                        -e CLOUD_AWS_CREDENTIALS_ACCESS_KEY=$CLOUD_AWS_CREDENTIALS_ACCESS_KEY \
                        -e CLOUD_AWS_S3_BUCKET=$CLOUD_AWS_S3_BUCKET \
                        -e SPRING_DATA_REDIS_HOST=$SPRING_DATA_REDIS_HOST \
                        -e SPRING_DATA_REDIS_PORT=$SPRING_DATA_REDIS_PORT \
                        $DOCKER_IMAGE --spring.profiles.active=\$NEXT_PROFILE


                      for i in {1..10}; do
                        sleep 5
                        RESPONSE=\$(curl -s http://localhost:\$IDLE_PORT/profile)
                        if [ "\$RESPONSE" = "\$NEXT_PROFILE" ]; then
                          echo "✅ Health check 성공: \$RESPONSE"
                          break
                        fi
                        if [ "\$i" -eq 10 ]; then
                          echo "❌ Health check 실패"
                          exit 1
                        fi
                      done

                      ~/switch_nginx.sh \$IDLE_PORT
                    '
                    """
                }
            }
        }
    }
}
