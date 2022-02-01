
package com.example.demo.services;

import org.springframework.http.ResponseEntity;

import com.example.demo.dto.BusinessResponse;
	
public interface BusinessSearchService {
	
	public ResponseEntity<BusinessResponse> getBusinessesByCriteria(
			String term, String categories, String latitude, String longitude, String price, String location, String limit, String radius);
	
}