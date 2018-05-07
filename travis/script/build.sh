#! /bin/bash
set -e

if [[ "$TRAVIS_PULL_REQUEST" == "false" && (("$TRAVIS_BRANCH" == "master") || ("$TRAVIS_BRANCH" == release-*)) ]]; then
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
  echo "$key_password" | gpg --passphrase-fd 0 $SCRIPT_DIR/ht-oss-public.asc.gpg
  echo "$key_password" | gpg --passphrase-fd 0 $SCRIPT_DIR/ht-oss-private.asc.gpg

  if grep "version\s*:=.*SNAPSHOT" build.sbt; then
    sbt ";set isSnapshot := true;set scalaVersion in Global := \"$TRAVIS_SCALA_VERSION\"; test; publishSigned"
  else
    sbt ";set scalaVersion in Global := \"$TRAVIS_SCALA_VERSION\"; test"
  	# wait different time for different jobs, due to race condition releasing in sonatype
  	if [[ "$TRAVIS_JOB_NUMBER" =~ ^[[:digit:]]+\.([[:digit:]]+)$ ]]; then
  		job_number=${BASH_REMATCH[1]}
  		i=5
  		wait_time=$(( ($job_number-1)*30 ))
  		echo "Waiting for job $job_number ($TRAVIS_JOB_NUMBER) for $wait_time x $i seconds..."
  		date
        while (( i >= 1 )); do
            sleep $wait_time
            echo $(( i-- ))
        done
  		date
  	fi
    sbt ";set scalaVersion in Global := \"$TRAVIS_SCALA_VERSION\"; publishSigned"
    sbt sonatypeReleaseAll || EXIT_CODE=$? && true
    if [[ $EXIT_CODE -ne 0 ]]; then
        NC='\033[0m'
        RED='\033[0;31m'
        echo -e "${RED} !!! sonatypeReleaseAll failed, please release manually !!! ${NC}"
    fi
  fi
else
  sbt ++$TRAVIS_SCALA_VERSION test
fi
