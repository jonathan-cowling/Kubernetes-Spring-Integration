#!/usr/bin/env bash

set -e -u

for f in *.yml; do cat "${f}"; echo -e "\n---"; done | kubectl apply -f -