package com.example.demo.contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BusinessResponse;
import com.example.demo.services.BusinessSearchService;

@RestController
public class BusinessSearchController {
	
		@Autowired
		private BusinessSearchService businessSearchService;
		
		@GetMapping("/get-business-by-criteria")
		public ResponseEntity<BusinessResponse> getBusinessesByCriteria(
				@RequestParam(name = "term", required = false, defaultValue = "") String term,
				@RequestParam(name = "categories", required = false, defaultValue = "") String categories,
				@RequestParam(name = "longitude", required = false, defaultValue = "") String longitude,
				@RequestParam(name = "latitude", required = false, defaultValue = "") String latitude,
				@RequestParam(name = "price", required = false, defaultValue = "") String price,
				@RequestParam(name = "location", required = false, defaultValue = "") String location,
				@RequestParam(name = "limit", required = false, defaultValue = "") String limit,
				@RequestParam(name = "radius", required = false, defaultValue = "") String radius){
			return businessSearchService.getBusinessesByCriteria(term, categories, latitude, longitude, price, location, limit, radius);

        }
}
