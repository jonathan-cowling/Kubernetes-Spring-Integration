#!/usr/bin/env bash

set -u -e
cd deployment/helmfile

helmfile -f releases.yml destroy