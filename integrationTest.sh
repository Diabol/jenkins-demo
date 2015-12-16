#!/bin/sh
rm -rf test-reports
mkdir -p test-reports
#echo '<testsuite><testcase time="0.131" classname="se.diabol.jenkinsdemo.TestRegistrationSrvc" name="serviceShouldBeAvailable"><failure>The Registration Service feature test failed because the service was unavailable.</failure></testcase></testsuite>' > test-reports/status.xml
echo '<testsuite><testcase time="0.131" classname="se.diabol.jenkinsdemo.TestRegistrationSrvc" name="serviceShouldBeAvailable"/></testsuite>' > test-reports/status.xml
