package com.ghj.mt.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * friend
 * @author 
 */
@Data
@EqualsAndHashCode
@Builder
public class Friend implements Serializable {
    private Integer id;

    private String myUserId;

    private String friendUserId;

    private static final long serialVersionUID = 1L;

}