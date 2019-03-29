package com.ghj.mt.service.impl;

import com.ghj.mt.dao.FriendDAO;
import com.ghj.mt.dao.UserDAO;
import com.ghj.mt.dto.UserDTO;
import com.ghj.mt.model.Friend;
import com.ghj.mt.model.FriendExample;
import com.ghj.mt.model.User;
import com.ghj.mt.model.UserExample;
import com.ghj.mt.service.IFriendService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class FriendServiceImpl implements IFriendService {

    @Resource
    FriendDAO friendDAO;

    @Resource
    UserDAO userDAO;

    public UserDTO listFriends(Integer clientID) {
        FriendExample friendExample = new FriendExample();
        FriendExample.Criteria fiendCriteria = friendExample.createCriteria();
        fiendCriteria.andMyUserIdEqualTo(clientID);
        List<Friend> friends = friendDAO.selectByExample(friendExample);
        List<User> userList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(friends)) {
            for (Friend friend:friends) {
                UserExample userExample = new UserExample();
                UserExample.Criteria userCriteria = userExample.createCriteria();
                userCriteria.andCidEqualTo(friend.getFriendUserId());
                List<User> users = userDAO.selectByExample(userExample);
                userList.add(users.get(0));
            }
        }
        UserDTO userDTO = UserDTO.builder().clientId(clientID).friendList(userList).build();
        return userDTO;
    }
}
