package com.example;

public class Models {
  public static class User {
    public String id;
    public String name;
    public User(String id, String name) { this.id = id; this.name = name; }
    @Override public String toString() { return "User{id='" + id + "', name='" + name + "'}"; }
  }

  public static class Order {
    public String orderId;
    public String userId;
    public double amount;
    public Order(String orderId, String userId, double amount) {
      this.orderId = orderId; this.userId = userId; this.amount = amount;
    }
    @Override public String toString() { return "Order{orderId='" + orderId + "', userId='" + userId + "', amount=" + amount + "}"; }
  }
}
