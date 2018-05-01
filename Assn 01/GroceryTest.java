// CSCI 2540 Programming Assignment 01
// Title:	GroceryTest.java
// Date:	30 August 2017
// Author:	Matthew Morgan
// Desc:
// Grocery shopping simulator meant for testing GroceryItem.java. Assumes that
// a file named "inventory.dat" exists with the following format:
// <product id> <name> <quantity> <price> <tax code>
// Example: 12345 loaf-bread 100 2.99 F

import java.io.*;
import java.util.*;

public class GroceryTest {
	
	// Constants
	public static final int INVENTORY_CAPACITY = 100; // Inventory max capacity
	public static final float
			TAX_FOOD = (float)0.02,		// Tax for food items
			TAX_NONFOOD = (float)0.07;	// Tax for non-food items

	public static void main(String[] args) {
		GroceryItem[] inventory =
				new GroceryItem[INVENTORY_CAPACITY]; // Store's stock
		Scanner fileInput = null;	// Scanner used for reading input
		int inventoryCount = 0;		// Number of products in the store's stock
		
		//*********************************************************************
		// READ IN INVENTORY ITEMS
		//*********************************************************************
		File inFile = new File("inventory.dat");
		
		try {
			fileInput = new Scanner(inFile);
			
			while(fileInput.hasNext()) {
				// Read inventory item
				GroceryItem tmp = new GroceryItem();// GroceryItem to be added
				tmp.readItem(fileInput);
				int index = GroceryItem.itemSearch(inventory, inventoryCount,
						tmp.getNumber());			// Result of product search
								
				if (index != -1)
					System.out.printf(
							"** ERROR: duplicate item %5d %12s ignored.\n",
							tmp.getNumber(), tmp.getName());
				else if (inventoryCount == INVENTORY_CAPACITY)
					System.out.println("** ERROR: Inventory at max capacity");
				else {
					inventory[inventoryCount] = tmp;
					inventoryCount++;
				}
			}
			
			fileInput.close();
		}
		catch(FileNotFoundException e) { System.out.println(e); }

		System.out.println("INITIAL INVENTORY");
		GroceryItem.printInventory(inventory, inventoryCount);
		customerProcess(inventory, inventoryCount, fileInput);
		System.out.println("FINAL INVENTORY");
		GroceryItem.printInventory(inventory, inventoryCount);
	}
	
	/**
	 * Processes all customers in the checkout line, updating the store's
	 * inventory as each item is rung up. Assumes that the input is formatted
	 * as follows, where "-1 -1" represents the end of a customer's products:
	 * <product ID> <quantity>
	 * ...
	 * -1 -1
	 * @param inventory			An array of products in the store's inventory.
	 * @param inventoryCount	The amount of products in the inventory array.
	 * @param fileInput			A scanner for reading in customer products.
	 */
	public static void customerProcess(GroceryItem[] inventory,
			int inventoryCount, Scanner fileInput) {
		int customerCurrent = 1;			// The current customer in checkout
		int[] customerItem = {-1, -1};		// Product ID and quantity of item
		float[] customerCharge = {0, 0, 0, 0};// Current subtotals (nonfood, food)
		////float customerTotal, customerTax;	// Final total and tax for customer
		fileInput = new Scanner(System.in);
		
		// Read in product information until the end of file has been reached
		while(fileInput.hasNext()) {
			if (customerItem[0] == -1)
				System.out.println("Customer " + customerCurrent);
			
			customerItem[0] = fileInput.nextInt();
			customerItem[1] = fileInput.nextInt();
			
			if ((customerItem[0] != -1 && customerItem[1] != -1)) {
				// Another product for the customer was read; update customer
				// charge and inventory if the product is in the inventory
				int index = GroceryItem.itemSearch(inventory, inventoryCount,
						customerItem[0]);
				
				if (index != -1) {
					GroceryItem item = inventory[index];
					
					//System.out.printf(
					//		"  %-12s %3d @ %5.2f, Cost: %5.2f %c\n",
					//		item.getName(), customerItem[1], item.getPrice(),
					//		customerItem[1]*item.getPrice(), item.getTaxCode());
					
					System.out.printf("  %12s %2d@%5.2f, Cost = %5.2f %s\n",
							item.getName(), customerItem[1], item.getPrice(),
							customerItem[1]*item.getPrice(), item.getTaxCode());
					
					if (item.getTaxCode().charAt(0) == 'N')
						customerCharge[0] += customerItem[1] * item.getPrice();
					else
						customerCharge[1] += customerItem[1] * item.getPrice();
					
					item.setQuantity(item.getQuantity() - customerItem[1]);
				}
				else
					System.out.printf("  *** item %5d not in inventory ***\n",
							customerItem[0]);
			}
			else {
				// The end of customer items was reached; calculate total cost
				// and reset for next customer
				////customerTotal = customerCharge[0]+customerCharge[1];
				////customerTax = customerCharge[0]*TAX_NONFOOD +
				////		customerCharge[1]*TAX_FOOD;
				customerCurrent++;
				customerCharge[2] = customerCharge[0]*TAX_NONFOOD +
						customerCharge[1]*TAX_FOOD;
				customerCharge[3] = customerCharge[0]+customerCharge[1];
				customerCharge[3] = customerCharge[3]+customerCharge[2];
				customerCharge[0] = 0;
				customerCharge[1] = 0;
				
				//for(int i=0; i<45; i++)
				//	System.out.print((i==0?"  ":"") + "-");
				
				//System.out.printf("\n   Total Cost: $ %6.2f\n" +
				//	"    Total Tax: $ %6.2f\n" +
				//	"  Grand Total: $ %6.2f\n", customerTotal, customerTax,
				//	customerTotal+customerTax);
				
				//for(int i=0; i<45; i++)
				//	System.out.print((i==0?"  ":"") + "-" +
				//            (i+1==45?"\n\n":""));
				
				////System.out.printf(
				////		"\n  Total Cost  = $%6.2f\n  Total Tax   = $%6.2f\n  "+
				////		"Grand Total = $%6.2f\n\n", customerTotal, customerTax,
				////		customerTotal+customerTax);
				
				System.out.printf(
						"\n  Total Cost  = $%6.2f\n  Total Tax   = $%6.2f\n  "+
						"Grand Total = $%6.2f\n\n", customerCharge[3]-customerCharge[2],
						customerCharge[2], customerCharge[3]);
			}
		}
		
		fileInput.close();
	}
	
}
