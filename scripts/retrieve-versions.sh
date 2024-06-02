#!/usr/bin/env bash

VALUE=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout -f "$1")
VERSION=$(echo "$VALUE" | cut -d'-' -f1)

MAJOR=$(echo "$VERSION" | cut -d'.' -f1)
MINOR=$(echo "$VERSION" | cut -d'.' -f2)
PATCH=$(echo "$VERSION" | cut -d'.' -f3)

echo "$MAJOR.$MINOR.$PATCH:$MAJOR.$MINOR.$((PATCH + 1))-SNAPSHOT"
