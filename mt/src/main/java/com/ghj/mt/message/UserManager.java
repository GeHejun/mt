package com.ghj.mt.message;

import io.netty.channel.socket.SocketChannel;
import org.omg.CORBA.INTERNAL;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserManager {
    private static UserManager manager;
    private static Map<Integer, SocketChannel> userList = new ConcurrentHashMap();
    private static Map<Integer, CopyOnWriteArrayList<Integer>> groupList = new ConcurrentHashMap();

    private UserManager(){
    }

    public static UserManager getInstance(){
        if(manager  == null){
            synchronized (UserManager.class) {
                if(manager  == null){
                    manager = new UserManager();
                }
            }
        }
        return manager;
    }

    @SuppressWarnings("unchecked")
    public  void addUser(Integer groupID, Integer clientID, SocketChannel channel){
        userList.put(clientID, channel);
        if(groupList.get(groupID) == null){
            CopyOnWriteArrayList<Integer> users = new CopyOnWriteArrayList<Integer>();
            users.add(clientID);
            groupList.put(groupID, users);
        }else{
            groupList.get(groupID).add(clientID);
        }
    }

    public  SocketChannel getUserChannel(Integer clientID){
        return (SocketChannel) userList.get(clientID);
    }

    public  void removeUser(Integer groupID, Integer clientID){
        userList.remove(clientID);
        CopyOnWriteArrayList<Integer> list = groupList.get(groupID);
        int count = list.size();
        for(int i=0; i<count; i++){
            if(list.get(i).equals(clientID)){
                groupList.get(groupID).remove(i);
                break;
            }
        }
    }

    public  void removeChannel(SocketChannel channel){
        Iterator<Map.Entry<Integer, SocketChannel>> entries = userList.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, SocketChannel> entry = entries.next();
            if(entry.getValue().equals(channel)){
                entries.remove();
                return;
            }
        }
    }

    /**
     * work in single thread.
     * @param groupID
     * @return
     */
    public CopyOnWriteArrayList<Integer> getUserListInGroup(Integer groupID){
        return groupList.get(groupID);
    }

    public  int getTotalUserCount(){
        return userList.size();
    }


    public  int getGroupSize(Integer groupID){
        return groupList.get(groupID).size();
    }

    public  void clearGroup(String groupID){
        groupList.get(groupID).clear();
    }

    public  void clearAll(){
        groupList.clear();
        userList.clear();
    }
}
