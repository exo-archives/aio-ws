#!/bin/sh

mvn clean install

if ( test -z "$JONAS_ROOT" ); then
  echo "ERROR: set \$JONAS_ROOT first"
  exit 1
fi

export PATH=$PATH:$JONAS_ROOT/bin/unix

jclient -Djava.security.auth.login.config=jaas.conf \
-cp $JONAS_ROOT/lib/apps/exo.kernel.component.common-trunk.jar:\
$JONAS_ROOT/lib/apps/exo.core.component.organization.api-trunk.jar \
-Djava.security.manager=default \
-Djava.security.policy=$JONAS_ROOT/conf/java.policy \
target/exo.ws.rest.ejbconnector21.client21-jonas-trunk.jar

