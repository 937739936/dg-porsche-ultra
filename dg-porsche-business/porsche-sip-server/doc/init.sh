#!/bin/bash
http -vj :9000/admin/config/genDeviceId name=海康180枪机  manufacturer=HIKVISION   deviceType=IPC  registerPassword=123456 channelCount=1
http -vj :9000/admin/config/genDeviceId name=大华181枪机    manufacturer=DAHUATECH   deviceType=IPC  registerPassword=123456 channelCount=1
http -vj :9000/admin/config/genDeviceId name=海康182球机    manufacturer=HIKVISION   deviceType=IPC  registerPassword=123456 channelCount=1
http -vj :9000/admin/config/genDeviceId name=大华183枪机    manufacturer=HIKVISION   deviceType=IPC  registerPassword=123456 channelCount=1
http -vj :9000/admin/config/genDeviceId name="海康200录像机(16路)"  manufacturer=HIKVISION   deviceType=NVR  registerPassword=123456 channelCount=16
http -vj :9000/admin/config/genDeviceId name="宇视201录像机(6路)"  manufacturer=UNIVIEW   deviceType=NVR  registerPassword=123456 channelCount=6
http -vj :9000/admin/config/genDeviceId name="大华202录像机(4路)"  manufacturer=DAHUATECH   deviceType=NVR  registerPassword=123456 channelCount=4

