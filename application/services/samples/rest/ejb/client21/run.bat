@if "%JONAS_ROOT%"=="" goto error
@goto ok

:error
@echo ERROR: set JONAS_ROOT first
@goto end


:ok
set CLASSPATH=%JONAS_ROOT%\lib\apps\exo.core.component.organization.api-trunk.jar;
set CLASSPATH=%CLASSPATH%%JONAS_ROOT%\lib\apps\exo.kernel.component.common-trunk.jar

jclient -Djava.security.manager=default -Djava.security.policy=%JONAS_ROOT%/conf/java.policy target\exo.ws.rest.ejbconnector21.client21-jonas-trunk.jar 
@goto end


:end
