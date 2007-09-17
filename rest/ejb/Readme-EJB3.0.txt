For launch examples do next:

NOTE: This example work with EJB3.

1. Install easybeans for Jonas.
   1.1. Download ow_easybeans_for_jonas from http://download.fr.forge.objectweb.org/jonas/ow_easybeans_for_jonas-1.0.0.M6.zip.
     Unpack this archive and put file ow_easybeans_for_jonas.rar $JONAS_BASE/rars and run command:
      
        'jonas admin -a ow_easybeans_for_jonas.rar'
     
     or just put it $JONAS_BASE/rars/autoload and restart Jonas.

2. Building.
	 2.1. Compile project connector30 and put exo.simple.rest.ejbconnector30-0.1.jar
     into directory $JONAS_BASE/easybeans-deploy.
     Then run jonas admin console: jonas admin and run next command jndiname. You should see
     yopur bean in the list.
   2.2. Compile example/simple-service and put exo.simple.rest.service-0.1.jar in lib directory of jonas.
     NOTE: File exo.rest.core-0.1.jar should be present!
   2.3. Compile example/client30.
     Try to launch client. NOTE: To do this you need ant. Run command:
   
   'ant -f run.xml -Djonas.home=$HOME/exo-java/exo-working/exo-jonas'

   		Parameter:
		   -Djonas.home=$HOME/exo-java/exo-working/exo-jonas - $JONAS_BASE.
   
