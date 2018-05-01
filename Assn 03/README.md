# ECU-2540

## Assignment 03
### Assignment Description
This assignment is a continuation of the second, expanding on the concept by including two new commands for program memory management:
- ‘g’ to grow a program’s size
- ‘s’ to shrink a program’s size

Furthermore, whereas the List.class file was provided for the prior assignment (in MemSim.jar), for this assignment, the list had to be reimplemented as an array. Error checking was also expanded to include necessary cases for growing and shrinking a program.

The following assumptions were allowed:
- At least one command will be passed to the program – the exit command
- Any program ID not used to print unallocated page ranges will be greater than 0
- The exit command identifies the simulation’s end, regardless of input afterward
### Source Files
MemSim.jar, MemSim.java, MemParam.java, List.java
### Compilation, Testing, and Known Issues
```
Windows:
Compile: javac -cp “.;MemSim.jar” MemSim.java MemParam.java List.java
Testing: java -cp “.;MemSim.jar” MemSim

To compile and test on Linux, replace ‘;’ with ‘:’ in the classpath toggle
```
Notes:
- See the assignment 2 readme for information about the four other commands in the program, as well as input formatting
