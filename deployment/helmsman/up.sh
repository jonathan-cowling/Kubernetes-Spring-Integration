#!/usr/bin/env bash

set -u -e
cd deployment/helmsman

helmsman -apply -f releases.yml