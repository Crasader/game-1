package com.netty.model;

public class ConnectionInfo {
	private String ip;
	private int count;
	private long beforTime = System.currentTimeMillis();
	
	public void addCount(){
		count++;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public long getBeforTime() {
		return beforTime;
	}
	public void setBeforTime(long beforTime) {
		this.beforTime = beforTime;
	}
	
	
}
