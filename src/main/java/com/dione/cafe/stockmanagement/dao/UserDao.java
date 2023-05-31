package com.dione.cafe.stockmanagement.dao;

import com.dione.cafe.stockmanagement.POJO.User;
import com.dione.cafe.stockmanagement.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {

    //Vérifier si l'email n'existe pas deux fois
    User findEmailById(@Param("email") String email);

    //Récupération liste des users
    List<UserWrapper> getAllUser();

    List<UserWrapper> getAlllAdmin();


    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);
}
