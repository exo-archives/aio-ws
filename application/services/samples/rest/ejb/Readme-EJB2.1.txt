For launch examples do next:

NOTE: This example work with EJB2.1.

1. Compile project 'connector21' and put 'exo.simple.rest.ejbconnector21-0.1.jar' in JONAS_ROOT/ejbjars and deploy
   it by command:
   
     './jonas admin -a exo.ws.rest.ejbconnector21.client21-jonas-${org.exoplatform.ws.version}.jar'
     './jonas admin -j'
   
   You should see  your bean in the list.

2. Compile 'simple-service' and put 'exo.ws.simple.rest.service-${org.exoplatform.ws.version}.jar' in lib directory of jonas.
   NOTE: File 'exo.ws.rest.core-${org.exoplatform.ws.version}.jar' should be present!

3. Compile 'client21'. Run example by command run.sh or run.bat.
   NOTE: The variable JONAS_ROOT should be set!

   
