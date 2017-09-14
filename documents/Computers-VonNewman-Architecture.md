Computers - Von Newman Architecture
=====================

Have you ever wondered how exactly computer does all the tasks from browsing, playing games, videos, editing documents, etc... 

By Computers I mean every thing from mobile phones, laptops to Dedicated server class machines and even world fastest super computers. 
All most all of them work on same basic design done by Von Newmann popularly known as 'Von Newmann Architecture'. 
Lets discuss about it on a high level in this post.


The basic characteristics of any computer:

- Computer has 4 main sub-systems
    - Memory
    - ALU (Arithmetic/Logic Unit)
    - Control Unit
    - Input/Output System (I/O)
- Program is stored in memory during execution
- Program instructions are executed sequentially till a halt instruction is executed


### Memory sub-system:
Memory is nothing but the RAM (Random Access Memory)
Stores informations in memory cells, which are accessible via some given address
Both the Program and Data used in programs are stored in memory cells.
They are directly accessed and by other sub-systems.

Typically memory sub-system is accessed via 
- Fetch (address) and
- Store (address, value) calls

These calls are implemented with the help of interfaces of the system
- Memory Address Register (MAR)
- Memory Data Register (MDR)
- Fetch/Store signal

### ALU sub-system:
They are responsible for 
- mathematical operations (+, -, x, /, …)
- logic operations (=, <, >, and, or, not, ...)


Typically they have hard-wired circuits for arithmetic/logic operations, 
Registers (to store intermediate results) and bus that connects them.


### Control Unit sub-system:
As mentioned earlier in memory sub-system all the programs are stored in memory. 
The main responsibility of control unit is to execute programs. 
It does it by  repeatedly doing the popularly known Fetch, Decode, Execute cycles.
- **In Fetch cycle** - next instruction to be executed is loaded from memory.
- **In Decode cycle** - it decodes the fetched instruction (recognize what need to be done)
- **In Execute cycle** - it Executes the instruction by issuing the appropriate signals to the ALU, memory, and I/O subsystems.

It repeats the same steps till it executes HALT instruction.

The following 2 registers plays a major role in control unit sub-system:
- Instruction Register(IR) contains current instruction in execution
- Program Counter(PC) contains address of next instruction to be executed

### Input/Output sub-system:
The main responsibility is to interact with devices that allow the computers to:
- Communicate and interact with users like Display Screen, keyboard, Mouse...
- Store information/data like Hard-disks, CD/DVD, tapes...

### The following sources might give more details:
- http://en.wikipedia.org/wiki/Von_Neumann_architecture
- http://none.cs.umass.edu/~dganesan/courses/fall09/handouts/Chapter4.pdf
