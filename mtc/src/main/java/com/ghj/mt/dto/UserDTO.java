package com.ghj.mt.dto;

import com.ghj.mt.message.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
@Builder
public class UserDTO {
    private Integer clientId;
    public List<User> friendList;
}
