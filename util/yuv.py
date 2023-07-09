#!/usr/bin/env python3

import cv2
import sys
import os

os.makedirs('/tmp/yuv', exist_ok=True)
for imgName in sys.argv[1:]:
    os.makedirs('/tmp/yuv/' + os.path.dirname(imgName), exist_ok=True)
    img = cv2.imread(sys.argv[1])
    yuv = cv2.cvtColor(img, cv2.COLOR_BGR2YUV)
    y, u, v = cv2.split(yuv)

    cleanName = imgName.replace('.jpeg', '').replace('.jpg', '')
    cv2.imwrite('/tmp/yuv/' + cleanName + '_y.jpeg', y)
    cv2.imwrite('/tmp/yuv/' + cleanName + '_u.jpeg', u)
    cv2.imwrite('/tmp/yuv/' + cleanName + '_v.jpeg', v)
