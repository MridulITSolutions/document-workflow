package com.docshare.common.entity;

//import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_master", schema = "docshare")
@Getter
@Setter
public class UserMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;
    @Column(name = "role")
    private String role;

    private String email;

    @Column(name = "password")
    private String password;

    private String department;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "sm_id")
    private Long smId;

    @Column(name = "security_id")
    private Long securityId;
}