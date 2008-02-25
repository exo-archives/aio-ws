For launch examples do next:

NOTE: This example works with EJB2.1.

1. Compile project 'connector21' and put 'exo.simple.rest.ejbconnector21-${org.exoplatform.ws.version}.jar' in
   JONAS_ROOT/ejbjars and deploy it by command:
   
     './jonas admin -a exo.ws.rest.ejbconnector21.client21-jonas-${org.exoplatform.ws.version}.jar'
     './jonas admin -j'
   
   You should see your bean in the list. Alternatively, you can also drop the EJB JAR in apps/autoload/exoplatform.ear
   and add the corresponding entry in apps/autoload/exoplatform.ear/META-INF/application.xml:

   <module>
     <ejb>exo.ws.rest.ejbconnector21-trunk.jar</ejb>
   </module>

   This EJB is responsible for allowing to access any REST-enabled service deployed in eXo container.

2. Make sure that exo.core.component.organization.api-${org.exoplatform.core.version}.jar exists in JONAS_ROOT/lib/apps.
   Possibly, you may need to modify the content of run.sh or run.bat to reflect the exact version of that jar file.

3. Compile 'simple-service' and put 'exo.ws.simple.rest.service-${org.exoplatform.ws.version}.jar' in lib directory of jonas.
   NOTE: File 'exo.ws.rest.core-${org.exoplatform.ws.version}.jar' should be present!

4. Compile 'client21'. Run example by command run.sh or run.bat.
   NOTE: The variable JONAS_ROOT should be set!

   
