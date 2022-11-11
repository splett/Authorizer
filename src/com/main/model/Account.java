package com.main.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account {

  @Expose
  @SerializedName("active-card")
  private Boolean activeCard;

  @Expose
  @SerializedName("available-limit")
  private BigDecimal availableLimit;

  @Expose
  private List<String> violations;

  private List<Transaction> transactions;
  
  public Account() {
    transactions = new ArrayList<Transaction>();
  }
  
  public Boolean getActiveCard() {
    return activeCard;
  }

  public BigDecimal getAvailableLimit() {
    return availableLimit;
  }

  public void setViolations(List<String> violations) {
    this.violations = violations;
  }

  public List<String> getViolations() {
    return violations;
  }

  public void toRealizeTransaction(Transaction transaction) {
    this.availableLimit = this.availableLimit.subtract(transaction.getAmount());
    this.transactions.add(transaction);
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }
  
  @Override
  public String toString() {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    return "{\"account\":" + gson.toJson(this) + "}";
  }
  
}
