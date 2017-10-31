/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufrn.sd.project2.network;

/**
 *
 * @author dhiogoboza
 */
public class ClientConfig {
	private String ip = "127.0.0.1";
    private int port = 21000;
	private String password = "/dev/ttyUSB0";

	public ClientConfig(String ip, int port, String password) {
		this.ip = ip;
		this.port = port;
		this.password = password;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "[ip: " + getIp() + ", port: " + getPort() + "]";
	}
	
	
}
