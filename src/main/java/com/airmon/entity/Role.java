package com.airmon.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "roleId")
	private Long id;

	@Column(name = "rollName" , unique = true)
	private String rollName;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "user_role", 
		joinColumns = @JoinColumn(name = "userId"), 
		inverseJoinColumns = @JoinColumn(name = "roleId"))
	private List<User> users;
	

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Role() {
	}

	public Role(String rollName) {
		this.rollName = rollName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return rollName;
	}

	public void setName(String rollName) {
		this.rollName = rollName;
	}

	@Override
	public String toString() {
		return "Role{" + "id=" + id + ", rollName='" + rollName + '\'' + '}';
	}
}
