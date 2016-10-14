#!/bin/sh

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

commit_website_files() {
  git add golang/
  git commit --message "Added compiled Golang ProtoBuf - Travis build: $TRAVIS_BUILD_NUMBER"
}

upload_files() {
  git remote add origin-push https://${GH_TOKEN}@github.com/RebuildTools/rebuild-agent-protocol.git > /dev/null 2>&1
  git push --quiet --set-upstream origin-push
}

setup_git
commit_website_files
upload_files
