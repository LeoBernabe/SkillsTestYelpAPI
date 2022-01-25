package com.example.demo.models;

import java.util.List;

import lombok.Data;

@Data
public class Business {
	 private String id;
	 private String name;
	 private List<Review> reviews;
}
