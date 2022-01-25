package com.example.demo.contollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Business;
import com.example.demo.services.BusinessSearchService;

@RestController
@RequestMapping(produces = "application/json", consumes = MediaType.APPLICATION_JSON_VALUE)
public class BusinessSearchController {
	
		@Autowired
		private BusinessSearchService businessSearchService;

		@GetMapping("/get-business-by-criteria")
		public ResponseEntity<List<Business>> getBusinessesByCriteria(
				@RequestParam(name = "location", required = false) String location,
				@RequestParam(name = "categories", required = false) String categories,
				@RequestParam(name = "price", required = false) String price,
				@RequestParam(name = "longitude", required = false) String longitude,
				@RequestParam(name = "latitude", required = false) String latitude){
	        return businessSearchService.getBusinessesByCriteria(location,categories,latitude,longitude,price);
	    }
}
