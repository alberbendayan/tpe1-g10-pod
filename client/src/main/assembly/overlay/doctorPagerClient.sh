#!/bin/bash
cd "$(dirname "$0")"

for i in "$@"; do
    case $i in
        -D*=*)
        JAVA_OPTS="$JAVA_OPTS $i"
        ;;
        *)
        break
        ;;
    esac
done

MAIN_CLASS="ar.edu.itba.pod.client.DoctorPagerClient"

java $JAVA_OPTS -cp 'lib/jars/*'  $MAIN_CLASS $*
