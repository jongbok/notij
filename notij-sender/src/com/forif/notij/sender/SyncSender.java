/*
 * @(#) SyncSender.java 1.0, 2014. 3. 21.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.notij.sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.forif.notij.NotiJException;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 3. 21.
 * 
 */
public class SyncSender implements NotiJSender {

	private ObjectMapper mapper = new ObjectMapper();
	private Socket socket = null;
	private PrintWriter output = null;
	private BufferedReader input = null;
	private String host = null;
	private int port = 0;
	private static NotiJSender sender = null;
	private Map<String, String> message = new HashMap<String, String>(4);
	private boolean connected = false;
	
	private SyncSender(){ }
	
	public static NotiJSender getInstance(){
		if(sender == null)
			sender = new SyncSender();
		return sender;
	}
	
	/* (non-Javadoc)
	 * @see com.forif.notij.sender.NotiJSender#connect(java.lang.String, int)
	 */
	public void connect(String host, int port) {
		this.host = host;
		this.port = port;
		connect();
	}
	
	/* (non-Javadoc)
	 * @see com.forif.notij.sender.NotiJSender#connect()
	 */
	public void connect() {
		if(host == null)
			throw new NotiJException("host is empty!");
		if(port == 0)
			throw new NotiJException("port is empty!");
		try {
			if(output != null)
				output.close();
			if(socket != null)
				socket.close();
			socket = new Socket(host, port);
			output = new PrintWriter(socket.getOutputStream());
			input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			connected = true;
		} catch (UnknownHostException e) {
			throw new NotiJException("cannot connect gateway!", e);
		} catch (IOException e) {
			throw new NotiJException("cannot connect gateway!", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.forif.notij.sender.NotiJSender#send(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void send(String userId, String msg, String url){
		synchronized (this) {
			if(!connected)
				connect();
			message.clear();
			message.put("cmd", "send");
			message.put("userid", userId);
			message.put("message", msg);
			message.put("url", url==null?"":url);
			try {
				output.println(mapper.writer().writeValueAsString(message));
				output.flush();
				String str = input.readLine();
				if("error".equals(str.trim())){
					throw new NotiJException("receiver is not exists!");
				}
			} catch (SocketException e) {
				connected = false;
				throw new NotiJException("cannot send message!", e);
			} catch (Exception e){
				throw new NotiJException("cannot send message!", e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.forif.notij.sender.NotiJSender#close()
	 */
	public void close() {
		Map<String, String> message = new HashMap<String, String>();
		message.put("cmd", "exit");
		try {
			output.println(mapper.writer().writeValueAsString(message));
		} catch (Exception e1) {
		}
		if(output != null)
			output.close();
		if(socket != null)
			try { socket.close(); } catch (IOException e) {}
	}
	
}
