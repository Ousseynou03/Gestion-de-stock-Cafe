package com.dione.cafe.stockmanagement.dao;

import com.dione.cafe.stockmanagement.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Integer> {
}
