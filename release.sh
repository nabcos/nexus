#!/bin/bash
mvn() {
  if [[ "$1" = +* ]]
  then
    # chomp '+' sign
    dir=$1
    dir="/tmp/mvnrepo/${dir:1}"
    shift
  else
      dir=`realpath "."`
      while [ ! -e "$dir/.git" ] || [ "$dir" = "/" ]
      do
	  dir=`realpath "$dir/.."`
      done

      if [ "$dir" = "/" ]
      then
	  dir="default"
      fi
      dir="/tmp/mvnrepo/$(basename `realpath $dir`)"
  fi

  echo "using local repo $dir"
  # ionice using class 'idle'
  ionice -c 3 $HOME/local/bin/mvn -Dmaven.repo.local="$dir" "$@"
}

edir=`realpath .`

if ! [ -e "/tmp/mvnrepo/nexus-4296" ]
then
    for i in sisu-maven-bridge sisu-restlet-bridge sisu-jetty7 classworlds-configuration-io provided-dependencies-maven-plugin
    do
	cd $HOME/sonatype/repos/$i && mvn +nexus-4296 clean install -DskipTests || exit 1
    done
fi

cd "$edir" && \
    mvn +nexus-4296 release:prepare -DreleaseVersion=1.9.3-FAKE -DdevelopmentVersion=1.9.4-SNAPSHOT -Dtag=nexus-1.9.3-FAKE -DlocalCheckout=true -DpushChanges=false -Pgpg && \
    mvn +nexus-4296 release:perform -DreleaseVersion=1.9.3-FAKE -DdevelopmentVersion=1.9.4-SNAPSHOT -Dtag=nexus-1.9.3-FAKE -DlocalCheckout=true -DpushChanges=false -Pgpg -DforgeReleaseUrl=http://localhost/service/local/staging/deploy/maven2
