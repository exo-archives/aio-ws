if ( test -z "$M2_REPO" ); then                                                                                           
  echo "ERROR: set \$M2_REPO first"                                                                                       
  exit 1                                                                                                                     
fi 

echo $M2_REPO

java -cp ./target/exo.ws.application.soap.cxf.samples.client-2.0.1.jar:\
$M2_REPO/org/apache/cxf/cxf-api/2.1.2/cxf-api-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-rt-transports-http/2.1.2/cxf-rt-transports-http-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-tools-wsdlto-core/2.1.2/cxf-tools-wsdlto-core-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-tools-java2ws/2.1.2/cxf-tools-java2ws-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-rt-frontend-jaxws/2.1.2/cxf-rt-frontend-jaxws-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-common-utilities/2.1.2/cxf-common-utilities-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-rt-core/2.1.2/cxf-rt-core-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-rt-bindings-soap/2.1.2/cxf-rt-bindings-soap-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-tools-common/2.1.2/cxf-tools-common-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-rt-databinding-jaxb/2.1.2/cxf-rt-databinding-jaxb-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-rt-transports-http-jetty/2.1.2/cxf-rt-transports-http-jetty-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-rt-frontend-simple/2.1.2/cxf-rt-frontend-simple-2.1.2.jar:\
$M2_REPO/org/apache/cxf/cxf-rt-ws-addr/2.1.2/cxf-rt-ws-addr-2.1.2.jar:\
$M2_REPO/org/apache/geronimo/specs/geronimo-jaxws_2.1_spec/1.0/geronimo-jaxws_2.1_spec-1.0.jar:\
$M2_REPO/org/apache/geronimo/specs/geronimo-ws-metadata_2.0_spec/1.1.2/geronimo-ws-metadata_2.0_spec-1.1.2.jar:\
$M2_REPO/org/apache/tomcat/annotations-api/6.0.18/annotations-api-6.0.18.jar:\
$M2_REPO/xml-resolver/xml-resolver/1.1/xml-resolver-1.1.jar:\
$M2_REPO/org/apache/ws/commons/schema/XmlSchema/1.4.2/XmlSchema-1.4.2.jar:\
$M2_REPO/javax/xml/bind/jaxb-api/2.1/jaxb-api-2.1.jar:\
$M2_REPO/com/sun/xml/bind/jaxb-impl/2.1.7/jaxb-impl-2.1.7.jar:\
$M2_REPO/javax/xml/soap/saaj-api/1.3/saaj-api-1.3.jar:\
$M2_REPO/com/sun/xml/messaging/saaj/saaj-impl/1.3/saaj-impl-1.3.jar:\
$M2_REPO/commons-logging/commons-logging-api/1.0.4/commons-logging-1.0.4.jar:\
$M2_REPO/javax/xml/stream/stax-api/1.0/stax-api-1.0.jar:\
$M2_REPO/wsdl4j/wsdl4j/1.6.1/wsdl4j-1.6.1.jar:\
$M2_REPO/activation/activation/1.1/activation-1.1.jar:\
$M2_REPO/org/jdom/jdom/1.0/jdom-1.0.jar:\
$M2_REPO/commons-httpclient/commons-httpclient/3.1/commons-httpclient-3.1.jar:\
$M2_REPO/commons-codec/commons-codec/1.2/commons-codec-1.2.jar:\
$M2_REPO/com/sun/xml/stream/sjsxp/1.0/sjsxp-1.0.jar \
org.exoplatform.services.ws.soap.jsr181.TicketOrderService_TicketOrderServicePort_Client
#./src/main/java/wsdl/TicketOrderService.wsdl

