package com.mdsdc.celebratingmilestonesshoppingbusinessservice.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "cart")
public class Cart{
	@Id
	private String id;
	private LocalDateTime timeCreated;
	private List<CartItem> items;
}