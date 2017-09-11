How to Parse a ELF file?
========================

As we all know ELF (Executable and Linkable File) is a standard file format for executables, object code, shared libraries, and core dumps in UNIX like systems.
In this post lets talk about the header of ELF file, which is of different size for different architecture. 
Also, How to parse the header information so that we can make use of data from it?

ELF header contains 20 fields. First 2 fields are standard size 4 bytes and 1 byte each holding a magic no to identify it as ELF file and boolean value to identify as 32/64 bit architecture. Except for 3 fields (ENTRY - start point for execution, PHOFF - start of program header table, SHOFF - start of section header table), which are memory reference others remain the same.
In case of 32 bit arch - these 3 fields are 4 bytes size and for 64 bit arch - they are 8 bytes size each.

The order of fields in ELF header are given below:

      MAGIC   = 0;
      ARCH_32_64 = 1;
      ENDIAN  = 2;
      VERSION = 3;
      OS   = 4;
      ABI   = 5;
      PADDING = 6;
      TYPE  = 7;
      MACHINE = 8;
      VERSION_1 = 9;
      ENTRY  = 10;
      PHOFF         = 11;
      SHOFF         = 12;
      FLAGS         = 13;
      EHSIZE  = 14;
      PHENTSIZE   = 15;
      PHNUM  = 16;
      SHENTSIZE   = 17;
      SHNUM  = 18;
      SHSTRNDX = 19;

For more details:
http://en.wikipedia.org/wiki/Executable_and_Linkable_Format
