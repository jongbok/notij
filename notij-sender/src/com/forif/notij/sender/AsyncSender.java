/*
 * @(#) AsyncSender.java 1.0, 2014. 3. 24.
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
import java.util.LinkedList;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.forif.notij.NotiJException;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 3. 24.
 * 
 */
public class AsyncSender implements NotiJSender {

	private ObjectMapper mapper = new ObjectMapper();
	private Socket socket = null;
	private PrintWriter output = null;
	private BufferedReader input = null;
	private String host = null;
	private int port = 0;
	private static NotiJSender sender = null;
	private static LinkedList<Map<String, String>> queue = null;
	private final static int DEFAULT_QUEUE_SIZE = 500;
	private static int queueSize = 0;
	private boolean connected = false;
	private Thread thread = null;
	
	
	/**
	 * 
	 */
	private AsyncSender() { }
	
	public static NotiJSender getInstance(){
		return getInstance(DEFAULT_QUEUE_SIZE);
	}
	
	public static NotiJSender getInstance(int queueSize){
		if(sender == null)
			sender = new AsyncSender();
		queue = new LinkedList<Map<String, String>>();
		AsyncSender.queueSize = queueSize;
		return sender;
	}

	/* (non-Javadoc)
	 * @see com.forif.notij.sender.NotiJSender#connect(java.lang.String, int)
	 */
	public void connect(String host, int port) {
		this.host = host;
		this.port = port;
		connect();
		connected = true;
		thread = new Thread(new SendRunable());
		thread.start();
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
	public void send(String userId, String msg, String url) {
		synchronized (queue) {
			if(queue.size() >= queueSize)
				throw new NotiJException("Message Queue is overflowed!");
			
			Map<String, String> message = new HashMap<String, String>(4);
			message.put("cmd", "send");
			message.put("userid", userId);
			message.put("message", msg);
			message.put("url", url==null?"":url);
			queue.push(message);
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
		host = null;
		port = 0;
		connected = false;
		if(output != null)
			output.close();
		if(socket != null)
			try { socket.close(); } catch (IOException e) {}
	}	

	class SendRunable implements Runnable{

		@Override
		public void run() {
			while(connected){
				try {
					while(queue.size() > 0){
						Map<String, String> message = queue.peek();
						output.println(mapper.writer().writeValueAsString(message));
						output.flush();
						input.readLine();
						queue.remove();
					}
				} catch (SocketException e){
					try{ connect(); }catch(NotiJException ex){}
				} catch (Exception e) {
					throw new NotiJException("Thread cannot run!", e);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new NotiJException("Thread cannot run!", e);
				}
			}
		}
		
	}
}
