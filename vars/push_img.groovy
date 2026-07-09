def call (String username, String image_name, String tag) {
   echo 'Pushing Image to DockerHub'
                    withCredentials([usernamePassword(
                       credentialsId: 'dockerHub',
                       usernameVariable: 'USER',
                       passwordVariable: 'PASS'
                   )]) {
                       sh """
                       echo "$PASS" | docker login -u "$USER" --password-stdin
                       docker push ${username}/${image_name}:${tag}
                       """
                  }
}
