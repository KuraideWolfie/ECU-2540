// CSCI 2540 Programming Assignment 04
// Title:   InfToPost.java
// Date:    8 November 2017
// Author:  Matthew Morgan
/* Description:
 * InfToPost computes a series of postfix expressions that are converted from
 * infix expressions passed via input. Values utilized to evaluate these
 * expressions are passed in via input before the series of expressions.
 *
 * Input Format:
 * Input is passed in via two phases: assignment and postfix evaluations. For
 * the first phase, format should be as follows: <char> = <value>, where
 * <char> is any capitalized character of the alphabet, and <value> is any
 * integer value. For example, 'A = -2.' For postfix evaluation, input must
 * be valid infix expressions. For example, '(A-B)*C+D.'
 *
 * Assumptions:
 * - Input will contain the line "$PART2" between assignment statements and
 *   infix expressions.
 * - All assignment operations will specify a capital letter, such as 'A', and
 *   not lowercase equivalents such as 'a'.
 */

import java.util.*;

public class InfToPost {
  
  public static void main(String[] args) {
    
    int[] charValues = new int[26]; // Array storing values of each character
    Scanner keyboard = new Scanner(System.in); // Redirects to system input
    boolean assnPhase = true; // Toggles assignment/postfix evaluation phases

    // Set the default values for characters to their placement in the alphabet
    for(int i=0; i<25; i++)
      charValues[i] = i+1;
    
    while(keyboard.hasNext()) {
      // The next line of input to be processed with all spaces removed
      String ln = keyboard.nextLine().replaceAll(" ", "");
      
      // Changes phase from assignment to postfix evaluation when a
      // particular line is found
      if (ln.equals("$PART2")) {
        assnPhase = false;
        continue;
      }
      
      // Checks whether to perform a character value assignment or evaluation
      // of a postfix expression
      if (assnPhase)
        charValues[addr(ln.charAt(0))] =
            Integer.parseInt(ln.substring(2, ln.length()));
      else {
        // Generates postfix expression, then prints the infix, postfix, and
        // result of the postfix
        String post = inToPost(ln);
        System.out.println("Infix: " + ln);
        System.out.println("Postfix: " + post);
        System.out.println("Result: " + evalPost(post, charValues));
        System.out.println();
      }
      
    }
    
    keyboard.close();
    
  }
  
  /**
   * Computes a numerical index for usage in the 'charValues' array to the
   * character specified as a parameter.
   * @param ch The character to compute the index of
   * @return The index of the character in the 'charValues' array
   */
  
  public static int addr(char ch) { return (int)ch - (int)'A'; }
  
  /**
   * Generates the postfix expression equivalent of an infix expression.
   * @param infix The infix expression to be converted
   * @return A string representing the postfix expression
   */
  
  public static String inToPost(String infix) {
    String postString = ""; // Postfix form of the infix expression
    Stack<Character> post = new Stack<Character>(); // Holds ops and operands
    
    // Processes all characters in the infix expression to generate
    // the postfix expression equivalent
    for(int i=0; i<infix.length(); i++)
      switch(infix.charAt(i)) {
        case '(':
          // Opening parenthesis
          post.push(infix.charAt(i));
          break;
        case ')':
          // Closing parenthesis
          while(!(post.peek() == '('))
            postString += post.pop();

          post.pop();
          break;
        case '-': case '+': case '*': case '/':
          // Operator
          char op = infix.charAt(i);
          
          while(!post.isEmpty() && !(post.peek()=='(') &&
              prec(op)<=prec(post.peek()))
            postString = postString + post.pop();
            
          post.push(op);
          break;
        default:
          // Operand
          postString = postString + infix.charAt(i);
      }
      
      // Pops the remaining characters on the stack into the postfix
      // expression string
      while(!post.isEmpty())
        postString = postString + post.pop();
      
      return postString;
  }
  
  /**
   * Evaluates a postfix expression.
   * @param post String representing the postfix expression
   * @param val Array of character values for usage in evaluation
   * @return The final result of the postfix expression
   */
  
  public static int evalPost(String post, int[] val) {
    Stack<Integer> operands = new Stack<Integer>(); // Holds values during eval
    
    // Loop through all characters of the postfix expression to evaluate
    for(int i=0; i<post.length(); i++)
      switch(post.charAt(i)) {
        case '+': case '-': case '*': case '/':
          // Operator : Pop two operands and push the result to the stack
          int a=operands.pop(), b=operands.pop();
          
          switch(post.charAt(i)) {
            case '+': operands.push(a+b); break;
            case '-': operands.push(b-a); break;
            case '*': operands.push(a*b); break;
            case '/': operands.push(b/a); break;
          }
          break;
        default:
          // Operand : Push to the stack
          operands.push(val[addr(post.charAt(i))]);
      }
    
    // Return the result of the evaluated expression
    return operands.pop();
  }
  
  /**
   * Returns the precedence of a given operator.
   * @param op The operator being looked at
   * @return The precedence of the operator specified
   */

  public static byte prec(char op) {
    switch(op) {
      // Addition and subtraction operators
      case '+': case '-':
        return 0;
      // Division and multiplication operators
      case '/': case '*':
        return 1;
      // Grouping operator
      case '(':
        return 2;
      // Unknown operators
      default:
        return -1;
    }
  }
  
}