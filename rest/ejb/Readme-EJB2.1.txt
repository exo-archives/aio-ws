For launch examples do next:

NOTE: This example work with EJB2.1.

1. Compile project connector21 and put exo.simple.rest.ejbconnector21-0.1.jar in JONAS_ROOT/ejbjars and deploy
   it by command:
   
     'jonas admin -a exo.simple.rest.ejbconnector21-0.1.jar'
     
   Then run jonas admin console: jonas admin and run next command jndiname. You should see  your bean in the list.

2. Compile example/simple-service and put exo.simple.rest.service-0.1.jar in lib directory of jonas.
   NOTE: File exo.rest.core-0.1.jar should be present!

3. Compile example/client21. Run example by command run.sh or run.bat.
   NOTE: The variable JONAS_ROOT should be set!

   
