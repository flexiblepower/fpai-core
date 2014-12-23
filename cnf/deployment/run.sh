#!/bin/bash
echo Running from `pwd`

java \
	-Djava.security.policy=etc/all.policy \
	-Dfelix.config.properties=file:etc/config.properties \
	-Dfelix.cm.dir="`pwd`/config" \
	-Dlogback.configurationFile=etc/logback.xml \
	-jar org.apache.felix.main.jar
