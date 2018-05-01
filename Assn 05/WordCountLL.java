// CSCI 2540 Programming Assignment 05
// Title:   WordCountLL.java
// Date:    4 December 2017
// Author:  Matthew Morgan
/* Description:
 * This program implements a hash table via utilization of chaining, requiring
 * the LinkedList structure. It reads a series of sentences in from a file
 * passed as an argument via the commandline, displaying thereafter a number
 * of words, n, also specified via commandline, that occur the most frequently
 * in the read in data.
 *
 * Assumptions:
 * - Two arguments will be passed to the program: a filename and a number
 *   of words that are the most frequent in the file.
 */

import java.util.*;

public class WordCountLL {

  public static final int TABLESIZE = 587;
  public static List<List<WordItem>> hashTable = new ArrayList<>(TABLESIZE);

  public static void main(String[] args) {
    // Initialization of the hash table
    for (int i = 0; i < TABLESIZE; i++)
      hashTable.add(new LinkedList<WordItem>());

    if (args.length < 2) {
      System.out.printf("ERROR: insufficient number of command line arguments." +
        " Program aborted.\n");
      return;
    }

    int numWords = Integer.parseInt(args[1]);
    // Number of highest-frequent words to show
    int unique = tableGenerate(args[0]);
    // The number of unique words found during table generation

    //printList();

    WordItem[] mostFrequent = tableAnalysis(numWords);
    // Array containing the most frequent words found in the data

    // Print the most frequent words and number of unique words
    System.out.printf("The %d most frequently occurring words were:\n",
      mostFrequent.length);
    for (int i = 0; i < mostFrequent.length; i++)
      System.out.printf("%d. %s occurred %d times\n", i + 1,
        mostFrequent[i].getWord(), mostFrequent[i].getCount());
    System.out.printf("\nThere were a total of %d unique words\n", unique);
  }

  /**
   * Generates the hash table by iterating through all words in the specified
   * file and incrementing the frequencies of each unique word.
   *
   * @param file The file whom's contents will be used to generate the table
   * @return The number of unique words in the table
   */
  public static int tableGenerate(String file) {
    // Create BookIterator and read data
    BookIterator book = new BookIterator();
    book.readBook(file);

    int index;      // Index of the list that the word belongs to in-table
    String word;    // The word currently being searched for in the table
    int unique = 0; // The number of unique words

    // Iterate through all the words in the book, inserting them into the table
    // or incrementing their counters if they already exist in the table.
    while (book.hasNext()) {
      word = book.next();
      index = hash(word);
      WordItem item = tableHasWord(index, word);

      if (item != null)
        item.incCount();
      else {
        hashTable.get(index).add(new WordItem(word));
        unique++;
      }
    }

    return unique;
  }

  /**
   * Searches for a given word in the hash table
   *
   * @param index The index of the list that may contain the word
   * @param word  The word to be searched for
   * @return The object containing the word's frequency, or null if the word
   * wasn't found in the table
   */
  public static WordItem tableHasWord(int index, String word) {
    ListIterator<WordItem> L = hashTable.get(index).listIterator();
    // Iterator for the List that may contain the word searched for

    // Iterate through the list in the hash table at the specified index to
    // attempt location of the word.
    while (L.hasNext()) {
      WordItem wi = L.next();
      if (wi.getWord().equals(word))
        return wi;
    }

    // Word wasn't found; return null
    return null;
  }

  /**
   * Commits analysis of the hash table to discern the requested number of
   * most frequent words in the given input data
   *
   * @param numWords The number of most-frequent words to find
   * @return An array of WordItems containing the most-frequent words
   */
  public static WordItem[] tableAnalysis(int numWords) {
    WordItem[] mostFrequent = new WordItem[numWords];
    // Array of WordItems that occur the most frequently

    // Sort the lists in the hash table in descending order
    for (int i = 0; i < TABLESIZE; i++)
      Collections.sort(hashTable.get(i), Collections.reverseOrder());

    // Store the number of requested most frequent words
    for (int i = 0; i < mostFrequent.length; i++)
      mostFrequent[i] = nextFreqWord();

    return mostFrequent;
  }

  /**
   * Returns the next most frequent word in the hash table
   *
   * @return The WordItem containing the next most frequent word or null
   * if no word was found
   */
  public static WordItem nextFreqWord() {
    int nextFreqWordList = -1; // Index of the next most-frequent word

    for (int i = 0; i < TABLESIZE; i++) {
      List<WordItem> L = hashTable.get(i); // List at index 'i' in the table

      if (!L.isEmpty()) {
        if (nextFreqWordList == -1)
          nextFreqWordList = i;
        else if (hashTable.get(nextFreqWordList).get(0).getCount() <
          hashTable.get(i).get(0).getCount())
          nextFreqWordList = i;
      }
    }

    if (nextFreqWordList == -1)
      return null;
    else
      return hashTable.get(nextFreqWordList).remove(0);
  }

  /**
   * Returns the hash value for the word stored in "word." The word is hashed
   * via summing the ASCII codes of the characters modulated by the table size.
   *
   * @param word The word to be assigned a hash value
   * @return The word's hash value
   */
  public static int hash(String word) {
    int i;
    int addr = 0;
    for (i = 0; i < word.length(); i++)
      addr += (int) word.charAt(i);

    return addr % TABLESIZE;
  }

  /* This function is not available in the submitted copy of the assignment. */
  public static void printList() {
    for(int i=0; i<hashTable.size(); i++) {
      if (!hashTable.get(i).isEmpty()) {
        ListIterator<WordItem> L = hashTable.get(i).listIterator();
        System.out.printf("List at index %-4d: ", i);
        while (L.hasNext()) {
          WordItem wi = L.next();
          System.out.printf("%d:%s -> ", wi.getCount(), wi.getWord());
        }
        System.out.println();
      }
    }
    System.out.println();
  }

}
