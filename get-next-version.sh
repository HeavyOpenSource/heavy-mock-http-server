#!/usr/bin/env bash

grep -rE 'version.next=[0-9]+\.[0-9]+\.[0-9]+' version.properties | cut -d '=' -f2
