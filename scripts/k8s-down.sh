#!/usr/bin/env bash

set -e -u

for f in kubernetes/*.yml; do cat "${f}"; echo -e "\n---"; done | kubectl delete -f -