#!/bin/bash -e
mvn -e -ntp clean verify -P ci -DskipTests=true -DskipITests=false