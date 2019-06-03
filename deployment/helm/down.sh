#!/usr/bin/env bash

set -e -u

cd helm
helm delete --purge stream-job-example