def call (String username, String image_name, String tag) {
    sh "docker build -t ${username}/${image_name}:${tag} ."
}
