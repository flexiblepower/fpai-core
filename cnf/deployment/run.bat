@echo off
title Starting the PowerMatcher Suite
echo Starting the PowerMatcher Suite
echo Running from %CD%

start "PowerMatcher Suite" java ^
	-Djava.security.policy=etc/all.policy ^
	-Dfelix.config.properties=file:etc/config.properties ^
	-Dfelix.cm.dir="%CD%\config" ^
	-Dlogback.configurationFile=etc/logback.xml ^
	-jar org.apache.felix.main.jar
