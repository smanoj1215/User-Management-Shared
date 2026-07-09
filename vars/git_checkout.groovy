def call(Sting repo_url, String branch_name) {
    git url: "${repo_url}" , branch: "${branch_name}"
}
