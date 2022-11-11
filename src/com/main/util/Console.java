package com.main.util;

import java.util.Scanner;

public class Console {

  static Scanner scanner = new Scanner(System.in);
 
  /**
   * Shows a message on the console 
   * @param message String
   */
  public static void toShow(String message) {
    System.out.println(message);
  }
  
  /**
   * Returns the next value that user input 
   * @return value
   */
  public static String toReadNextValue() {
    return scanner.nextLine();
  }
}
