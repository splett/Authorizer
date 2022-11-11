package com.test;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import com.main.authorizer.Authorizer;
import com.main.model.Account;
import com.main.model.Transaction;
import com.main.util.Error;
import com.main.util.JsonReader;

import junit.framework.TestCase;

public class AuthorizerTest extends TestCase {
  
  private Authorizer authorizer = new Authorizer();

  @Test
  @DisplayName("Test creation account sucess")
  public void testToValidateCreationAccount() {
    Account accountPost = (Account) JsonReader.toReadJson("{\"account\": {\"active-card\": true, \"available-limit\": 350}}");
    
    authorizer.toCreateAccount(accountPost);
   
    assertEquals(accountPost, authorizer.getAccount());
  }
  
  @Test
  @DisplayName("Test creation account already initialized")
  public void testToValidateCreationAccountAlreadyInitialized() {
    Account accountPost = (Account) JsonReader.toReadJson("{\"account\": {\"active-card\": true, \"available-limit\": 350}}");
    
    /* Creating the first account */
    Account accountRest =  authorizer.toCreateAccount(accountPost);
    /* Trying again */
    accountRest =  authorizer.toCreateAccount(accountPost);
    
    assertEquals(accountRest.getViolations().contains(Error.ACCOUNT_ALREADY_INITIALIZED.getMessage()), true);
  }
  
  @Test
  @DisplayName("Test to authorize transaction sucess")
  public void testToAuthorizeTransactionSucess() {
    Account accountPost = (Account) JsonReader.toReadJson("{\"account\": {\"active-card\": true, \"available-limit\": 350}}");
    BigDecimal availableLimitInitial = accountPost.getAvailableLimit();
    authorizer.toCreateAccount(accountPost);
   
    Transaction transactionPost = (Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\":\"2019-02-13T10:00:00.000Z\"}}");
    
    Account accountRest = authorizer.toAuthorizeTransaction(transactionPost);
    
    assertEquals(accountRest.getAvailableLimit(), availableLimitInitial.subtract(transactionPost.getAmount()));
  }
  
  @Test
  @DisplayName("Test to authorize transaction with card not active")
  public void testToAuthorizeTransactionCardNotActive() {
    Account accountPost = (Account) JsonReader.toReadJson("{\"account\": {\"active-card\": false, \"available-limit\": 350}}");
    
    authorizer.toCreateAccount(accountPost);
    
    Transaction transactionPost = (Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\":\"2019-02-13T10:00:00.000Z\"}}");
    
    Account accountRest = authorizer.toAuthorizeTransaction(transactionPost);
    
    assertEquals(accountRest.getViolations().contains(Error.CARD_NOT_ACTIVE.getMessage()), true);
  }
  
  @Test
  @DisplayName("Test to authorize transaction with insufficient limit")
  public void testToAuthorizeTransactionInsufficientLimit() {
    Account accountPost = (Account) JsonReader.toReadJson("{\"account\": {\"active-card\": true, \"available-limit\": 350}}");
    Transaction transactionPost = (Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 2000, \"time\":\"2019-02-13T10:00:00.000Z\"}}");
    
    authorizer.toCreateAccount(accountPost);
    
    Account accountRest = authorizer.toAuthorizeTransaction(transactionPost);
    
    assertEquals(accountRest.getViolations().contains(Error.INSUFFICIENT_LIMIT.getMessage()), true);
  }
  
  @Test
  @DisplayName("Test to authorize transaction with account not initialized")
  public void testToAuthorizeTransactionAccountNotInitialized() {
    Transaction transactionPost = (Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 2000, \"time\":\"2019-02-13T10:00:00.000Z\"}}");
       
    Account accountRest = authorizer.toAuthorizeTransaction(transactionPost);
    
    assertEquals(accountRest.getViolations().contains(Error.ACCOUNT_NOT_INITIALIZED.getMessage()), true);
  }
  
  @Test
  @DisplayName("Test to authorize transaction with double transactions")
  public void testToAuthorizeTransactionDoubleTransaction() {
    Account accountPost = (Account) JsonReader.toReadJson("{\"account\": {\"active-card\": true, \"available-limit\": 350}}");
    
    authorizer.toCreateAccount(accountPost);
    
    /* First transaction */
    Account accountRest = authorizer.toAuthorizeTransaction((Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\":\"2019-02-13T10:00:00.000Z\"}}"));
    
    /* Second transaction after one minute */
    accountRest = authorizer.toAuthorizeTransaction((Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\":\"2019-02-13T10:01:00.000Z\"}}"));
    
    assertEquals(accountRest.getViolations().contains(Error.DOUBLED_TRANSACTION.getMessage()), true);
  }
  
  @Test
  @DisplayName("Test to authorize transaction with high frequency in small interval")
  public void testToAuthorizeTransactionHighFrequencySmallInterval() {
    Account accountPost = (Account) JsonReader.toReadJson("{\"account\": {\"active-card\": true, \"available-limit\": 350}}");
    
    authorizer.toCreateAccount(accountPost);
    
    /* First transaction */
    Account accountRest = authorizer.toAuthorizeTransaction((Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\":\"2019-02-13T10:00:00.000Z\"}}"));
    
    /* Second transaction  */
    accountRest = authorizer.toAuthorizeTransaction((Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"MC Donalds\", \"amount\": 40, \"time\":\"2019-02-13T10:00:30.000Z\"}}"));
    
    /* Third transaction  */
    accountRest = authorizer.toAuthorizeTransaction((Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"Habbibs\", \"amount\": 20, \"time\":\"2019-02-13T10:01:00.000Z\"}}"));
    
    /* Fourth transaction  */
    accountRest = authorizer.toAuthorizeTransaction((Transaction) JsonReader.toReadJson("{\"transaction\": {\"merchant\": \"Madero\", \"amount\": 100, \"time\":\"2019-02-13T10:01:30.000Z\"}}"));
    
    assertEquals(accountRest.getViolations().contains(Error.HIGH_FREQUENCY_SMALL_INTERVAL.getMessage()), true);
  }
}
