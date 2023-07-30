# SW8S-Java
Java code for operating Seawolf-8, an autonomous submarine built by AquaPack Robotics at NC State.

# Operation
## Local (Linux machine)
* Make local copies of dependencies: ```bash
./gradlew sync
```
* Sync files and time on Jetson, build jar remotely: ```bash
./gradlew push
```
* Full build (including zip): ```bash
./gradlew build
```
* Test: ```bash
./gradlew test
```
* Play video streams: ```bash
util/playstreams.sh
```
    * RTSP streams take a bit of time to start up, may need to cancel early and try multiple times
* Sync logs from Jetson (only works after time sync): ```bash
rsync -av sw8@192.168.2.5:/mnt/data/ TARGET
```

## Remote (Jetson Nano)
* Connect: ```bash
ssh sw8@192.168.2.5
```
* Go set read-write and go to code directory: ```bash
rw && cd ~/SW8S-Java
```
* Build program: ```bash
./gradlew build --offline
```
* Run program: ```bash
./gradlew run --offline --args='TAG --MISSION'
```
    * MISSION values:
        * wait_arm
        * path
        * buoy
        * bin
        * octagon
        * gate (NOTE: currently mimics "path", but records gate model data)
* Start rtsp server: ```bash
systemctl --user start rtsp
```
    * Check rtsp server status: ```bash
    systemctl --user status rtsp
    ```
* Clear logs: ```bash
rm -rf /mnt/data/*
```
