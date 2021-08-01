#!/bin/bash

die() {
	echo "ERROR: ${1}" && exit 1;
}

[ -z "${JAVA_HOME}" ] && die "JAVA_HOME environment variable not set."

install_dir=$(cd "$(dirname "$0")"/.. && pwd)
lib_dir="${install_dir}/service/lib"

echo "using JAVA_HOME: ${JAVA_HOME}"
echo "install dir: $install_dir"
echo "lib dir: $lib_dir"

"${JAVA_HOME}/bin/java" -cp "${lib_dir}"/'*' org.dougmcintosh.index.IndexerCli "$@"