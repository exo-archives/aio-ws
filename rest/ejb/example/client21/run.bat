@if "%JONAS_ROOT%"=="" goto error
@goto ok

:error
@echo ERROR: set JONAS_ROOT first
@goto end


:ok
set CLASSPATH=%JONAS_ROOT%\lib\apps\exo.core.component.organization.api-2.0.3.jar;
set CLASSPATH=%CLASSPATH%%JONAS_ROOT%\lib\apps\exo.kernel.component.common-2.0.3.jar;
set CLASSPATH=%CLASSPATH%%JONAS_ROOT%\lib\apps\exo.ejb.loginmodule-0.1.jar

jclient -Djava.security.manager=default -Djava.security.policy=c:\exo-jonas2/conf/java.policy target\exo.simple.rest.service.client21-jonas-0.1.jar 
@goto end


:end
