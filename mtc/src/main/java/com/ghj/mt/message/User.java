package com.ghj.mt.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class User {
    private Integer id;

    private Integer username;

    private String password;

    private String faceImage;

    private String faceImgBig;

    private Integer phone;

    private String nickname;

    private Integer cid;

    private static final long serialVersionUID = 1L;
}
