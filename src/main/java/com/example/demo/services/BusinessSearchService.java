
package com.example.demo.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.demo.models.Business;
	
public interface BusinessSearchService {
	
	public ResponseEntity<List<Business>> getBusinessesByCriteria(String term, String latitude, String longitude);
	
}