# ECU-2540

## Assignment 02
### Assignment Description
This assignment was a memory simulator, allowing memory to be taken from a system and then returned to it. (The system is virtual – no actual allocation is done in the operating system!) Further, basic parameters were specified, and mandated to be stored in the file `MemParam.java`. Four basic commands were required:
- ‘i’ to initiate a ‘program’ in the memory
- ‘t’ to terminate a program in the memory
- ‘p’ for printing a program’s information
- ‘x’ to terminate execution of the program

The following assumptions were allowed:
- At least one command would be passed to the program
- Any program ID not being used to print page allocation will be >= 0
- The size parameter, if not used for initiation, would be 0
- The exit command identifies the end of the simulation, regardless of input lines afterward
### Source Files
MemSim.java, MemParam.java, MemSim.jar
### Compilation, Testing, and Known Issues
```
Windows:
Compile: javac -cp “.;MemSim.jar” MemSim.java MemParam.java
Testing: java -cp “.;MemSim.jar” MemSim

To compile and test on Linux, replace ‘;’ with ‘:’ in the classpath toggle
```
Notes:
- Input for the program should be formatted as “[code] [id] [size],” where [code] is one of the above commands, [id] is the ID of the program to execute the code for, and [size] is the size of the program. (The size parameter should only matter for program initiation – specify 0 for termination, printing, or exiting the program.)
