package com.ghj.mt.dao;

import com.ghj.mt.model.Friend;
import com.ghj.mt.model.FriendExample;
import org.springframework.stereotype.Repository;

/**
 * FriendDAO继承基类
 */
@Repository
public interface FriendDAO extends MyBatisBaseDao<Friend, Integer, FriendExample> {
}