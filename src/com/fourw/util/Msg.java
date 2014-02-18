package com.fourw.util;

import java.io.Serializable;

public class Msg implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int LOGIN=0;
	public static final int CALL=1;
	public static final int SIGN=2;
	public static final int RESULT=3;
	public static final int LOGOUT=4;
	public static final int STOP_CALL=5;
	public static final int SUBMIT=6;
	
	public static final int HINT=7;
	public static final int SHOW1=8;
	public static final int SHOW2=9;
	
	public static final int DISCONNECTED=10;
	
	public static final String STRMSG="String";
	public static final String OBJMSG="Object";
	public static final String LISTMSG="LIST";
	
	public static final String EXIT="exit";
		
	private String phoneNum;
	private String ipAddr;
	private Boolean isShake;
	private String info;
	private Position positon;
	private int code;// 信息指令

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Boolean getIsShake() {
		return isShake;
	}

	public void setIsShake(Boolean isShake) {
		this.isShake = isShake;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Position getPositon() {
		return positon;
	}

	public void setPositon(Position positon) {
		this.positon = positon;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Msg(){
		
	}
	
	public Msg(String phoneNum, String ipAddr, Boolean isShake, String info,
			Position positon, int code) {
		super();
		this.phoneNum = phoneNum;
		this.ipAddr = ipAddr;
		this.isShake = isShake;
		this.info = info;
		this.positon = positon;
		this.code = code;
	}

	@Override
	public String toString() {
		return String.valueOf(code);
//		return "Msg [code=" + code + ", info=" + info + ", ipAddr=" + ipAddr
//				+ ", isShake=" + isShake + ", phoneNum=" + phoneNum
//				+ ", pos_lon=" + positon.getLongitude()+", pos_lat"+ positon.getLongitude() + "]";
	}

}
