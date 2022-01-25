package com.example.demo.contollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Business;
import com.example.demo.services.BusinessSearchService;

@RestController
public class BusinessSearchController {
	
		@Autowired
		private BusinessSearchService businessSearchService;
		
		@GetMapping("/get-business-by-criteria")
		public ResponseEntity<List<Business>> getBusinessesByCriteria(
				@RequestParam(name = "term", required = false, defaultValue = "") String term,
				@RequestParam(name = "categories", required = false, defaultValue = "") String categories,
				@RequestParam(name = "longitude", required = false, defaultValue = "") String longitude,
				@RequestParam(name = "latitude", required = false, defaultValue = "") String latitude,
				@RequestParam(name = "price", required = false, defaultValue = "") String price){
			return businessSearchService.getBusinessesByCriteria(term, categories, latitude, longitude, price);

        }
}
