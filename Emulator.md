## Emulator vs Simulator
Even though the terms are more generic, this post will talk about only software simulators and emulators.

Emulators are software/hardware, which mimics every aspect of other system (software/hardware), so that we get the feeling of using it. It is also important to note that, if something works in am emulator, it will also work in real thing being emulated. Every aspect about it are modeled and interact as in real system.
It can be used as a substitute to the original one.
Example: Android Emulator available as part of Android SDK is an emulator. Virtual PC simulate PCs of different architecture.

Simulators are interested in certain aspects of the whole system. They only model those components as accurate as possible and leave other aspects. Mainly used for analysis and training purpose.
Example: Flight simulator, Chemical reaction simulators in industries


## How to start writing code an Emulator?
As discussed in earlier post - Emulators are software/hardware, which mimics every aspect of other system (software/hardware), so that we get the feeling of using it under complete different environment.

To start writing an emulator, we need the full knowledge of the system - about various components and how they interact to produce the desired results in the original (or target) system. Knowledge about the actual
implementation is not needed (may be this is misleading - what I meant is the abstraction about the systems actual implementation in terms of hardware or primitive routines). Even though knowledge about their interactions to produce the actual result in the target system is needed. i.e. we need a list of components, their interactions, inputs and output combinations.

Try to make test-cases for (both white-box & black-box) all the input, output combinations with corresponding interactions. Also, time for completion should be proportional to the original system in all these test cases. The more closer they are - you are building an actual system.


## More details:
http://en.wikipedia.org/wiki/Emulator
