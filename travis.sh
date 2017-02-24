if [[ ${TRAVIS_BRANCH} == 'master' ]]
then
  echo "Building Maven Site and deploying to GitHub pages"
  mvn site:site
  # mvn site used to do this, but now API rate limiting makes it a non starter
  cd target/site
  git config --global user.email "Travis CI"
  git config --global user.name "tung-jin-chew-hp@users.noreply.github.com"
  echo "Creating repo"
  git init
  echo "Adding remote"
  git remote add origin "git@github.com:${TRAVIS_REPO_SLUG}"
  echo "Adding all the files"
  git add .
  echo "Committing"
  git commit -m "Update GitHub Pages"
  echo "Pushing"
  echo ${GPG_KEY} > tmp.txt && gpg --batch --passphrase-fd 3 3<tmp.txt ../../java-powerpoint-report-deploy-key.gpg
  mkdir .ssh
  cp ../../java-powerpoint-report-deploy-key .ssh/java-powerpoint-report-deploy-key
  chmod go-rw -R .ssh
  GIT_SSH_COMMAND="ssh -i $PWD/.ssh/java-powerpoint-report-deploy-key " git push --force origin master:gh-pages
fi