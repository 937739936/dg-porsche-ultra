var id = 46;
var id2 = 100;
let str = [];
for(let i = 0; i < 300; i++) {
    let number = ++id2;
    var t = "INSERT INTO jade.t_device (id, device_id, name, protocol_type, channel_count, register_password, ip, port, transport, manufacturer, device_type, model, firmware, stream_mode, stream_url, enable, enable_audio, online, register_time, keepalive_time, remark, created_by, created_time, last_modified_by, last_modified_time) VALUES ("+(++id)+", '41010596001320000"+number+"', 'test"+number+"', 'RTMP', 1, null, null, null, null, 'NONE', 'IPC', null, null, null, null, 1, null, 0, '2025-10-23 18:51:51.693455', null, null, 4, '2025-10-23 18:51:51.699028', 4, '2025-10-23 18:51:51.699028');"
    var t2 = "INSERT INTO jade.t_device_channel (name, device_id, channel_id, enable, online, recording, ptz_type, leave_time, register_time, created_by, created_time, last_modified_by, last_modified_time) VALUES ('test"+number+"', '41010596001320000"+number+"', '41010596001320000"+number+"', 1, 0, 0, 'None', null, null, 4, '2025-10-23 18:51:51.700635', 4, '2025-10-23 18:51:51.700635');"
    str.push(t)
    str.push(t2)
}
console.log(str.join("\n"))
