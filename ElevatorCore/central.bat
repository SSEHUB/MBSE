@echo off

SET VERSION=0.5.4
SET TARGET=https://oss.sonatype.org/service/local/staging/deploy/maven2
SET REPO=ossrh-iip
SET DEPLOYCMD=mvn gpg:sign-and-deploy-file -Durl=%TARGET% -DrepositoryId=%REPO%
SET FOLDER=target
SET PREFIX=mbse.ElevatorCore-%VERSION%

call %DEPLOYCMD% -DpomFile=pom.xml -Dfile=%FOLDER%\%PREFIX%.jar
call %DEPLOYCMD% -DpomFile=pom.xml -Dfile=%FOLDER%\%PREFIX%-tests.jar -Dclassifier=tests
call %DEPLOYCMD% -DpomFile=pom.xml -Dfile=%FOLDER%\%PREFIX%-javadoc.jar -Dclassifier=javadoc
call %DEPLOYCMD% -DpomFile=pom.xml -Dfile=%FOLDER%\%PREFIX%-sources.jar -Dclassifier=sources
call %DEPLOYCMD% -DpomFile=pom.xml -Dfile=%FOLDER%\%PREFIX%-test-sources.jar -Dclassifier=test-sources
call %DEPLOYCMD% -DpomFile=pom.xml -Dfile=%FOLDER%\%PREFIX%-test-javadoc.jar -Dclassifier=test-javadoc
