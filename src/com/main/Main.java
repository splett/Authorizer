package com.main;

import com.main.authorizer.Authorizer;
import com.main.model.Account;
import com.main.model.Transaction;
import com.main.util.Console;
import com.main.util.Error;
import com.main.util.JsonReader;

public class Main {

  private Authorizer authorizer;

  public Main() {
    authorizer = new Authorizer();
  }

  public static void main(String[] args) {
    new Main().toExecute();
  }

  /**
   * Launch the application
   */
  private void toExecute() {
    Console.toShow("Enter with JSON or press 0 to exit!");
    String userInput = new String("");
    do {
      try {
        userInput = Console.toReadNextValue();
     
        if (!userInput.equals("0"))
          toRealizeOperation(JsonReader.toReadJson(userInput));
      
      } catch (Exception e) {
        Console.toShow(e.getMessage());
      }

    } while (!userInput.equals("0"));
    Console.toShow("Authorizer finalized. Bye!");
  }

  /**
   * If the user entered one with an account JSON it will perform the account creation, 
   * if it is a Transaction JSON it will perform the transaction. 
   * If it is not one of these types, it displays a violation message
   * @param json Json
   */
  private void toRealizeOperation(Object json) {
    if (json instanceof Account)
      Console.toShow(authorizer.toCreateAccount((Account) json).toString());
    else if (json instanceof Transaction)
      Console.toShow(authorizer.toAuthorizeTransaction((Transaction) json).toString());
    else
      Console.toShow(Error.JSON_INVALID.getMessage());
  }
}
