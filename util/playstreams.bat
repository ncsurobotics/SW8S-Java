start /b mpv.exe --title=Cam0 --no-cache --untimed --profile=low-latency --no-correct-pts --fps=60 --osc=no rtsp://192.168.2.5:8554/cam0 &
mpv.exe --title=Cam1 --no-cache --untimed --profile=low-latency --no-correct-pts --fps=60 --osc=no rtsp://192.168.2.5:8554/cam1
