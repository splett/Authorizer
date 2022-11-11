package com.main.util;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.main.model.Account;
import com.main.model.Transaction;

public class JsonReader {

  public static final String TRANSACTION = "transaction"; 
  public static final String ACCOUNT = "account"; 
  
  /**
   * Reads JSON. If it starts with "transaction" it returns an object of type {@link Transaction}, 
   * If it starts with "account" it returns an object of type {@link Account}. If it doesn't fit either, returns null.
   * @param json String 
   * @return {@link Object} Json Entity
   */
  public static Object toReadJson(String json) {
    if (json.startsWith("{\"" + TRANSACTION))
      return new Gson().fromJson(new JsonParser().parse(json).getAsJsonObject().get(TRANSACTION), Transaction.class);
    else if (json.trim().startsWith("{\"" + ACCOUNT))
      return new Gson().fromJson(new JsonParser().parse(json).getAsJsonObject().get(ACCOUNT), Account.class);
    return null;
  }
  
}
