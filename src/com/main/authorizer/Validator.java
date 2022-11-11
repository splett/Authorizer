package com.main.authorizer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.main.model.Transaction;
import com.main.util.Error;

public class Validator {

  private Authorizer authorizer;
  private List<String> violations;
  private static final int MINUTES_COMPARE = 2;
  private static final int MAX_TRANSACTIONS = 3;

  public Validator(Authorizer authorizer) {
    this.authorizer = authorizer;
  }

  /**
   * Performs validations to verify account creation
   * @return {@link List} violations
   */
  public List<String> toValidateCreationAccount() {
    violations = new ArrayList<String>();

    if (isAccountCreated())
      violations.add(Error.ACCOUNT_ALREADY_INITIALIZED.getMessage());

    return violations;
  }

  /**
   * Performs validations to check the completion of a transaction
   * @param transaction Trasaction
   * @return {@link List} violations
   */
  public List<String> toValidateTransaction(Transaction transaction) {
    violations = new ArrayList<String>();

    toValidateAccountInitialized();
    toValidateCardActive();
    toValidateInsufficientLimit(transaction);
    toValidateHighFrequencySmallInterval(transaction);
    toValidateDoubleTransaction(transaction);

    return violations;
  }

  /**
   * Validates if the account has already been created, if it has already been added, add a violation message.
   */
  private void toValidateAccountInitialized() {
    if (!isAccountCreated())
      violations.add(Error.ACCOUNT_NOT_INITIALIZED.getMessage());
  }

  /**
   * Validates that the card is active, if not, add a violation message.
   */
  private void toValidateCardActive() {
    if (isAccountCreated() && !authorizer.getAccount().getActiveCard())
      violations.add(Error.CARD_NOT_ACTIVE.getMessage());
  }

  /**
   * Validates if it is sufficient to carry out the transaction, if not, add a violation message.
   * @param transaction Transaction
   */
  private void toValidateInsufficientLimit(Transaction transaction) {
    if (isAccountCreated() && authorizer.getAccount().getAvailableLimit().compareTo(transaction.getAmount()) < 0)
      violations.add(Error.INSUFFICIENT_LIMIT.getMessage());
  }

  /**
   * Validate duplicate transaction (same merchant and and amount) in less than 2 minutes, if has any, add a violation message.
   * @param transaction Transaction
   */
  private void toValidateDoubleTransaction(Transaction transaction) {
    Date dateTimeCompare = toCalculateMinimumDate(transaction.getTime());
   
    if (isAccountCreated())
      for (int i = 0; i < authorizer.getAccount().getTransactions().size(); i++)
        if (authorizer.getAccount().getTransactions().get(i).getTime().compareTo(dateTimeCompare) > 0 && authorizer.getAccount().getTransactions().get(i).equals(transaction))
          violations.add(Error.DOUBLED_TRANSACTION.getMessage());
  }

  /**
   * Validates high frequency of transactions, if there are more than 3 in the same minute of the new transaction, add a violation message.
   * @param transaction Transaction
   */
  private void toValidateHighFrequencySmallInterval(Transaction transaction) {
    Date dateTimeCompare = toCalculateMinimumDate(transaction.getTime());
    Integer countTrasactions = 0;
    
    if (isAccountCreated())
      for (int i = 0; i < authorizer.getAccount().getTransactions().size(); i++)
        if (authorizer.getAccount().getTransactions().get(i).getTime().compareTo(dateTimeCompare) > 0)
          ++countTrasactions;

    if (countTrasactions >= MAX_TRANSACTIONS)
      violations.add(Error.HIGH_FREQUENCY_SMALL_INTERVAL.getMessage());
  }

  /**
   * Verify if the account has already been created 
   * @return {@link Boolean} true if the account has already been created 
   */
  private Boolean isAccountCreated() {
    return authorizer.getAccount() != null;
  }

  /**
   * Performs the calculation of the minimum period for validations of duplicate transactions and transactions with high frequency in a short period. Transaction date minus 2 minutes.
   * @param date Transaction date 
   * @return {@link Date} Transaction date minus 2 minutes 
   */
  private Date toCalculateMinimumDate(Date date) {
    Calendar minimumDate = Calendar.getInstance();
    minimumDate.setTime(date);
    minimumDate.add(Calendar.MINUTE, -MINUTES_COMPARE);

    return minimumDate.getTime();
  }
}
