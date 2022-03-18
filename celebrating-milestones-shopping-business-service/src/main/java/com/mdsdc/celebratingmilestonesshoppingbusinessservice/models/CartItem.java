package com.mdsdc.celebratingmilestonesshoppingbusinessservice.models;

import lombok.Data;

@Data
public class CartItem {
	private int itemId;
	private String itemName;
	private String desc;
	private int quantity;
	private double price;
	private String img;
}
