Recovering older data from Floppies
===================================
Recovering data from media is a non-trivial task. 
There is no magic - we need device (hardware) for reading the media.  

- We can find some of older **Pentium machines (II, III, IV)** with 5.25, 3.5 floppy drives
- Most of them have windows 98/xp working on them
- We can mount the Medias and retrieve data (files) directly
- write them to CDs or Pen drives or send emails and retrieve in new machines

If we cannot find older machines, 
- we can get external floppy disks for 3.5 in retail shops (at least in ebay/Amazon websites). 

Also, there are various vendors doing this **recovery of bits from older Medias as paid service**. 
They can recover from 5.25", 3.5" floppy disks and older zip-drives (http://floppydisk.com).

### FAT12 File System:

Once we have the bit stream, we need to retrieve the actual data from it. 
- Most of the floppies use the FAT file system. 
- A floppy disk has the same structures as a single hard disk volume. 
- Almost all the floppy disks are formatted in the FAT12 version of the FAT file system. 
- It uses a 12-bit binary number to hold the cluster number. 
- A FAT12 formatted volume can hold a maximum of 4,086 clusters, which is 2^12 minus a few values (for reserved values to be used in the FAT).
- FAT12 is therefore most suitable for very small volumes, and is used on floppy disks and hard disk partitions smaller than 16 MB. 
- We can mount the raw floppy image data with the help of  
    - some virtualization in Bochs (http://bochs.sourceforge.net/doc/docbook/user/loop-device-usage.html) or
    - some tools in windows like Virtual Floppy Drive (http://vfd.sourceforge.net/) and retrieve the data (files) from them
