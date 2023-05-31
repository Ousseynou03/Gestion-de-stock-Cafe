package com.dione.cafe.stockmanagement.rest;

import com.dione.cafe.stockmanagement.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/user")
public interface UserRest {

    @PostMapping(path = "/signup")
    ResponseEntity<String> signUp(@RequestBody(required = true) Map<String,String> requestMap);

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody Map<String, String> requestMap);

    @GetMapping("/get")
    ResponseEntity<List<UserWrapper>> getAllUser();

    @PostMapping("/update")
    ResponseEntity<String> update(@RequestBody Map<String, String> requestMap);
}
