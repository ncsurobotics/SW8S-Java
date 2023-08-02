v4l2-ctl -d /dev/video1 -c exposure_auto=1
v4l2-ctl -d /dev/video1 -c exposure_absolute=$1
