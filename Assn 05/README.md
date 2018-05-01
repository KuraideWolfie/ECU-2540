# ECU-2540

## Assignment 05
### Assignment Description
This assignment exists in two parts, both of which do the same thing, but are implemented with two different strategies. The assignment reads a series of sentences from a file passed as commandline argument, displaying a number of words, n, also specified via commandline. The words shown are the most frequently-occurring words in the text file provided as argument. The “HT” version implements the program with a Hashtable and Collection object, and “LL” implements it as a linked list.
### Source Files
WordCountHT.java, WordCountLL.java, BookIterator.class, WordItem.class
### Compilation, Testing, and Known Issues
```
Windows:
Compile: javac -cp “.;rsc” WordCountHT.java WordCountLL.java
Testing:
java -cp “.;rsc” WordCountHT <input> <words>
java -cp “.;rsc” WordCountLL <input> <words>

<input> is the path of the text file to read, and <words> is the number of most frequent words to be shown. To compile and run this program on Linux, replace all ‘;’ with ‘:’ in the classpath toggles
```
Notes:
- If you run a text file through both variants of the program with the same number of words, n, you should receive the same results.
- The original data provided by the instructor was a book – the Adventures of Huckleberry Finn – in text format. For safety, these files are not provided here, and instead, a generic text file provided to test the code with.