package com.fujica.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 * 
 */
@Data
public class MqttUser implements Serializable {
    private Integer id;

    private String username;

    private String passwordHash;

    private String salt;

    private Integer isSuperuser;

    private String remark;

    private Date created;

    private static final long serialVersionUID = 1L;

}