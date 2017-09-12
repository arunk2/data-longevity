ASCII Encoding
==============
Most of us use ASCII by default and are unaware of what it exactly it means and 
how it is related to displaying encoded characters. 
American Standard Code for Information Interchange (ASCII) is nothing but a character encoding system based on the English alphabet 
(the numbers 0-9, the letters a-z and A-Z, some basic punctuation symbols).

Initially ASCII used 7-bit code and represented 128 characters (0-127), 
- out of which 0 - 31 (first 32) characters are control characters for devices like printers and telegraphic devices (they are also called non-printable characters).
- ASCII codes 32–126 (0-9, a-z A-Z, some punctuation symbols)
- ASCII is expanded to 8-bit code for supporting 256 characters (mainly **vernacular language specific characters, various symbols, as well as box-drawing characters**. 
- 128-255 characters are referred to as extended ASCII.

----------------------------------------

ASCII & Code Page 437
=====================
'Code page' is a mapping of values for a character set (for encoding a particular language). 
It all started with IBM assigning unique numbers to characters in EBCDIC encoding scheme for mainframe systems, 
later every system vendors used their own scheme for characters encoding. 

We can also view **'code page' as graphical glyph set** used for rendering a encoded character. 
These code pages were originally embedded directly in the text mode hardware of the graphic adapters used with the IBM PC and its clones.

Having said that - Code page 437 is the actual character set of the original IBM PC (personal computer). 
It includes 
- ASCII codes 32–126, 
- extended codes for 
  - accented letters (diacritics), 
  - some Greek letters, 
  - icons
  - drawing symbols. 
  
Most of the code pages(for different languages) are super-sets of ASCII(discussed in previous section). 
Also, 8-bit implementations of the ASCII code set the top bit used as parity bit in network data transmissions. 

### Unicode
Natural doubt for anyone will be - is there any standard way of encoding for all the code pages?
Unicode is the answer for this. Unicode is an effort to include all characters from all code pages into a single character enumeration that can be used with a number of encoding schemes. There are standard translations for converting code pages to Unicode.


## More details can be found on:
- http://en.wikipedia.org/wiki/ASCII
- http://unicode.org/Public/MAPPINGS/VENDORS/MICSFT/PC/
- http://en.wikipedia.org/wiki/Code_page_437

