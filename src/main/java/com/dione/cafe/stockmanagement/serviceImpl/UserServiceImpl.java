package com.dione.cafe.stockmanagement.serviceImpl;

import com.dione.cafe.stockmanagement.JWT.CustomerUsersDetailsService;
import com.dione.cafe.stockmanagement.JWT.JwtFilter;
import com.dione.cafe.stockmanagement.JWT.JwtUtil;
import com.dione.cafe.stockmanagement.POJO.User;
import com.dione.cafe.stockmanagement.constents.CafeConstants;
import com.dione.cafe.stockmanagement.dao.UserDao;
import com.dione.cafe.stockmanagement.service.UserService;
import com.dione.cafe.stockmanagement.utils.CafeUtils;
import com.dione.cafe.stockmanagement.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final AuthenticationManager authenticationManager;
    private final CustomerUsersDetailsService customerUsersDetailsService;
    private final JwtUtil jwtUtil;
    private final JwtFilter jwtFilter;
    public UserServiceImpl(UserDao userDao, AuthenticationManager authenticationManager, CustomerUsersDetailsService customerUsersDetailsService, JwtUtil jwtUtil, JwtFilter jwtFilter) {
        this.userDao = userDao;
        this.authenticationManager = authenticationManager;
        this.customerUsersDetailsService = customerUsersDetailsService;
        this.jwtUtil = jwtUtil;
        this.jwtFilter = jwtFilter;
    }

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
        if (validateUserSignUp(requestMap)){
            User user = userDao.findEmailById(requestMap.get("email"));
            if (Objects.isNull(user)){ //Cela signifie que si nous trouvons pas d'email, seul l'objet user ici sera nul

                userDao.save(getUserFromMap(requestMap));

                return CafeUtils.getResponseEntity("Successfully Registered.", HttpStatus.CREATED);
            }else {
                return CafeUtils.getResponseEntity("Email already exist", HttpStatus.BAD_REQUEST);
            }
        }else {
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }
    }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Méthode de connexion d'un user
    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
       log.info("Inside login");
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
        );
        if (auth.isAuthenticated()){
            if (customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(
                        customerUsersDetailsService.getUserDetail().getEmail(), customerUsersDetailsService.getUserDetail().getRole())
                        + "\"}",
                        HttpStatus.OK);
            }else {
                //Demande d'autorisation à l'admin du systéme de se connecter
                return new ResponseEntity<String>("{\"message\":\"" + "Wait for Admin approuval." + "\"}", HttpStatus.BAD_REQUEST);
            }
        }
       try {

       }catch (Exception ex){
           log.error("{}", ex);
       }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials." + "\"}", HttpStatus.BAD_REQUEST);
    }



    //Les noms qui suivent sont obligatoires pour pouvoir valider l'enregistrement d'un utilisateur
    public boolean validateUserSignUp(Map<String, String> requestMap){
       if( requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")){
           return true;
       }
       return false;
    }


    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }




    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //Update User
    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                //Vérifions d'abord si l'utilisateur existe déjà dans la base de données
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optional.isEmpty()){
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAlllAdmin());
                    return CafeUtils.getResponseEntity("User Status updated successfuly", HttpStatus.OK);
                }else {
                    return CafeUtils.getResponseEntity("User dosen't exist", HttpStatus.OK);
                }
            }else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String email, List<UserWrapper> alllAdmin) {

    }
}
