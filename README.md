# Calculating GatewaySender event queue, transmission and processing times
## Description

This project provides a **TimingGatewayEventFilter** that calculates queue, transmission and total processing times for GatewaySender events using a custom Statistics object.

The **GatewaySenderQueueStatistics** creates a Statistics object that defines the following statistics:

- **queuedEvents** - the number of events queued by the gateway sender
- **transmittedEvents** - the number of events transmitted by the gateway sender
- **acknowledgedEvents** - the number of events acknowledged by the gateway sender
- **minimumQueueTime** - the minimum time an event spent in the gateway sender queue
- **maximumQueueTime** - the maximum time an event spent in the gateway sender queue
- **totalQueueTime** - the total time events spent in the gateway sender queue
- **minimumTransmissionTime** - the minimum time an event spent in transmission including processing time on the remote site
- **maximumTransmissionTime** - the maximum time an event spent in transmission including processing time on the remote site
- **totalTransmissionTime** - the total time events spent in transmission including processing time on the remote site
- **minimumProcessingTime** - the minimum time an event spent being processed including queue time on the local site and processing time on the remote site
- **maximumProcessingTime** - the maximum time an event spent being processed including queue time on the local site and processing time on the remote site
- **totalProcessingTime** - the total time events spent being processed including queue time on the local site and processing time on the remote site

## Initialization
Modify the **GEODE** environment variable in the *setenv.sh* script to point to a Geode installation directory.
## Build
Build the Spring Boot Client Application and Geode Server Function classes using gradle like:

```
./gradlew clean jar bootJar
```
## Run Example
### Start and Configure WAN Sites
Start and configure the locator and two servers in two sites using the *startsites.sh* script like:

```
./startsites.sh
```
### Load Entries
Run the client to load Trade instances forever using the *runclient.sh* script like below.

The parameters are:

- operation (load-forever)
- number of keys (10000)

```
./runclient.sh load-forever 10000
```
### Shutdown WAN Sites
Execute the *shutdownsites.sh * script to shutdown the servers and locators in both sites like:

```
./shutdownsites.sh 
```
### Remove Locator and Server Files
Execute the *cleanupfiles.sh* script to remove the server and locator files like:

```
./cleanupfiles.sh
```
## Example Sample Output
### Start and Configure WAN Sites
Sample output from the *startsites.sh* script is:

```
./startsites.sh 
1. Executing - set variable --name=APP_RESULT_VIEWER --value=any

Value for variable APP_RESULT_VIEWER is now: any.

2. Executing - start locator --name=locator-ln --port=10331 --locators=localhost[10331] --mcast-port=0 --J=-Dgemfire.remote-locators=localhost[10332] --J=-Dgemfire.distributed-system-id=1 --J=-Dgemfire.jmx-manager-start=true --J=-Dgemfire.jmx-manager-http-port=8081 --J=-Dgemfire.jmx-manager-port=1091

..........................
Locator in <working-directory>/locator-ln on 192.168.1.15[10331] as locator-ln is currently online.
Process ID: 54842
Uptime: 35 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/locator-ln/locator-ln.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

Successfully connected to: JMX Manager [host=192.168.1.15, port=1091]

Cluster configuration service is up and running.

3. Executing - start server --name=server-ln-1 --locators=localhost[10331] --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

........
Server in <working-directory>/server-ln-1 on 192.168.1.15[59912] as server-ln-1 is currently online.
Process ID: 54861
Uptime: 6 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-ln-1/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

4. Executing - start server --name=server-ln-2 --locators=localhost[10331] --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

.............
Server in <working-directory>/server-ln-2 on 192.168.1.15[59942] as server-ln-2 is currently online.
Process ID: 54862
Uptime: 9 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-ln-2/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

5. Executing - deploy --jar=server/build/libs/server-0.0.1-SNAPSHOT.jar

  Member    |       Deployed JAR        | Deployed JAR Location
----------- | ------------------------- | ------------------------------------------------------------
server-ln-1 | server-0.0.1-SNAPSHOT.jar | <working-directory>/server-ln-1/server-0.0.1-SNAPSHOT.v1.jar
server-ln-2 | server-0.0.1-SNAPSHOT.jar | <working-directory>/server-ln-2/server-0.0.1-SNAPSHOT.v1.jar

6. Executing - create gateway-sender --id=ny --parallel=true --remote-distributed-system-id=2 --gateway-event-filter=example.server.filter.TimingGatewayEventFilter

  Member    | Status | Message
----------- | ------ | -------------------------------------------
server-ln-1 | OK     | GatewaySender "ny" created on "server-ln-1"
server-ln-2 | OK     | GatewaySender "ny" created on "server-ln-2"

Cluster configuration for group 'cluster' is updated.

7. Executing - sleep --time=5


8. Executing - create region --name=Trade --type=PARTITION_REDUNDANT --gateway-sender-id="ny"

  Member    | Status | Message
----------- | ------ | ----------------------------------------
server-ln-1 | OK     | Region "/Trade" created on "server-ln-1"
server-ln-2 | OK     | Region "/Trade" created on "server-ln-2"

Cluster configuration for group 'cluster' is updated.

9. Executing - disconnect

Disconnecting from: 192.168.1.15[1091]
Disconnected from : 192.168.1.15[1091]

10. Executing - start locator --name=locator-ny --port=10332 --locators=localhost[10332] --mcast-port=0 --J=-Dgemfire.remote-locators=localhost[10331] --J=-Dgemfire.distributed-system-id=2 --J=-Dgemfire.jmx-manager-start=true --J=-Dgemfire.jmx-manager-http-port=8082 --J=-Dgemfire.jmx-manager-port=1092

.................
Locator in <working-directory>/locator-ny on 192.168.1.15[10332] as locator-ny is currently online.
Process ID: 54869
Uptime: 19 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/locator-ny/locator-ny.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

Successfully connected to: JMX Manager [host=192.168.1.15, port=1092]

Cluster configuration service is up and running.

11. Executing - start server --name=server-ny-1 --locators=localhost[10332] --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

........
Server in <working-directory>/server-ny-1 on 192.168.1.15[60013] as server-ny-1 is currently online.
Process ID: 54888
Uptime: 6 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-ny-1/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

12. Executing - start server --name=server-ny-2 --locators=localhost[10332] --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

........
Server in <working-directory>/server-ny-2 on 192.168.1.15[60044] as server-ny-2 is currently online.
Process ID: 54894
Uptime: 5 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-ny-2/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

13. Executing - create gateway-receiver

  Member    | Status | Message
----------- | ------ | ----------------------------------------------------------------------------------
server-ny-1 | OK     | GatewayReceiver created on member "server-ny-1" and will listen on the port "5466"
server-ny-2 | OK     | GatewayReceiver created on member "server-ny-2" and will listen on the port "5419"

Cluster configuration for group 'cluster' is updated.

14. Executing - create region --name=Trade --type=PARTITION_REDUNDANT

  Member    | Status | Message
----------- | ------ | ----------------------------------------
server-ny-1 | OK     | Region "/Trade" created on "server-ny-1"
server-ny-2 | OK     | Region "/Trade" created on "server-ny-2"

Cluster configuration for group 'cluster' is updated.

15. Executing - disconnect

Disconnecting from: 192.168.1.15[1092]
Disconnected from : 192.168.1.15[1092]

************************* Execution Summary ***********************
Script file: startsites.gfsh

Command-1 : set variable --name=APP_RESULT_VIEWER --value=any
Status    : PASSED

Command-2 : start locator --name=locator-ln --port=10331 --locators=localhost[10331] --mcast-port=0 --J=-Dgemfire.remote-locators=localhost[10332] --J=-Dgemfire.distributed-system-id=1 --J=-Dgemfire.jmx-manager-start=true --J=-Dgemfire.jmx-manager-http-port=8081 --J=-Dgemfire.jmx-manager-port=1091
Status    : PASSED

Command-3 : start server --name=server-ln-1 --locators=localhost[10331] --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status    : PASSED

Command-4 : start server --name=server-ln-2 --locators=localhost[10331] --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status    : PASSED

Command-5 : deploy --jar=server/build/libs/server-0.0.1-SNAPSHOT.jar
Status    : PASSED

Command-6 : create gateway-sender --id=ny --parallel=true --remote-distributed-system-id=2 --gateway-event-filter=example.server.filter.TimingGatewayEventFilter
Status    : PASSED

Command-7 : sleep --time=5
Status    : PASSED

Command-8 : create region --name=Trade --type=PARTITION_REDUNDANT --gateway-sender-id="ny"
Status    : PASSED

Command-9 : disconnect
Status    : PASSED

Command-10 : start locator --name=locator-ny --port=10332 --locators=localhost[10332] --mcast-port=0 --J=-Dgemfire.remote-locators=localhost[10331] --J=-Dgemfire.distributed-system-id=2 --J=-Dgemfire.jmx-manager-start=true --J=-Dgemfire.jmx-manager-http-port=8082 --J=-Dgemfire.jmx-manager-port=1092
Status     : PASSED

Command-11 : start server --name=server-ny-1 --locators=localhost[10332] --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status     : PASSED

Command-12 : start server --name=server-ny-2 --locators=localhost[10332] --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status     : PASSED

Command-13 : create gateway-receiver
Status     : PASSED

Command-14 : create region --name=Trade --type=PARTITION_REDUNDANT
Status     : PASSED

Command-15 : disconnect
Status     : PASSED
```
### Load Entries
Sample output from the *runclient.sh* script is:

```
./runclient.sh load-forever 10000
2020-11-27 14:53:16.608  INFO 69082 --- [           main] example.client.Client                    : Starting Client on ...
...
2020-11-27 14:53:21.356  INFO 69082 --- [           main] example.client.Client                    : Started Client in 5.405 seconds (JVM running for 6.11)
2020-11-27 14:53:21.359  INFO 69082 --- [           main] example.client.service.TradeService      : Loading 10000 trades forever
2020-11-27 14:53:29.470  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=1000) in 8111 ms
2020-11-27 14:53:31.740  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=2000) in 2269 ms
2020-11-27 14:53:33.908  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=3000) in 2167 ms
2020-11-27 14:53:35.839  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=4000) in 1931 ms
2020-11-27 14:53:37.174  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=5000) in 1334 ms
2020-11-27 14:53:38.604  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=6000) in 1430 ms
2020-11-27 14:53:39.950  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=7000) in 1345 ms
2020-11-27 14:53:41.124  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=8000) in 1174 ms
2020-11-27 14:53:42.365  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=9000) in 1240 ms
2020-11-27 14:53:43.556  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=10000) in 1191 ms
...
2020-11-27 14:55:22.967  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=241000) in 330 ms
2020-11-27 14:55:23.300  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=242000) in 333 ms
2020-11-27 14:55:23.659  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=243000) in 359 ms
2020-11-27 14:55:24.003  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=244000) in 344 ms
2020-11-27 14:55:24.384  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=245000) in 381 ms
2020-11-27 14:55:24.830  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=246000) in 446 ms
2020-11-27 14:55:25.244  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=247000) in 414 ms
2020-11-27 14:55:25.730  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=248000) in 486 ms
2020-11-27 14:55:26.078  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=249000) in 348 ms
2020-11-27 14:55:26.425  INFO 69082 --- [           main] example.client.service.TradeService      : Loaded 1000 trades (total=250000) in 347 ms
...
```
### Shutdown Sites
Sample output from the * shutdownsites.sh* script is:

```
./shutdownsites.sh 

(1) Executing - connect --locator=localhost[10331]

Connecting to Locator at [host=localhost, port=10331] ..
Connecting to Manager at [host=192.168.1.10, port=1091] ..
Successfully connected to: [host=192.168.1.10, port=1091]


(2) Executing - shutdown --include-locators=true

Shutdown is triggered


(1) Executing - connect --locator=localhost[10332]

Connecting to Locator at [host=localhost, port=10332] ..
Connecting to Manager at [host=192.168.1.10, port=1092] ..
Successfully connected to: [host=192.168.1.10, port=1092]


(2) Executing - shutdown --include-locators=true

Shutdown is triggered
```
