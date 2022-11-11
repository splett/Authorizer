package com.main.authorizer;

import java.util.List;

import com.main.model.Account;
import com.main.model.Transaction;

public class Authorizer {

  private Account account;
  private Validator validator;

  public Authorizer() {
    validator = new Validator(this);
  }

  /**
   * Performs account creation
   * @param account {@link Account}
   * @return {@link Account} Account
   */
  public Account toCreateAccount(Account account) {
    List<String> violations = validator.toValidateCreationAccount();

    if (violations.isEmpty()) {
      this.account = account;
      return this.account;
    }
    else 
      return toAddViolations(violations);
  }

  /**
   * Performs a transaction
   * @param transaction Transaction
   * @return {@link Account} Account
   */
  public Account toAuthorizeTransaction(Transaction transaction) {
    List<String> violations = validator.toValidateTransaction(transaction);

    if (violations.isEmpty()) {
      this.account.toRealizeTransaction(transaction);
      this.account.setViolations(null);
      return this.account;
    } 
    else
      return toAddViolations(violations);
        
  }

  /**
   * Adds validation messages to show the user 
   * @param  violations Violations
   * @return {@link Account} Account
   */
  private Account toAddViolations(List<String> violations) {
    Account account = this.account;
    
    if(account == null) {
      account = new Account(); 
    }        
    
    account.setViolations(violations); 
    return account;
  }

  public Account getAccount() {
    return account;
  }

}
