package com.ghj.mt.service;

import com.ghj.mt.dao.FriendDAO;
import com.ghj.mt.dao.UserDAO;
import com.ghj.mt.dto.UserDTO;
import com.ghj.mt.model.Friend;
import com.ghj.mt.model.FriendExample;
import com.ghj.mt.model.User;
import com.ghj.mt.model.UserExample;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface IFriendService {

    /**
     *
     *
     * @param clientID
     * @return
     */
    UserDTO listFriends(Integer clientID);
}
