package com.example.demo.models;

import com.example.demo.dto.FaceDetectionResponse;

import lombok.Data;

@Data
public class Review{
	private String text;
	private User user;
	private FaceDetectionResponse faceDetectionResponse;
}
