
## codec
H.264 is video compression standard (mostly used for record, store & distribute video content)

## profile 
This will inform codec to use some set of features of algorithm. Normally higher profiles require more CPU power to decode and are able to generate better looking videos at same bitrate. We should always choose the best profile for the targeted devices. 
Following table gives an idea.

| Device |	Max supported |
|-----|-----|
| Desktop browsers, iPhone 4S+, iPad 2+, Android 4.x+ tablets, Xbox 360, Playstation 3 |	High profile |
| iPhone 3GS, iPhone 4, iPad, low-end Android phones |	Main profile |
| iPhone, iPhone 3G, old low-end Android devices, other embedded players |	Baseline profile |

There are other high profiles specific to HD devices.

## Bitrate
It is the no.of bits transferred/processed per unit time (per second). 
In current context - It is the no.of bits required to represent 1 second of video data. 

Following bit rate of youtube gives an idea

| Bitrate |	Video profile |
|-----|-----|
| 400 kbit/s  | YouTube 240p videos (using H.264) |
| 750 kbit/s   | YouTube 360p videos (using H.264) |
| 1 Mbit/s      | YouTube 480p videos (using H.264) |
| 2.5 Mbit/s   | YouTube 720p videos (using H.264) |
| 4.5 Mbit/s   | YouTube 1080p videos (using H.264) |


## Bitrate extra params (maxrate and -bufsize) 
This will inform codec to build video in a way suitable for streaming through web. 
Very useful for web - setting this to bitrate and 2x bitrate gives good results.

## resize video (-vf scale)
This resizes video to desired resolution (normally done for increasing the sharpness of video). 

'720:480' would resize video to 720x480,

'-1' means resize so the aspect ratio is same. 

Usually we set only height of the video like  
- for 380p:   'scale=-1:380' 
- for 720p:   'scale=-1:720'


On-the-fly Encoding
==========================

### Algorithm: (On-the-fly video and audio encoding)

1. As soon as a chunk of 10MB is received @ server follow below steps
2. In case of first chunk,
    - Get video details(video-bitrate & audio-bitrate) 
    - Calculate no.of seconds of video available in a chunk of 10 MB
    - Fix time-slice for encoding based on above calculation
3. Calculate start-time and end-time for the current chunk and slice-time
4. Call ffmpeg for encoding with the partial uploaded video, start-time, end-time
5. If 'encoding process' has some error - Abort the chunk encoding and fall back to full conversion
6. else 'encoding process' is success
    - Mark the chunk as completed
    - Append the converted video to the video_list
7. In case of last chunk, along with the above steps call a new ffmpeg to concat all the chunk videos in the list
8. If there is an error in above step - Abort the chunk encoding and fall back to full conversion
