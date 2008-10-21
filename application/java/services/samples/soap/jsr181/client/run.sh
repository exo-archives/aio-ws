export EXO_SERVER="../../../exo-tomcat"

if ( test -z "$EXO_SERVER" ); then                                                                                           
  echo "ERROR: set \$EXO_SERVER first"                                                                                       
  exit 1                                                                                                                     
fi 

/home/andrew/lib/java/bin/java -cp ./exo.ws.soap.cxf.jsr181.samples.client-trunk.jar:\
$EXO_SERVER/lib/xfire-all-1.2.6.jar:\
$EXO_SERVER/lib/commons-logging-1.0.4.jar:\
$EXO_SERVER/lib/stax-api-1.0.jar:\
$EXO_SERVER/lib/wsdl4j-1.6.1.jar:\
$EXO_SERVER/lib/activation-1.1.jar:\
$EXO_SERVER/lib/jdom-1.0.jar:\
$EXO_SERVER/lib/xfire-jsr181-api-1.0.jar:\
$EXO_SERVER/lib/jaxb-api-2.0.jar:\
$EXO_SERVER/lib/commons-httpclient-3.1.jar:\
$EXO_SERVER/lib/commons-codec-1.2.jar:\
$EXO_SERVER/lib/sjsxp-1.0.jar org.exoplatform.services.ws.soap.jsr181.TicketOrderService_TicketOrderServicePort_Client.Main

