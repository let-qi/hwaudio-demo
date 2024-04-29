# demo介绍
 - 包含录音和播放两个演示代码， 大家可以在特定的位置替换为自己的代码，或者只借鉴创建录音和播放实例的代码。

## 录音
 - 实现功能简介：调用录音API接口时，bypass掉底层降噪等算法，获得原始数据流录音
 - 数据格式说明：创建一个录音实例，并将录音流存储为pcm文件，代码默认文件格式（采样率：48kHz，通道：2，位深：16bit）
 - 文件路径位置：src/main/java/com/hwaudio/record/AudioRecorderBase.java
 - 创建录音位置：代码76行，创建录音的audioSorce要设置为MediaRecorder.AudioSource.VOICE_RECOGNITION
 - 特别注意位置：23行要修改为自己要存储的路径，29行mBufferSize自行设置

## 播放
 - 实现功能简介：调用播放API接口时，bypass掉底层音效等算法，实现原始流数据播放
 - 数据格式说明：创建一个播放实例，读取本地pcm文件，送给播放实例播放，代码默认pcm文件格式（采样率：48kHz，通道：2，位深：16bit）
 - 文件路径位置：src/main/java/com/hwaudio/player/AudioPlayerBase.java
 - 创建播放位置：代码61行，创建播放流类型为：AudioManager.STREAM_NOTIFICATION
 - 特别注意位置：17行修改为自己要播放的pcm文件路径
