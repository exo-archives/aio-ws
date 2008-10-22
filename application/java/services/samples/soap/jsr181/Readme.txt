If you want client code also, run command:
mvn clean install -Dexo.test.skip=false antrun:run

After build edit wsdl file at target/generated/wsdl for correct 
address where SOAP service running.

For example:
<soap:address location="http://localhost:8080/ws-examples/soap/services/TicketOrderService"/>


