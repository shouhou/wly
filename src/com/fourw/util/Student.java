package com.fourw.util;


import java.io.Serializable;

public class Student implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8582641078853102152L;
	private int id;
	private String name;
	private String phone;
	private String num;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Student(){
		
	}
	public Student( String name, String phone ) {
		super();
		this.name = name;
		this.phone = phone;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
}
