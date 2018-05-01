# ECU-2540

## Assignment 04
### Assignment Description
This assignment was about converting a postfix expression to an infix expression, evaluating that expression after reading in the different values from standard input. The following assumptions were made:
- The input will contain a line with `$PART2` between assignment statements and infix expressions
- All assignment operations will specify a capital letter, such as ‘A’.
### Source Files
InfToPost.java
### Compilation, Testing, and Known Issues
```
Compile: javac InfToPost.java
Testing: java InfToPost <./input.txt
```
Notes:
- The format of an input file is as follows:<br/>[assignment]<br/>...</br>$PART2<br/>[postfix expression]<br/>...<br/>EOF<br/><br/>An assignment is, for example, `A = -2`, where spaces are not mandatory; however, only the letters A to Z (capital) may be used for assignment, and only a single letter may be specified (no ‘AA’ or ‘BFC’). [postfix expression] should be a valid postfix expression, with parentheticals surrounding appropriate sections. For example, `(D+C)-(H*X-C)`.
