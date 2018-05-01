// CSCI 2540 Programming Assignment 01
// Title:	GroceryItem.java
// Date:	30 August 2017
// Author:	Matthew Morgan
// Desc:
// Definition of a single grocery item available for purchase.

public class GroceryItem {
	
	// State variables
	private int itemID;			// The item's identifying integer
	private int itemCount;		// The amount of the item being purchased
	private String itemDesc;	// A 12-character description of the item
	private char itemTaxCode;	// Code specifying the item's tax
	private float itemPrice;	// The price of a single of the item
	
	// Accessor methods
	public String getName() { return itemDesc; }
	public int getNumber() { return itemID; }
	public float getPrice() { return itemPrice; }
	public int getQuantity() { return itemCount; }
	////public char getTaxCode() { return itemTaxCode; }
	public String getTaxCode() { return Character.toString(itemTaxCode); }

	/**
	 * Sets the available quantity of the item in inventory.
	 * @param value		The new quantity of the item in inventory.
	 */
	public void setQuantity(int value) { itemCount = value; }
	
	/**
	 * Reads in a single inventory item. Assumes that the formatting for each
	 * item in the file is as follows:
	 * <product ID> <name> <desc> <count> <price> <tax code>
	 * @param inFile	The Scanner where the item's information is read from.
	 */
	public void readItem(java.util.Scanner inFile) {
		itemID = inFile.nextInt();
		itemDesc = inFile.next();
		itemCount = inFile.nextInt();
		itemPrice = inFile.nextFloat();
		itemTaxCode = inFile.next().charAt(0);
	}
	
	/**
	 * Searches for the given item identifier in the inventory.
	 * @param item		An inventory of items being purchased.
	 * @param numItems	The number of items in the inventory.
	 * @param productID	The unique identifier of the product being searched.
	 * @return The index of the product in the inventory, or -1 if not found.
	 */
	public static int itemSearch(GroceryItem[] item, int numItems,
			int productID) {
		// Loop through all filled indices of the inventory
		for(int index=0; index<numItems; index++)
			if (item[index].getNumber() == productID)
				return index;
		
		// Return -1; item wasn't located
		return -1;
	}
	
	/**
	 * Prints the given inventory, one item per line, up to the entry with the
	 * index numItems.
	 * @param item		The inventory to be printed to the display.
	 * @param numItems	The number of items in the inventory to print.
	 */
	public static void printInventory(GroceryItem[] item, int numItems) {
		// Loop through indices 0 to numItems in the inventory
		System.out.println("  ID            DESC  CNT  PRICE TAX");
		for(int index=0; index<numItems; index++) {
			GroceryItem tmp = item[index];
			//System.out.printf(
			//		"  Item %5d: %12s @ $%-5.2f each, Tax Code: %s, Quantity: %d\n",
			//		tmp.getNumber(), tmp.getName(), tmp.getPrice(),
			//		tmp.getTaxCode(), tmp.getQuantity());
			System.out.printf("  %5d %12s %4d %6.2f  %1s\n", tmp.getNumber(),
					tmp.getName(), tmp.getQuantity(), tmp.getPrice(),
					tmp.getTaxCode());
		}
		System.out.println();
	}
	
}