# BLE-Connect
# BLE连接（用于产测）
又是一个在原有搜索的代码上进行的更改哈哈哈（披皮工程2ψ(._. )>）  
主要思路：思路：点击按钮后自动搜索附近设备，时间5秒，通过名字过滤无关产测的设备，通过rssi过滤掉信号差的设备，将设备保存至一个列表中，进行循环连接  
连接第一台设备后发送一条指令，类似于语音播报的指令，发送指令后，设备会上传一条信息上来，收到传上来的设备才自动断开，然后才自动连接第二台设备  
点击开始按钮----自动连接A设备----发送指令----收到回复----自动断开----自动连接B设备-----发送指令  
