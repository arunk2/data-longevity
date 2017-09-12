Basic Machine Architecture - MIPS R2000 ISA
===========================================
MIPS R2000 is a RISC processor. 
Its ISA has fixed-width 32 bit instructions and fall into one of the following three categories: 
- R-type
- I-type 
- J-type

### R-type - Register type instructions

|B31-26|B25-21|B20-16|B15-11|B10-6|B5-0|
|---|---|----|----|----|----|
|opcode|register s|register t|register d|shift amount|function|

### I-type instructions - Immediate type instructions

|B31-26|B25-21|B20-16|B15-0|
|---|---|----|----|
|opcode|register s|register t|Immediate|

### J-type instructions - Jump type instructions

|B31-26 |B25-0|
|----|-----|
|opcode |Target|

All the Instructions - can also be grouped under following functional groups.

- **Arithmetic Instructions:** +, -, *, / operations on std data-structures (short, int, long, float)
- **Logical Instructions:** AND, OR, NOT, XOR operations on std data-structures (short, int, long, float)
- **Comparison Instructions:** <, >, =, >=, <= operations on std data-structures (short, int, long, float)
- **Branch Instructions:** Changing the PC register values - updating with target address defined in the instruction
- **Load, Store and Move Instruction:** For moving data from Memory to Registers and Registers to Memory with various addressing modes
- **Trap, Exception and Interrupt Instructions:** These are handled trigger an processor exception, which are caught and handled at next layer (OS layer)


Totally we implement emulating all the **140 instructions** of MIPS R2000 for a basic machine architecture


## More details can be found in:
http://pages.cs.wisc.edu/~larus/SPIM/spim_documentation.pdf
http://www2.engr.arizona.edu/~ece369/Resources/spim/MIPSReference.pdf
