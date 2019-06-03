#!/usr/bin/env bash

set -e -u
cd deployment/kubernetes

for f in *.yml; do cat "${f}"; echo -e "\n---"; done | kubectl delete -f -