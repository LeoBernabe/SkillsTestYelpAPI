package com.example.demo.dto;

import java.util.List;

import com.example.demo.models.Business;

import lombok.Data;

@Data
public class BusinessResponse {
    private Integer total;
    private List<Business> businesses;
}
