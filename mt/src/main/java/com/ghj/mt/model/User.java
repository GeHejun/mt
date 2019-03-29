package com.ghj.mt.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * user
 * @author 
 */
@Data
@EqualsAndHashCode
@Builder
public class User implements Serializable {
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