package com.example.demo.services;

import java.util.Collections;
import java.util.List;
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
			List<Review> reviewList = result.getReviews();
			for (Review review : reviewList) {			
				review.setFaceDetectionResponse(getFaceDetection(review.getUser().getImage_url()));
			}
			return reviewList;
		}catch (final HttpClientErrorException e) {
			e.printStackTrace();
				log.info(e.getStatusCode().toString() + " " + e.getResponseBodyAsString());
			    return null;  
		}
	}
	
	public ResponseEntity<List<Business>> getBusinessesByCriteria(String term, String latitude, String longitude) {
		try {
			String url = "https://api.yelp.com/v3/businesses/search";				
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
					.queryParam("term", term)
			        .queryParam("latitude", latitude)
			        .queryParam("longitude", longitude);
			
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
			e.printStackTrace();
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
			
			List<FaceAnnotation> faceAnnotationList = response.getFaceAnnotationsList();
			for (FaceAnnotation fa : faceAnnotationList) {
				faceDetectionResponse.setJoyLikelihood(fa.getJoyLikelihood().toString());
				faceDetectionResponse.setSorrowLikelihood(fa.getSorrowLikelihood().toString());
				faceDetectionResponse.setAngerLikelihood(fa.getAngerLikelihood().toString());
				faceDetectionResponse.setSurpriseLikelihood(fa.getSurpriseLikelihood().toString());
			}
			return faceDetectionResponse;
		}
		return new FaceDetectionResponse();
		
	}

}
