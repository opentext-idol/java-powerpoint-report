if [[ ${TRAVIS_BRANCH} == 'master' ]]
then
  echo "Building Maven Site and deploying to GitHub pages"
  mvn site
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
  chmod 600 ../../java-powerpoint-report-deploy-key
  GIT_SSH_COMMAND='ssh -i ../../java-powerpoint-report-deploy-key ' git push --force origin master:gh-pages
fi