#!/usr/bin/env pwsh

# -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=8888 -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=8888 -Djava.net.useSystemProxies=true
java -jar target/java-datalake-startup-perf-g2-1.0-SNAPSHOT-jar-with-dependencies.jar $args
