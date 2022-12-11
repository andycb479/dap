package com.pad.Users.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class User {
  @Id @GeneratedValue private Long id;
  private String firstName;
  private String lastName;
  private String status;
  private boolean isActive;
  private String lastTransactionId;
}
