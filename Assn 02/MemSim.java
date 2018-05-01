// CSCI 2540 Programming Assignment 02
// Title:   MemSim.java
// Date:    18 September 2017
// Author:  Matthew Morgan
/* Description:
 * MemSim is a memory allocation simulator, where an initial amount of memory
 * pages are available for allocation to programs. The 'system' initiates
 * and terminates programs, also capable of printing ranges of pages allocated
 * to a program or those remaining available.
 *
 * Input Format:
 * A series of commands for the simulator to execute may be passed through
 * either user input or file redirection, where lines are formatted as below:
 * <opcode> <progID> <size>
 *
 * The 'opcode' is any of the following:
 * - 'i' for initiate     - 'p' for printing
 * - 't' for terminate    - 'x' for system exit
 * The 'progID' is the identifying number of the program to execute the command
 * for, and 'size' is the size, in bytes, of the program as needed.
 *
 * Assumptions:
 * - At least one command will be passed to the program, where the least to be
 *   passed is the exit command itself.
 * - Any 'progID' not being used to print the ranges of unallocated pages will be
 *   greater than or equivalent to 0.
 * - The 'size' parameter, if not used for initiation, will be 0.
 * - The exit command identifies the end of the simulation regardless of other
  *  input lines available after it. */

import java.util.Scanner;

public class MemSim {
  
  // Constant Declaration
  private static final int
      NUM_PAGES = MemParam.NUM_PAGES, // Pages in system memory initially
      NUM_PROGRAMS = MemParam.NUM_PROGRAMS, // Max number of programs allowed
      MAX_SIZE = MemParam.MAX_SIZE,   // Maximum program size allowed
      PAGE_SIZE = MemParam.PAGE_SIZE; // Size of each page (in bytes)
  private static final byte
      PAGECNT = 0, // Index for number of memory pages left in system
      OPCODE = 1,  // Index for operation code
      PROGID = 2,  // Index for program identifier
      PRSIZE = 3;  // Index for program size

  public static void main(String[] args) {
    
    // Variable declaration
    List<PageUsage> freePages = new List<PageUsage>();
        // Available memory pages in-system
    ProgInfo[] progs = new ProgInfo[NUM_PROGRAMS];
        // Array of running programs
    Scanner kbd = new Scanner(System.in);
        // Allows reading of user input
    int[] params = new int[4];
        // Array of parameters used during operations
    
    // Initial setup for simulation - set defaults
    params[PAGECNT] = NUM_PAGES;
    freePages.add(1, new PageUsage(0, NUM_PAGES-1));
    for(int i=0; i<NUM_PROGRAMS; i++) {
      progs[i] = new ProgInfo();
      progs[i].bytes = -1;
    }

    // Continue execution of commands in the system
    // Execution terminates either when input ends or exit command is reached
    while(kbd.hasNext()) {
      params[OPCODE] = kbd.next().charAt(0); // Op code
      params[PROGID] = kbd.nextInt();  // Program identifier
      params[PRSIZE] = kbd.nextInt();  // Size of program

      if (params[PRSIZE] >= 0 && params[PRSIZE] <= MAX_SIZE) {
        switch((char)
          params[OPCODE]) {
          case 'i': progInitiate(freePages, progs, params); break;
          case 't':  progTerminate(freePages, progs, params); break;
          case 'p': progPrint(freePages, progs, params[PROGID]); break;
          case 'x': progExit(progs, params[PAGECNT]); break;
        }
      }
    }
    
    kbd.close();
    
  }

  /**
   * Attempts to allocate memory to initiate a program in the system.
   * @param freePages List of unallocated memory pages.
   * @param progs     Array of programs in the system.
   * @param params    Array containing the program ID, size, and other info.
   */
  public static void progInitiate(List<PageUsage> freePages, ProgInfo[] progs,
      int[] params) {

    int pgsFree = params[PAGECNT], // Number of pages not allocated to programs
        progId = params[PROGID],   // Identifier of the program to initiate
        size = params[PRSIZE];     // Size of the program to initiate

    if (!progExists(progs, progId)) {
      int pgsNeeded = (int)Math.ceil(1.0*size/PAGE_SIZE), // Program page req.
          pgEntries = freePages.size(); // Number of entries in freePages

      if (pgsFree >= pgsNeeded) {
        List<PageUsage> pages = new List<PageUsage>();
        params[PAGECNT] -= pgsNeeded;

        // Loop through all the entries of the pages array until either none or
        // left or enough pages have been found for the application.
        for (int i = 1; i <= pgEntries && pgsNeeded > 0; i++) {
          PageUsage pg = freePages.get(i);  // Current entry being worked with
          int pgStart = pg.getStart(),    // Start 'page' in memory
            pgEnd = pg.getEnd();          // Ending 'page' in memory

          // Add pages from the current entry or simply append entire entry
          // to list of pages to be allocated.
          if (pgEnd - pgStart > pgsNeeded) {
            pages.add(pages.size() + 1, new PageUsage(pgStart,
              pgStart + pgsNeeded - 1));
            pg.setStart(pgStart + pgsNeeded);
            pgsNeeded = 0;
          }
          else {
            pages.add(pages.size() + 1, pg);
            freePages.remove(i);
            pgEntries--;
            i--;
            pgsNeeded -= pgEnd - pgStart + 1;
          }
        }

        System.out.printf("Program %d initiated, size = %d\n", progId, size);
        progs[progId].bytes = size;
        progs[progId].prog_usage = pages;
      }
      else
        System.out.printf(
            "ERROR on initiate command. Insufficient space for Program %d\n",
            progId);
    }
    else
      System.out.printf(
          "ERROR on initiate command. Program %d already exists.\n", progId);
  
  }

  /**
   * Attempts to terminate a program already operating in the system.
   * @param freePages List of unallocated memory pages in the system.
   * @param progs     Array of programs in the system.
   * @param params    Array containing program id and other information.
   */
  public static void progTerminate(List<PageUsage> freePages,
      ProgInfo[] progs, int[] params) {

    int progId = params[PROGID];

    if (progExists(progs, progId)) {
      progs[progId].bytes = -1;
      List<PageUsage> pages = progs[progId].prog_usage;
      progs[progId].prog_usage = null;
      
      int pageCnt = 0;  // Number of entries in page list for program
      for(int i=pages.size(); i>0; i--) {
        PageUsage pg = pages.get(i);
        pageInsert(freePages, pg);
        pageCnt += (pg.getEnd() - pg.getStart() + 1);
      }
      params[PAGECNT] += pageCnt;
      
      System.out.printf(
          "Program %d terminated, %d pages freed\n", progId, pageCnt);
    }
    else
      System.out.printf(
        "ERROR on terminate command. Program %d does not exist.\n", progId);
  }

  /**
   * Prints either the list of unallocated memory pages or list of memory pages
   * allocated to a particular program.
   * @param freePages List of unallocated memory pages.
   * @param progs     Array of programs in the system.
   * @param progId    Program ID to print allocated pages of, or -1 for free
   *                  memory pages.
   */
  public static void progPrint(List<PageUsage> freePages, ProgInfo[] progs, int progId) {
    List<PageUsage> pages;

    if (progId < 0) {
      // Printing free pages list
      System.out.printf("Contents of Free Page List\n");
      pages = freePages;
    }
    else if (progExists(progs, progId)) {
      // Printing a program's list
      System.out.printf("Page usage for program %d --- size = %d bytes\n",
          progId, progs[progId].bytes);
      pages = progs[progId].prog_usage;
    }
    else {
      // Not printing free pages, but program with the given ID doesn't exist
      System.out.printf(
          "ERROR on print command: Program %d does not exist.\n", progId);
      return;
    }

    progPrintPages(pages);
  }

  /**
   * Prints a list of memory pages.
   * @param pages The list of memory pages to print.
   */
  public static void progPrintPages(List<PageUsage> pages) {
    int pgEntries = pages.size();
    System.out.println("Start Page   End Page");
    
    for(int i=1; i<=pgEntries; i++) {
      PageUsage pg = pages.get(i);
      System.out.printf("%6d      %6d\n", pg.getStart(), pg.getEnd());
    }
    
    System.out.println();
  }

  /**
   * Terminates the application after showing how many programs are still
   * operating in the system.
   * @param progs Array of programs in the system.
   * @param sysPgCount Number of pages left in the system
   */
  public static void progExit(ProgInfo[] progs, int sysPgCount) {
    int progCount = 0;  // Number of programs found to be operating
    
    for(int i=0; i<NUM_PROGRAMS; i++)
      if (progExists(progs, i))
        progCount++;
    
    System.out.printf(
        "SIMULATOR EXIT: %d programs exist, occupying %d pages.\n",
        progCount, NUM_PAGES-sysPgCount);

    System.exit(0);
  }

  /**
   * Checks if a program exists in the system with the given ID.
   * @param progs   Array of programs in the system.
   * @param progId  ID of the program to check the existence of.
   * @return True if the program exists, or false if not.
   */
  public static boolean progExists(ProgInfo[] progs, int progId) {
    return progs[progId].bytes >= 0;
  }

  /**
   * Attempts to insert a PageUsage object into a list of PageUsage objects.
   * @param pgList  List of memory pages to insert the object into.
   * @param pg      PageUsage object to be inserted into the list.
   * @return The index of where the page was inserted, or -1 if unsuccessful.
   */
  public static int pageInsert(List<PageUsage> pgList, PageUsage pg) {
    int listSize = pgList.size();

    for(int i=1; i<=listSize; i++)
      if (pgList.get(i).getStart() > pg.getStart()) {
        pgList.add(i, pg);
        return i;
      }

    return -1;
  }

}