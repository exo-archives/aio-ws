@set EXO_SERVER=c:\exo-tomcat

@if "%EXO_SERVER%"=="" goto error
@goto ok

:error
@echo ERROR: set EXO_SERVER first
@goto end

:ok
@set CLASSPATH=%EXO_SERVER%\lib\xfire-all-1.2.6.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\commons-logging-1.0.4.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\stax-api-1.0.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\wsdl4j-1.6.1.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\activation-1.1.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\jdom-1.0.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\xfire-jsr181-api-1.0.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\jaxb-api-2.0.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\commons-httpclient-3.1.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\commons-codec-1.2.jar;
@set CLASSPATH=%CLASSPATH%%EXO_SERVER%\lib\sjsxp-1.0.jar;
@set CLASSPATH=%CLASSPATH%.\target\exo.ws.soap.xfire.ticketservice.client-1.2.jar

java org.exoplatform.services.ws.soap.jsr181.client.Main

@goto end

:end
