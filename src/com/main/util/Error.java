package com.main.util;

public enum Error {

  ACCOUNT_ALREADY_INITIALIZED("account-already-initialized"),
  ACCOUNT_NOT_INITIALIZED("account-not-initialized"),
  CARD_NOT_ACTIVE("card-not-active"),
  INSUFFICIENT_LIMIT("insufficient-limit"),
  HIGH_FREQUENCY_SMALL_INTERVAL("high-frequency-small-interval"),
  DOUBLED_TRANSACTION("doubled-transaction"),
  JSON_INVALID("JSON invalid or not supported in this version.");
  
  private String message;

  Error(String message) {
      this.message = message;
  }

  public String getMessage() {
      return message;
  }
}
