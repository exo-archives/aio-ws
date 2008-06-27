@if "%JONAS_ROOT%"=="" goto error
@goto ok

:error
@echo ERROR: set JONAS_ROOT first
@goto end

:ok
set CLASSPATH=%JONAS_ROOT%\lib\apps\exo.core.component.security.core-2.1.jar

%JONAS_ROOT%\bin\nt\jclient -Djava.security.manager=default -Djava.security.policy=%JONAS_ROOT%/conf/java.policy target\exo.ws.application.ejbconnector21.client21-1.2.jar
@goto end

:end
