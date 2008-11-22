#!/bin/sh

VERSION=1.3.1

if ( test -z "$JONAS_ROOT" ); then
  echo "ERROR: set \$JONAS_ROOT first"
  exit 1
fi

$JAVA_HOME/bin/java -Djava.security.manager=default  \
-Djava.security.policy=$JONAS_ROOT/conf/java.policy \
-cp $JONAS_ROOT/lib/apps/exo.core.component.security.core-$VERSION.jar:\
$JONAS_ROOT/lib/apps/exo.ws.commons-$VERSION.jar:$JONAS_ROOT/lib/client.jar \
org.objectweb.jonas.client.ClientContainer rest-ejb-connector-2_1.ear
