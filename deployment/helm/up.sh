#!/usr/bin/env bash

set -e -u

cd deployment/helm
helm install stream-job-example --name stream-job-example --values values.yml
