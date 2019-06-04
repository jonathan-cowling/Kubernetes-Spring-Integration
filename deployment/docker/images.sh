#!/usr/bin/env bash

docker build -t streamjobexample:local ../../job

docker build -t rabbit:local ../../rabbit/server

docker build -t rabbit-connect:local ../../rabbit/connect