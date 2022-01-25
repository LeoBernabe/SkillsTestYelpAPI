package com.example.demo.dto;

import java.util.List;

import com.example.demo.models.Review;

import lombok.Data;

@Data
public class ReviewResponse{
	private List<Review> reviews;
}
