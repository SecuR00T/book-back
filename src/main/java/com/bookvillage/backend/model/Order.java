package com.bookvillage.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    public String id;
    public String orderNumber;
    public String customerId;
    public String customerName;
    public List<OrderItem> items = new ArrayList<>();
    public int totalAmount;
    public String paymentStatus;
    public String fulfillmentStatus;
    public DeliveryInfo delivery;
    public String createdAt;
    public String paidAt;
}
