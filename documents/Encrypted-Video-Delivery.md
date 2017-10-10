## What is encryption?
Encryption is the process of encoding messages or information in such a way that only authorized parties can read it. Encryption does not of itself prevent interception, but denies the message content to the interceptor. Usually encryption involves some secret key and a complex algorithm encoding the data.
There exists many standard encryption algorithms broadly grouped symmetric and asymmetric algorithms. The grouping mainly come from the keys being used and how they are distributed by algorithm. 
In 1 line
  - **Symmetric** - only 1 key is used for both encryption and decryption end
  - **Asymmetric** - 2 different keys are used

## HLS supported Encryption:
As per HLS specification it only supports AES-128 encryption. The Advanced Encryption Standard (AES) is an example of a block cipher, which encrypts (and decrypts) data in fixed-size blocks. AES-128 uses a key length of 128 bits (16 bytes).

HLS uses AES in cipher block chaining (**CBC**) mode. This means each block is encrypted using the cipher text of the preceding block, but this gives us a problem: how do we encrypt the first block? There is no block before it! To get around this problem we use what is known as an initialisation vector (IV). In this instance, it’s a 16-byte random value that is used to initialise the encryption process. It doesn’t need to be kept secret for the encryption to be secure.


# Steps - Create Encrypted HLS:

   1. Get a 128 bits (16 bytes) key using openssl

```
$ openssl rand 16 > encyption.key
```


   2. Create a 'hls_key_info_file' for telling 'FFMPEG' process to create 'Encrypted HLS'. The key_info_file must contain 3 lines as below

```
Key URI
Path to key file
IV (optional)


**Example:**  create a file 'encryption.keyinfo'


https://secure.ventunotech.com/encyption.key
/home/dev/encyption.key
ecd0d06eaf884d8226c33928e87efa31

```

   3. Run 'FFMPEG' with following options to encrypt the video segments:

```
ffmpeg -y \
    -i <<<INPUT_VIDEO_FILE>>> \
    -hls_time 10 \
    -hls_key_info_file encryption.keyinfo \
    -hls_playlist_type vod \
    -hls_segment_filename "segment%d.ts" \
    playlist_index.m3u8
```


   4. On success, the above step will generate 
  - A playlist with keyfile location details and segments details (playlist_index.m3u8)
  - set of encrypted segments (segment0.ts, segment1.ts,...)

The playlist will look like below:

```

#EXTM3U
#EXT-X-VERSION:3
#EXT-X-TARGETDURATION:9
#EXT-X-MEDIA-SEQUENCE:0
#EXT-X-PLAYLIST-TYPE:VOD
#EXT-X-KEY:METHOD=AES-128,URI="https://secure.ventunotech.com/encyption.key",IV=ecd0d06eaf884d8226c33928e87efa31
#EXTINF:9.66666
segment0.ts
#EXTINF:9.66666
segment1.ts
...
```

## Additional Security: 
The player will retrieve the key from URI location to decrypt the media segments. 
To protect the key from eavesdroppers it should be served over HTTPS. 
We may have to implement some of authentication mechanism to restrict, who has access to the key. (some kind one-time tokens for user-session)


NOTE: Even though HLS supports encryption, which provides some sort of content protection, it isn’t a full DRM solution.


## Reference:
- https://en.wikipedia.org/wiki/Encryption
- http://hlsbook.net/how-to-encrypt-hls-video-with-ffmpeg/
- https://dryize.wordpress.com/2014/04/02/protected-hls-using-ffmpeg-and-openssl/
- http://stackoverflow.com/questions/34424746/how-to-use-ffmpeg-encrypt-aes-128-hls-m3u8-playlist

