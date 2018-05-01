// CSCI 2540 Programming Assignment 03
// Title:   MemSim.java
// Date:    30 October 2017
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
 * - 's' for shrink       - 'g' for grow
 * The 'progID' is the identifying number of the program to execute the command
 * for, and 'size' is the size, in bytes, as needed.
 *
 * Assumptions:
 * - At least one command will be passed to the program, where the least to be
 *   passed is the exit command itself.
 * - Any 'progID' not being used to print the ranges of unallocated pages will
 *   be greater than or equivalent to 0.
 * - The exit command identifies the end of the simulation regardless of other
 *   input lines available after it. It will always be present in the input.
 *
 * Error Checking:
 * - Initiation
 *   * A program exists that already has the specified program identifier.
 *   * Not enough space exists to initiate the program.
 * - Termination
 *   * A program doesn't exist that has the program identifier.
 * - Printing
 *   * A program doesn't exist that has the program identifier.
 * - Grow
 *   * A program doesn't exist that has the program identifier.
 *   * Not enough space exists to grow the program.
 *   * Growing the program would make it too large for the system.
 * - Shrink
 *   * A program doesn't exist that has the program identifier.
 *   * The program doesn't have enough memory to be shrunk. */

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
    do {
      params[OPCODE] = kbd.next().charAt(0); // Op code
      params[PROGID] = kbd.nextInt();  // Program identifier
      params[PRSIZE] = kbd.nextInt();  // Size of program

      switch ((char) params[OPCODE]) {
        case 'i': progInitiate(freePages, progs, params); break;
        case 't': progTerminate(freePages, progs, params); break;
        case 'p': progPrint(freePages, progs, params[PROGID]); break;
        case 's': progShrink(freePages, progs, params); break;
        case 'g': progGrow(freePages, progs, params); break;
        case 'x': progExit(progs, params[PAGECNT]); break;
        default:
          System.out.printf("ERROR: Command '%s' not recognized!\n",
            (char) params[OPCODE]);
      }
    }
    while(params[OPCODE] != 'x');
    
    kbd.close();
    
  }

  /**
   * Allocates more memory for a program if the system has enough and the
   * program will remain under the maximum size allowed.
   * @param freePages A list of pages available to the system.
   * @param progs     An array of programs currently in the system.
   * @param params    Array of parameters for processing a program's growth.
   */
  public static void progGrow(List<PageUsage> freePages, ProgInfo[] progs,
      int[] params) {

    int pgsFree = params[PAGECNT],  // Number of pages still in system
        progId = params[PROGID],    // Program to be operated on
        size = params[PRSIZE]+progs[progId].bytes; // New program size

    if (sysHasProg(progs, progId)) {
      int pgsNeeded = sysCalcPages(size) - sysProgPages(progs[progId]);
          // Number of pages still needed for expansion

      // Check if system has enough pages to allocate to the program
      // Also check to ensure program will remain small enough for the system
      if (sysHasPages(pgsFree, pgsNeeded)) {
        if (size <= MAX_SIZE) {
          // Allocate pages for size increase and update program information
          params[PAGECNT] -= pgsNeeded;
          progs[progId].bytes = size;
          List<PageUsage> append = sysGenList(freePages, pgsNeeded);

          // Insert pages into program-usage list in ascending order
          for (int i = append.size(); i > 0; i--) {
            pageInsert(progs[progId].prog_usage, append.get(i));
            append.remove(i);
          }

          System.out.printf("Program %d increased by %d bytes, new size"+
                  " = %d\n", progId, params[PRSIZE], size);
        }
        else
          System.out.printf("ERROR on grow command: MAX SIZE exceeded for"+
                  " Program %d.\n", progId);
      }
      else
        System.out.printf("ERROR on grow command: insufficient space for "+
                  "Program %d.\n", progId);
    }
    else
      System.out.printf("ERROR on grow command: Program %d does not exist.\n",
          progId);
  }

  /**
   * Shrinks a program's size by the number of bytes specified.
   * @param freePages The list of free pages (pages still in the system).
   * @param progs     Array of programs running in the system.
   * @param params    Parameters necessary for the shrinking operation.
   */
  public static void progShrink(List<PageUsage> freePages, ProgInfo[] progs,
      int[] params) {

    int progId = params[PROGID], // ID of program to be operated on
        size = params[PRSIZE];   // Size to shrink the program by

    if (sysHasProg(progs, progId)) {
      if (size <= progs[progId].bytes) {
        int pgsNeeded = sysProgPages(progs[progId])
            -sysCalcPages(progs[progId].bytes-size); // Pages to deallocate
        List<PageUsage> progList = progs[progId].prog_usage;
            // List of pages allocated to program

        progs[progId].bytes -= size;
        params[PAGECNT] += pgsNeeded;

        // Loop through all entries in the program's allocation list until
        // the end of the list is reached or all pages have been deallocated
        for(int i=progList.size(); i>=1 && pgsNeeded!=0; i--) {
          PageUsage pg = progList.get(i); // Current page being looked at

          if (pg.getEnd()-pg.getStart()+1 > pgsNeeded) {
            // More than enough pages were located
            pageInsert(freePages, new PageUsage(pg.getEnd()-pgsNeeded+1,
                pg.getEnd()));
            pg.setEnd(pg.getEnd()-pgsNeeded);
            pgsNeeded = 0;
          }
          else {
            // Less than or the amount of pages needed were found
            pgsNeeded -= (pg.getEnd()-pg.getStart()+1);
            pageInsert(freePages, pg);
            progList.remove(i);
          }
        }

        System.out.printf("Program %d decreased by %d bytes, new size = %d\n",
            progId, size, progs[progId].bytes);
      }
      else
        System.out.printf("ERROR on shrink command: insufficient allocation "+
            "for Program %d.\n", progId);
    }
    else
      System.out.printf("ERROR on shrink command: Program %d does not exist.\n"
          ,progId);

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

    if (!sysHasProg(progs, progId)) {
      int pgsNeeded = sysCalcPages(size);
      if (sysHasPages(pgsFree, pgsNeeded)) {
        List<PageUsage> pages = sysGenList(freePages, pgsNeeded);
        params[PAGECNT] -= pgsNeeded;
        System.out.printf("Program %d initiated, size = %d\n", progId, size);
        progs[progId].bytes = size;
        progs[progId].prog_usage = pages;
      }
      else
        System.out.printf(
            "ERROR on initiate command. Insufficient space for Program %d.\n",
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

    if (sysHasProg(progs, progId)) {
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
          "Program %d terminated, %d pages freed.\n", progId, pageCnt);
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
  public static void progPrint(List<PageUsage> freePages, ProgInfo[] progs,
      int progId) {

    List<PageUsage> pages;

    if (progId < 0) {
      // Printing free pages list
      System.out.printf("Contents of Free Page List\n");
      pages = freePages;
    }
    else if (sysHasProg(progs, progId)) {
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

    sysPrintPages(pages);
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
      if (sysHasProg(progs, i))
        progCount++;
    
    System.out.printf(
        "SIMULATOR EXIT: %d program(s) exist, occupying %d pages.\n",
        progCount, NUM_PAGES-sysPgCount);
  }

  /**
   * Attempts to insert a PageUsage object into a list of PageUsage objects.
   * @param pgList  List of memory pages to insert the object into.
   * @param pg      PageUsage object to be inserted into the list.
   * @return The index of where the page was inserted.
   */
  public static int pageInsert(List<PageUsage> pgList, PageUsage pg) {
    int listSize = pgList.size();

    for(int i=1; i<=listSize; i++)
      if (pgList.get(i).getStart() > pg.getStart()) {
        pgList.add(i, pg);
        return i;
      }

    pgList.add(listSize+1, pg);
    return listSize+1;
  }

  /**
   * Checks if a program exists in the system with the given ID.
   * @param progs   Array of programs in the system.
   * @param progId  ID of the program to check the existence of.
   * @return True if the program exists, or false if not.
   */
  public static boolean sysHasProg(ProgInfo[] progs, int progId) {
    return progs[progId].bytes >= 0;
  }
  
  /**
   * Checks whether the system has enough free pages to meet demand.
   * @param sysPages    Number of free pages in the system.
   * @param neededPages Number of pages needed.
   * @return True if enough pages are available, or false if not.
   */
  public static boolean sysHasPages(int sysPages, int neededPages) {
    return sysPages >= neededPages;
  }

  /**
   * Computes the number of pages currently allocated to a program.
   * @param program The program to calculate memory allocation for.
   * @return The number of pages allocated to the program currently.
   */
  public static int sysProgPages(ProgInfo program) {
    List<PageUsage> pages = program.prog_usage; // Program allocation list
    int pageCount = 0;  // Current count of pages in program

    // Loop through all entries of program allocation list
    for(int i=1; i<=pages.size(); i++) {
      PageUsage pg = pages.get(i);
      pageCount += pg.getEnd() - pg.getStart() + 1;
    }

    return pageCount;
  }

  /**
   * Generates a list of PageUsage objects that contain the number of needed
   * pages from the list of free pages.
   * @param free  List of PageUsage objects representing pages in the system.
   * @param need  The number of pages to have in the final list.
   * @return List of PageUsage objects whose page count is that of 'need.'
   */
  public static List<PageUsage> sysGenList(List<PageUsage> free, int need) {
    List<PageUsage> pages = new List<PageUsage>();

    // Loop through all the entries of the free pages list until either none or
    // left or enough pages have been found for the application.
    for (int i = 1; i <= free.size() && need > 0; i++) {
      PageUsage pg = free.get(i);  // Current entry being worked with
      int pgStart = pg.getStart(), // Start 'page' in memory
        pgEnd = pg.getEnd();       // Ending 'page' in memory

      // Add pages from the current entry or simply append entire entry
      // to list of pages to be allocated.
      if (pgEnd - pgStart + 1 > need) {
        pages.add(pages.size() + 1, new PageUsage(pgStart,
          pgStart + need - 1));
        pg.setStart(pgStart + need);
        need = 0;
      }
      else {
        pages.add(pages.size() + 1, pg);
        free.remove(i);
        i--;
        need -= pgEnd - pgStart + 1;
      }
    }

    return pages;
  }

  /**
   * Prints a list of memory pages.
   * @param pages The list of memory pages to print.
   */
  public static void sysPrintPages(List<PageUsage> pages) {
    int pgEntries = pages.size();
    System.out.println("Start Page   End Page");

    for(int i=1; i<=pgEntries; i++) {
      PageUsage pg = pages.get(i);
      System.out.printf("%6d      %6d\n", pg.getStart(), pg.getEnd());
    }

    System.out.println();
  }

  /**
   * Calculates the number of pages required for the given number of bytes.
   * @param bytes The number of bytes needed.
   * @return The number of pages the number of bytes would consume.
   */
  public static int sysCalcPages(int bytes) {
    return (int)Math.ceil(1.0*bytes/PAGE_SIZE);
  }

}