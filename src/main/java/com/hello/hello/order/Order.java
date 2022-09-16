package com.hello.hello.order;

import com.google.gson.JsonObject;
import com.hello.hello.endpoints.database.DatabaseConnection;

/**
 * This is Order object to make select save information a make JSON which will be sent to backend
 */

public class Order {

    public JsonObject Json = new JsonObject();

	public Order(String id, String name, String price, String userName, String Products){
        Json.addProperty("id", id);
        Json.addProperty("name", name);
        Json.addProperty("summaryprice", price);
        Json.addProperty("login", userName);
        Json.add("productcollection", getProductNames(Products));
    }
	
	private JsonObject getProductNames(String productId) {
        DatabaseConnection dbsConn = new DatabaseConnection();
        JsonObject productNames = dbsConn.getData("SELECT name, description FROM products WHERE serialnumber = ANY(ARRAY[" + productId + "])");
        return productNames;
	}
}