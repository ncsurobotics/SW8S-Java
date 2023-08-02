v4l2-ctl -d /dev/video0 -c exposure_auto=1
v4l2-ctl -d /dev/video0 -c exposure_absolute=$1
