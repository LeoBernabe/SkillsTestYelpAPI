package com.example.demo.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.dto.BusinessResponse;
import com.example.demo.dto.FaceDetectionResponse;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.models.Business;
import com.example.demo.models.Review;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;

@Service
public class BusinessSearchServiceImpl implements BusinessSearchService {
	private static final Logger log = Logger.getLogger(String.valueOf(BusinessSearchService.class));

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private CloudVisionTemplate cloudVisionTemplate;

	@Value("${skill.test.API_KEY}")
	private String API_KEY;
	
    private HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
        return new HttpEntity<>(headers);
    }
    
	private List<Review> getBusinessReviewsById(String businessId) {
		try {
			String uri = "https://api.yelp.com/v3/businesses/" + businessId + "/reviews";		
			ResponseEntity<ReviewResponse> resultResponseEntity = restTemplate.exchange(uri, HttpMethod.GET, getHttpEntity(), ReviewResponse.class);		
			ReviewResponse result =  resultResponseEntity.getBody();
			List<Review> reviewList = result.getReviews().stream().map(review -> {
				review.setFaceDetectionResponse(getFaceDetection(review.getUser().getImage_url()));
				return review;
			}).collect(Collectors.toList());
			return reviewList;
		}catch (final HttpClientErrorException e) {
			log.info(e.getStatusCode().toString() + " " + e.getResponseBodyAsString());
		    return null;  
		}
	}
	
	public ResponseEntity<List<Business>> getBusinessesByCriteria(String term, String categories, String latitude, String longitude, String price) {
		try {
			String url = "https://api.yelp.com/v3/businesses/search";		

			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			if(!term.isBlank())
				params.add("term", term);
			if(!categories.isBlank())
				params.add("categories", categories);
			if(!latitude.isBlank())
				params.add("latitude", latitude);
			if(!longitude.isBlank())
				params.add("longitude", longitude);
			if(!price.isBlank())
				params.add("price", price);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParams(params);
			
			ResponseEntity<BusinessResponse> resultResponseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, getHttpEntity(), BusinessResponse.class);	
			
			BusinessResponse result = resultResponseEntity.getBody();
			List<Business> businessList = result.getBusinesses().stream().map(business -> {
				List<Review> reviews = getBusinessReviewsById(business.getId());
				business.setReviews(reviews);
				business.setId(business.getId());
				business.setName(business.getName());
				return business;
			}).collect(Collectors.toList());	
			
			return new ResponseEntity<List<Business>>(businessList, HttpStatus.OK);
		}catch (final HttpClientErrorException e) {
		    log.info(e.getStatusCode().toString() + " " + e.getResponseBodyAsString());
			return new ResponseEntity<List<Business>>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
		}
	}
    
    
    public FaceDetectionResponse getFaceDetection(String imageUrl) {
		FaceDetectionResponse faceDetectionResponse = new FaceDetectionResponse();
		
		if(!StringUtils.isBlank(imageUrl)) {
			Resource imageResource = this.resourceLoader.getResource(imageUrl);
			AnnotateImageResponse response = this.cloudVisionTemplate.analyzeImage(imageResource,
					Feature.Type.FACE_DETECTION);
			
			
			// get the first face detection in the data
			Optional<FaceAnnotation> faceAnnotation = response.getFaceAnnotationsList().stream().findFirst();
			if(faceAnnotation.isPresent()) {
				faceDetectionResponse.setJoyLikelihood(faceAnnotation.get().getJoyLikelihood().toString());
				faceDetectionResponse.setSorrowLikelihood(faceAnnotation.get().getSorrowLikelihood().toString());
				faceDetectionResponse.setAngerLikelihood(faceAnnotation.get().getAngerLikelihood().toString());
				faceDetectionResponse.setSurpriseLikelihood(faceAnnotation.get().getSurpriseLikelihood().toString());
			}
			
			return faceDetectionResponse;
		}
		return new FaceDetectionResponse();
		
	}

}
