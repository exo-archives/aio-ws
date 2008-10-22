export EXO_SERVER="$HOME/exo/exo-working/exo-tomcat"
echo $EXO_SERVER

if ( test -z "$EXO_SERVER" ); then                                                                                           
  echo "ERROR: set \$EXO_SERVER first"                                                                                       
  exit 1                                                                                                                     
fi 

/home/andrew/lib/java/bin/java -cp ./exo.ws.soap.cxf.jsr181.samples.client-trunk.jar:\
$EXO_SERVER/lib/geronimo-jaxws_2.1_spec-1.0.jar:\
$EXO_SERVER/lib/cxf-api-2.1.2.jar:\
$EXO_SERVER/lib/cxf-rt-transports-http-2.1.2.jar:\
$EXO_SERVER/lib/cxf-tools-wsdlto-core-2.1.2.jar:\
$EXO_SERVER/lib/cxf-tools-java2ws-2.1.2.jar:\
$EXO_SERVER/lib/cxf-rt-frontend-jaxws-2.1.2.jar:\
$EXO_SERVER/lib/jaxb-api-2.1.jar:\
$EXO_SERVER/lib/cxf-common-utilities-2.1.2.jar:\
$EXO_SERVER/lib/cxf-rt-core-2.1.2.jar:\
$EXO_SERVER/lib/annotations-api.jar:\
$EXO_SERVER/lib/xml-resolver-1.1.jar:\
$EXO_SERVER/lib/cxf-rt-frontend-simple-2.1.2.jar:\
$EXO_SERVER/lib/XmlSchema-1.4.2.jar:\
$EXO_SERVER/lib/cxf-rt-bindings-soap-2.1.2.jar:\
$EXO_SERVER/lib/cxf-tools-common-2.1.2.jar:\
$EXO_SERVER/lib/cxf-rt-databinding-jaxb-2.1.2.jar:\
$EXO_SERVER/lib/jaxb-impl-2.1.7.jar:\
$EXO_SERVER/lib/geronimo-ws-metadata_2.0_spec-1.1.2.jar:\
$EXO_SERVER/lib/cxf-rt-ws-addr-2.1.2.jar:\
$EXO_SERVER/lib/cxf-rt-ws-addr-2.1.2.jar:\
$EXO_SERVER/lib/saaj-api-1.3.jar:\
$EXO_SERVER/lib/saaj-impl-1.3.jar:\
$EXO_SERVER/lib/cxf-rt-transports-http-jetty-2.1.2.jar:\
$EXO_SERVER/lib/commons-logging-1.0.4.jar:\
$EXO_SERVER/lib/stax-api-1.0.jar:\
$EXO_SERVER/lib/wsdl4j-1.6.1.jar:\
$EXO_SERVER/lib/activation-1.1.jar:\
$EXO_SERVER/lib/jdom-1.0.jar:\
$EXO_SERVER/lib/xfire-jsr181-api-1.0.jar:\
$EXO_SERVER/lib/jaxb-api-2.0.jar:\
$EXO_SERVER/lib/commons-httpclient-3.1.jar:\
$EXO_SERVER/lib/commons-codec-1.2.jar:\
$EXO_SERVER/lib/sjsxp-1.0.jar org.exoplatform.services.ws.soap.jsr181.TicketOrderService_TicketOrderServicePort_Client

