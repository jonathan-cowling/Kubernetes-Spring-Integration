#!/usr/bin/env bash

set -u -e
cd deployment/helmsman

helmsman -destroy -f releases.yml