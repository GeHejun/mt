package com.ghj.mt.message;

import com.ghj.mt.dto.UserDTO;
import com.ghj.mt.enums.MessageType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
@Builder
public class Message {
    private List<Integer> receiveIDs;
    private Integer clientID;
    private String body;
    private MessageType msgType;
    private Integer groupID;
    private UserDTO userDTO;
    private String picture;
    private byte[] voiceMsg;
}
