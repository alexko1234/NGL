#!/bin/sh
${JAVA_HOME}/bin/java -Xmx1024m -DbirdsProjectConfiguration=deploy-sub-auto/birdsProject.properties -classpath deploy-sub-auto/birds-client.jar fr.genoscope.lis.devsi.birds.api.client.BirdsLineCommands -c $@
