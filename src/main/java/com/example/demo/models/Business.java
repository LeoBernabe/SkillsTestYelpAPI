package com.example.demo.models;

import java.util.List;

import lombok.Data;

@Data
public class Business {
	 private String id;
	 private String name;
	 private Object location;
	 private Object coordinates;
	 private Object categories;
	 private String price;
	 private List<Review> reviews;
}
