#!/bin/sh

if ( test -z "$JONAS_ROOT" ); then
  echo "ERROR: set \$JONAS_ROOT first"
  exit 1
fi

export PATH=$PATH:$JONAS_ROOT/bin/unix
jclient -Djava.security.manager=default -Djava.security.policy=$JONAS_ROOT/conf/java.policy target/exo.simple.rest.service.client21-jonas-0.1.jar
