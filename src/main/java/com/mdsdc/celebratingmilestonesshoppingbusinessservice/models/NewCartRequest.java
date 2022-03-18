package com.mdsdc.celebratingmilestonesshoppingbusinessservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewCartRequest{
	private String ip;
	private CartItem item;
}