package com.ghj.mt.dao;

import com.ghj.mt.model.User;
import com.ghj.mt.model.UserExample;
import org.springframework.stereotype.Repository;

/**
 * UserDAO继承基类
 */
@Repository
public interface UserDAO extends MyBatisBaseDao<User, Integer, UserExample> {
}