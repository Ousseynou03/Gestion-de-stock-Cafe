package com.dione.cafe.stockmanagement.dao;

import com.dione.cafe.stockmanagement.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {

    //Vérifier si l'email n'existe pas deux fois
    User findEmailById(@Param("email") String email);
}
