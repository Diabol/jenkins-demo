#!/bin/sh
rm -rf test-reports
mkdir -p test-reports
echo '<testsuite><testcase classname="se.diabol.jenkinsdemo.RegistrationSrvcTest" name="ServiceUnavailableError"><failure>The Registration Service feature test failed because the service was unavailable.</failure></testcase></testsuite>' > test-reports/status.xml
