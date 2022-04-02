#!/bin/bash -e
mvn -e -ntp verify -P ci -DskipTests=true -DskipITests=false