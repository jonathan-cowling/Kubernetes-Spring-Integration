#!/usr/bin/env bash

set -e -u

for f in */k8s/*.yml; do cat "${f}"; echo -e "\n---"; done | kubectl apply -f -