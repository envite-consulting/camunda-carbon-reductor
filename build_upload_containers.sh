#!/usr/bin/env bash

#  Build and Upload Docker Containers
# The Containers will be built for
#
#  Init docker buildx
# If you haven't initialized your docker buildx environment run the following command:
#
# docker buildx create --name <myName> --use
#
#  Docker login
# Before running the script you should be logged in to your desired docker account.
#
# docker login [repository]
#
#  Run the script

# ./build_upload_containers.sh 2.0.0 c8 c7
#

TAGNAME=$1
GITTAGNAME=v$1
C8="${2:-C8}"
C7="${3:-C7}"
if [ "$(git tag -l $GITTAGNAME)" ]
then
  git checkout tags/$GITTAGNAME -b $GITTAGNAME-branch
  if [ "$C8" = "c8" ] || [ "$C8" = "C8" ]
  then
    echo 'building c8'
    docker buildx build --push --platform linux/arm64/v8,linux/amd64 \
                  -t enviteconsulting/camunda-8-carbon-reductor-connector:latest \
                  -t enviteconsulting/camunda-8-carbon-reductor-connector:$TAGNAME \
                  -f camunda-carbon-reductor-c8/Dockerfile .
  fi
  if [ "$C7" = "c7" ] || [ "$C7" = "C7" ]
  then
    echo 'building c7'
    docker buildx build --push --platform linux/arm64/v8,linux/amd64 \
                  -t enviteconsulting/camunda-7-carbon-reductor-connector:latest \
                  -t enviteconsulting/camunda-7-carbon-reductor-connector:$TAGNAME \
                  -f camunda-carbon-reductor-c7/Dockerfile .
  fi
  git switch main
  git branch -d $GITTAGNAME-branch
else
  echo tag does not exist!
fi