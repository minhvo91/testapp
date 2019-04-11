# KIXEYE Test Application

This application is provided as a dependency for a DevOps interview challenge.

## Dependencies

* Java Runtime Environment: `version 1.8.0_121`
* Redis server available at: `redis.local:6379` (a working redis.conf file is included in ./resources)
* Logging: local file system at `/var/log/testapp.log`

## Run

1. extract the .jar file from `testapp.tar` 
1. the following will run the application (replacing the path to the test jar with an actual path):
 
`java -cp ./testapp.jar kixeye.testapp.Main`
