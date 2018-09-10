# RAWAudioData
收集双麦原始数据，用来分析双麦音源频谱

andorid 系统使用原生AudioRecord 录音获取的pcm数据，仍然还是做过双通道数据处理。 pcm数据无法用来做双麦数据分析。 
使用tinycap 录音则能够对双麦数据区分。

usage:
tinycap file.wav [-D card] [-d device] [-c channels] [-r rate] [-b bits] [-p period_size] [-n n_periods]


！[image](https://github.com/HelloWorld1024/RAWAudioData/edit/master/audioRecorderPcmData.png)

audiorecord 录音频谱数据


![image](https://github.com/HelloWorld1024/RAWAudioData/edit/master/tinycapData.png)
