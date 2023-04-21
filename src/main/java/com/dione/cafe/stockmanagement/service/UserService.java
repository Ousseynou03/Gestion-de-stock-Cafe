package com.dione.cafe.stockmanagement.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {

    public ResponseEntity<String> signUp(Map<String, String> requestMap);
}
