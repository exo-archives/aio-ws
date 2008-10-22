eXo.require("eXo.projects.Module") ;
eXo.require("eXo.projects.Product") ;

function getModule(params) {
  var module = new Module();

  module.version = "1.3" ;
  module.relativeMavenRepo =  "org/exoplatform/ws" ;
  module.relativeSRCRepo =  "ws/1.3" ;
  module.name =  "ws" ;

  module.commons = 
    new Project("org.exoplatform.ws", "exo.ws.commons", "jar", module.version);

  module.rest = 
    new Project("org.exoplatform.ws", "exo.rest.core", "jar", module.version).      
    addDependency(new Project("org.exoplatform.ws", "exo.ws.commons", "jar", module.version)).
    addDependency(new Project("commons-chain", "commons-chain", "jar", "1.0")) .
    addDependency(new Project("javax.xml.parsers", "jaxp-api", "jar", "1.4")) .
    addDependency(new Project("javax.xml.bind", "jaxp-api", "jar", "2.1")) .
    addDependency(new Project("com.sun.xml.bind", "jaxb-impl", "jar", "2.1.7")) .
    addDependency(new Project("com.sun.xml.parsers", "jaxp-ri", "jar", "1.4")) .
    addDependency(new Project("org.jvnet.jaxb2.maven2", "maven-jaxb2-plugin", "jar", "0.1"));
      
  module.soap = {};
  module.soap.jsr181 =
    new Project("org.exoplatform.ws", "exo.soap.cxf.jsr181", "jar", module.version).
    addDependency(new Project("picocontainer", "picocontainer", "jar", "1.1")) .
    addDependency(new Project("org.apache.cxf", "cxf-api", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-common-utilities", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-rt-bindings-soap", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-rt-core", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-rt-databinding-jaxb", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-rt-frontend-jaxws", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-rt-frontend-simple", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-rt-transports-http", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-rt-transports-http-jetty", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-rt-transports-local", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-rt-ws-addr", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.cxf", "cxf-tools-common", "jar", "2.1.2")) .
    addDependency(new Project("org.apache.ws.commons.schema", "XmlSchema", "jar", "1.4.2")) .
    addDependency(new Project("javax.activation", "activation", "jar", "1.1")) .
    addDependency(new Project("org.apache.geronimo.specs", "geronimo-ws-metadata_2.0_spec", "jar", "1.1.1")) .
    addDependency(new Project("javax.xml.ws", "jaxws-api", "jar", "2.1-1")) .
    addDependency(new Project("javax.xml.soap", "saaj-api", "jar", "1.3")) .
    addDependency(new Project("xml-resolver", "xml-resolver", "jar", "1.2")) .
    addDependency(new Project("stax", "stax-api", "jar", "1.0.1")) .
    addDependency(new Project("wsdl4j", "wsdl4j", "jar", "1.6.1")) .
    addDependency(new Project("jdom", "jdom", "jar", "1.0"));  
  
  return module;
}
