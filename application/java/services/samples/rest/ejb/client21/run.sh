#!/bin/sh

VERSION=1.2

if ( test -z "$JONAS_ROOT" ); then
  echo "ERROR: set \$JONAS_ROOT first"
  exit 1
fi

export PATH=$PATH:$JONAS_ROOT/bin/unix

#
jclient \
-cp $JONAS_ROOT/lib/apps/exo.core.component.security.core-2.1.jar \
-Djava.security.manager=default \
-Djava.security.policy=$JONAS_ROOT/conf/java.policy \
target/exo.ws.application.ejbconnector21.client21-$VERSION.jar

