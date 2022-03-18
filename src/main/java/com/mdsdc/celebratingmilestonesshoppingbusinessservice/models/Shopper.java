package com.mdsdc.celebratingmilestonesshoppingbusinessservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Shopper{
	private String ip;
	private String firstName;
	private String lastName;
	private String email;
	private String street;
	private String city;
	private String state;
	private int zip;
	private String date;
}