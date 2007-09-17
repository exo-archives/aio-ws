@if "%JONAS_ROOT%"=="" goto error
@goto ok

:error
@echo ERROR: set JONAS_ROOT first
@goto end

:ok
%JONAS_ROOT%\bin\nt\jclient.bat -Djava.security.manager=default -Djava.security.policy=%JONAS_ROOT%\conf\java.policy .\target\exo.simple.rest.service.client21-jonas-0.1.jar
@goto end

:end


