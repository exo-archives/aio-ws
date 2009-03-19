To deploy a WS sample application on a server use a comman "mvn clean install -f MODULE_XML_FILE antrun:run".

Where MODULE_XML_FILE is one of the files:
  product-exo-ws-as-jboss.xml
  product-exo-ws-as-jonas.xml
  product-exo-ws-as-tomcat6.xml

For example, to deploy on JOnAS use: 
"mvn clean install -f product-exo-ws-as-jonas.xml antrun:run"


Run and browse:

/java/exo-working/exo-tomcat/bin$ ./eXo.sh run

Application available at http://localhost:8080/ws-examples/