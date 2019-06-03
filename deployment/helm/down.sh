#!/usr/bin/env bash

set -e -u

cd deployment/helm
helm delete --purge stream-job-example