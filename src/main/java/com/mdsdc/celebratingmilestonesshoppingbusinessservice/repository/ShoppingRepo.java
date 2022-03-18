package com.mdsdc.celebratingmilestonesshoppingbusinessservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import com.mdsdc.celebratingmilestonesshoppingbusinessservice.models.Cart;

@Service
public interface ShoppingRepo extends MongoRepository<Cart, String>{
	
}