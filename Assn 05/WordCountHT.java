// CSCI 2540 Programming Assignment 05
// Title:   WordCountHT.java
// Date:    4 December 2017
// Author:  Matthew Morgan
/* Description:
 * This program implements a hash table via Java's included structures
 * Hashtable and Collection. It reads a series of sentences in from a file
 * passed as an argument via the commandline, displaying thereafter a number
 * of words, n, also specified via commandline, that occur the most frequently
 * in the read in data.
 *
 * Assumptions:
 * - Two arguments will be passed to the program: a filename and a number
 *   of words that are the most frequent in the file.
 */

import java.util.*;

public class WordCountHT {

  public static final int TABLESIZE = 11003;
  public static Hashtable<String,WordItem> hashTable =
      new Hashtable<>(TABLESIZE);

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.printf("ERROR: insufficient number of command line arguments."+
        " Program aborted.\n");
      return;
    }

    int numWords = Integer.parseInt(args[1]);
      // Number of highest-frequent words to show
    int unique = tableGenerate(args[0]);
      // The number of unique words found during table generation
    WordItem[] mostFrequent = tableAnalysis(numWords);
      // Array containing the most frequent words found in the data

    // Print the most frequent words and number of unique words
    System.out.printf("The %d most frequently occurring words were:\n",
      mostFrequent.length);
    for(int i=0; i<mostFrequent.length; i++)
      System.out.printf("%d. %s occurred %d times\n", i+1,
        mostFrequent[i].getWord(), mostFrequent[i].getCount());
    System.out.printf("\nThere were a total of %d unique words\n", unique);
  }

  /**
   * Generates the hash table by iterating through all words in the specified
   * file and incrementing the frequencies of each unique word.
   *
   * @param file  The file whom's contents will be used to generate the table
   * @return The number of unique words in the table
   */
  public static int tableGenerate(String file) {
    // Create BookIterator and read data
    BookIterator book = new BookIterator();
    book.readBook(file);

    String word;    // The word currently being searched for in the table
    int unique = 0; // The number of unique words

    // Iterate through all the words in the book, inserting them into the table
    // or incrementing their counters if they already exist in the table.
    while(book.hasNext()) {
      word = book.next();
      WordItem item = tableHasWord(word);

      if (item != null)
        item.incCount();
      else {
        hashTable.put(word, new WordItem(word));
        unique++;
      }
    }

    return unique;
  }

  /**
   * Searches for a given word in the hash table
   *
   * @param word  The word to be searched for
   * @return  The object containing the word's frequency, or null if the word
   *          wasn't found in the table
   */
  public static WordItem tableHasWord(String word) {
    ArrayList<WordItem> L = Collections.list(hashTable.elements());
      // Arraylist representing the contents of the hash table

    // Iterate through each entry of the table's entries to search for the word
    for(WordItem wi : L)
      if (wi.getWord().equals(word))
        return wi;

    // Word wasn't found; return null
    return null;
  }

  /**
   * Commits analysis of the hash table to discern the requested number of
   * most frequent words in the given input data
   *
   * @param numWords  The number of most-frequent words to find
   * @return An array of WordItems containing the most-frequent words
   */
  public static WordItem[] tableAnalysis(int numWords) {
    WordItem[] mostFrequent = new WordItem[numWords];
      // Array of WordItems that occur the most frequently

    // Create an arraylist containing the table's items and sort it (descending)
    ArrayList<WordItem> L = Collections.list(hashTable.elements());
    Collections.sort(L, Collections.reverseOrder());

    // Add the number of requested most-frequent words to the array
    for(int i=0; i<mostFrequent.length; i++)
      mostFrequent[i] = L.remove(0);

    return mostFrequent;
  }
}