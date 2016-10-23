#! /bin/bash
set -e

if [[ "$TRAVIS_PULL_REQUEST" == "false" && "$TRAVIS_BRANCH" == "master" ]]; then
  echo "$key_password" | gpg --passphrase-fd 0 ./travis/ht-oss-public.asc.gpg
  echo "$key_password" | gpg --passphrase-fd 0 ./travis/ht-oss-private.asc.gpg

  if grep "version\s*:=.*SNAPSHOT" build.sbt; then
    sbt +jsonBindersJVM/test +jsonBindersJS/test +jsonTimeBindersJVM/test +jsonTimeBindersJS/test +jsonBindersJVM/publishSigned +jsonBindersJS/publishSigned +jsonTimeBindersJVM/publishSigned +jsonTimeBindersJS/publishSigned
  else
    sbt +jsonBindersJVM/test +jsonBindersJS/test +jsonTimeBindersJVM/test +jsonTimeBindersJS/test +jsonBindersJVM/publishSigned +jsonBindersJS/publishSigned +jsonTimeBindersJVM/publishSigned +jsonTimeBindersJS/publishSigned sonatypeReleaseAll
  fi
else
  sbt +jsonBindersJVM/test +jsonBindersJS/test +jsonTimeBindersJVM/test +jsonTimeBindersJS/test
fi
