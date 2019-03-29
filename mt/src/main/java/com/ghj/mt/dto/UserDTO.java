package com.ghj.mt.dto;

import com.ghj.mt.model.User;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
@Builder
public class UserDTO {
    private Integer clientId;
    private List<User> friendList;
}
