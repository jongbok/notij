/*
 * @(#) NotiJSender.java 1.0, 2014. 3. 24.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.notij.sender;
/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 3. 24.
 * 
 */
public interface NotiJSender {

	void connect(String host, int port);
	
	void connect();
	
	void send(String userId, String msg, String url);
	
	void close();
}
