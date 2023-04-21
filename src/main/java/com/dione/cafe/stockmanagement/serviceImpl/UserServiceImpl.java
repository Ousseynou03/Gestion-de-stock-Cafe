package com.dione.cafe.stockmanagement.serviceImpl;

import com.dione.cafe.stockmanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);

        return null;
    }

    //Les noms qui suivent sont obligatoires pour pouvoir valider l'enregistrement d'un utilisateur
    public boolean validateUserSignUp(Map<String, String> requestMap){
       if( requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")){
           return true;
       }
       return false;
    }
}
