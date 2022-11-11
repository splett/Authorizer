package com.main.model;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {

  private String merchant;
  private BigDecimal amount;
  private Date time;

  public String getMerchant() {
    return merchant;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Date getTime() {
    return time;
  }

  @Override
  public boolean equals(Object obj) {
    boolean result = false;

    if (this.getMerchant().equals(((Transaction) obj).getMerchant())
        && this.getAmount().equals(((Transaction) obj).getAmount()))
      result = true;

    return result;
  }
}
