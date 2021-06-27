#!/usr/bin/env bash

APPNAME=${2:-$(basename "${1}" '.sh')};
DIR="${APPNAME}.app/Contents/MacOS";

if [ -a "${APPNAME}.app" ]; then
	echo "${PWD}/${APPNAME}.app already exists :(";
	exit 1;
fi;

mkdir -p "${DIR}";
cp "${1}" "${DIR}/${APPNAME}";
cp "jycntw.icns" "${APPNAME}.app/Contents/Resources"
chmod +x "${DIR}/${APPNAME}";

echo "${PWD}/$APPNAME.app";