#!/usr/bin/env bash

while [[ $# -gt 0 ]]; do
    case "$1" in
        --pom)
            MAVEN_POM="$2"
            shift 2
            ;;
        --snapshot)
            SNAPSHOT=true
            shift
            ;;
        *)
            echo "Unknown argument: $1"
            exit 1
            ;;
    esac
done

if [ -z "$MAVEN_POM" ]; then
    echo "Missing required argument: --pom"
    exit 1
fi

if [ -z "$SNAPSHOT" ]; then
  SNAPSHOT=false
fi

NEXT_VERSION=$(grep -rE 'version.next=[0-9]+\.[0-9]+\.[0-9]+' version.properties | cut -d '=' -f2)

MAJOR=$(echo "$NEXT_VERSION" | cut -d '.' -f1)
MINOR=$(echo "$NEXT_VERSION" | cut -d '.' -f2)
PATCH=$(echo "$NEXT_VERSION" | cut -d '.' -f3)


if [ "$SNAPSHOT" = true ]; then
  echo "Setting new version: $MAJOR.$MINOR.$((PATCH + 1))-SNAPSHOT"
  mvn -f "$MAVEN_POM" versions:set -DgenerateBackupPoms=false -DnewVersion="$MAJOR.$MINOR.$((PATCH + 1))-SNAPSHOT"
  sed -i "s/version.next=$MAJOR.$MINOR.$PATCH/version.next=$MAJOR.$MINOR.$((PATCH + 1))/g" version.properties
else
echo "Setting new version: $MAJOR.$MINOR.$PATCH"
  mvn -f "$MAVEN_POM" versions:set -DgenerateBackupPoms=false -DnewVersion="$MAJOR.$MINOR.$PATCH"
fi
