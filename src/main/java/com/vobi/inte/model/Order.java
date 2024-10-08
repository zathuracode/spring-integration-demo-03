package com.vobi.inte.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Order implements Serializable {
	
	private static final long serialVersionUID = 1L; // Agregar un serialVersionUID
	private int orderId;
	private String itemName;
	private double amount;
	private String orderStatus;
	
}
