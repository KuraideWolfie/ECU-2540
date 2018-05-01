// CSCI 2540 Programming Assignment 03
// Title:   List.java
// Date:    30 October 2017
// Author:  Matthew Morgan
/* Description:
 * Reference-based implementation of ADT list using arrays. Due to the
 * limitations with array of generics, the "data type" for the list
 * items is fixed to be of type PageUsage. Any program using this class must
 * specify <PageUsage> as the value for the type parameter.
 */

public class List<T> {

  // Constants
  public static final int MAX_LIST = 20;
  public static final int NULL = -1;

  // State variables
  private PageUsage item[] = new PageUsage[MAX_LIST];  // data
  private int next[] = new int[MAX_LIST]; // pointer to next item
  private int head;     // pointer to front of used cells list
  private int free;     // pointer to front of unused cells list
  private int numItems; // number of items in list

  // Constructor must initialize used list to empty and free list to
  // all available nodes.
  public List() {
    for (int index = 0; index < MAX_LIST - 1; index++)
      next[index] = index + 1;

    next[MAX_LIST - 1] = NULL;
    numItems = 0;
    head = NULL;
    free = 0;
  }

  /**
   * Removes all the entries in the list
   */
  public void removeAll() {
    for (int index = 0; index < MAX_LIST - 1; index++)
      next[index] = index + 1;

    next[MAX_LIST - 1] = NULL;
    numItems = 0;
    head = NULL;
    free = 0;
  }

  /**
   * Returns if the list is currently empty or not
   * @return True if the list is empty, or false if not
   */
  public boolean isEmpty() {
    return numItems == 0;
  }

  /**
   * Obtains and returns the size of the list currently
   * @return The current size of the list
   */
  public int size() {
    return numItems;
  }

  /**
   * Locates a specified node in the linked list.
   * Precondition: Index is the node's index in the list
   * where the index satisfies 1 <= index <= numItems
   * @param index The index of the node to be located.
   * @return A reference to the desired node.
   */
  private int find(int index) {
    int ptr = head;
    for (int i = 2; i <= index; i++)
      ptr = next[ptr];
    return ptr;
  }

  /**
   * Returns the item at the specified index in the linked list.
   * Precondition: Assumes 1 <= index <= numItems
   * @param index The index of the item to be retrieved in the linked list.
   * @return The item contained in the node at the specified index.
   */
  public PageUsage get(int index) {
    if (!isEmpty())
      return item[find(index)];
    else
      System.out.printf("ERROR on get: linked list is empty.\n");
    return null;
  }

  /**
   * Adds a new item to the linked list at the specified index.
   * Precondition: Assumes 1 <= index <= numItems+1
   * @param index The index to add the item at.
   * @param newItem The new item to be added to the list.
   */
  public void add(int index, PageUsage newItem) {
    if (size() < MAX_LIST) {
      int nxtFree = next[free]; // Next node in the free list

      if (index == 1) {
        // Inserting at the beginning of the linked list
        int curHead = head; // The current head of the list

        head = free;
        item[head] = newItem;
        next[head] = curHead;
        free = nxtFree;
      }
      else {
        // Inserting in the middle or at end of the list
        int prev = find(index - 1), // Node before 'cur' in the list
            cur = next[prev];             // Current node at the desired index

        next[prev] = free;
        item[next[prev]] = newItem;
        next[next[prev]] = cur;
        free = nxtFree;
      }

      numItems++;
    }
    else
      System.out.printf("ERROR on get: linked list is full.");
  }

  /**
   * Removes the item at the specified index in the linked list.
   * Precondition: Assumes 1 <= index <= numItems
   * @param index The index to remove from the list.
   */
  public void remove(int index) {
    if (!isEmpty()) {
      if (numItems == 1 || index == 1) {
        // Removing from the front of the list
        int nxtHead = next[head]; // Temporary storage for node after head

        item[head] = null;
        next[head] = free;
        free = head;
        head = nxtHead;
      }
      else {
        // Removing from the middle of the list
        int prev = find(index - 1),
          cur = next[prev],
          nxt = next[next[prev]];

        // Remove the item, update the current node to point to the front
        // of the free list, update the previous node's pointer, and then
        // update free to reflect the free list's new front.
        next[prev] = nxt;
        item[cur] = null;
        next[cur] = free;
        free = cur;
      }
      numItems--;
    }
    else
      System.out.printf("ERROR on remove: linked list is empty.\n");
  }

}