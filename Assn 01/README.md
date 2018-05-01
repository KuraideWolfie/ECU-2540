# ECU-2540

## Assignment 01
### Assignment Description
This assignment was, for lack of better words, a grocery store simulator, where an inventory of items is loaded into the program, and then customers allowed to make purchases (via specifying an ID and quantity of the item to be purchased). If a duplicate item was attempted to be loaded into the inventory, then the duplicate item would be ignored. If the inventory was already at maximum capacity, an error would be shown. If an item was unavailable for purchase by the customer, then an error should be shown. At the end of a customer’s purchases, statistics would be shown – subtotal, tax, and total cost. <i>(The number of customers to be processed was never given a limitation, nor was limitations on how to terminate customer processing given. Input for this program was given via file, where the EOF signaled the end of processing.)</i>
### Source Files
GroceryItem.java, GroceryTest.java, inventory.dat
### Compilation, Testing, and Known Issues
```
Compile: javac GroceryItem.java GroceryTest.java
Testing: java GroceryTest <./data/customers.dat
```
Notes:
- The format of a customer’s data should be the item ID they’re purchasing, and the quantity being purchased, all on the same line. A customer’s data ends with two -1s. For example:<br/>30007 4<br/>11010 6<br/>-1 -1<br/>11012 1<br/>-1 -1<br/><br/>The above example has two customers to be processed by the program, with customer 1 purchasing a total of 10 items, with customer 2 purchasing only 1 item.
- Input should be redirected from a file, as the program expects an EOF to stop processing of customers. (Input could be specified during execution, live, via the console, but customer processing will never terminate as such.)
- The program expects that information information be provided via the file `inventory.dat`. The format of such a file is as follows:<br/>[id] [name] [quantity] [price] [F/N]<br/><br/>F and N represent ‘food’ and ‘nonfood’ items, which will determine tax computations. No special input is necessary to terminate the inventory file – input is stopped when EOF is reached.