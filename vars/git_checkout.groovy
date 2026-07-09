def call(String repo_url, String branch_name) {
    git url: "${repo_url}" , branch: "${branch_name}"
}
