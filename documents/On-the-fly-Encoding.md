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
