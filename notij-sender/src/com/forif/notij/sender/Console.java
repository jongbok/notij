/*
 * @(#) Console.java 1.0, 2014. 3. 24.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.notij.sender;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 3. 24.
 * 
 */
public class Console {

	public void display() throws UnsupportedEncodingException{
		System.out.println(Charset.defaultCharset().displayName());
		Scanner scanner = new Scanner(System.in, "MS949");
		String host = getHost(scanner);
		int port = getPort(scanner);
		NotiJSender sender = getSender(scanner);
		sender.connect(host, port);
		System.out.println("connect success!");
		System.out.println();
		while(true){
			String id = getRecevierId(scanner);
			String msg = getMessage(scanner);
			String url = getUrl(scanner);
			try{
				sender.send(id, msg, url);
				System.out.println("Message be successfully sent!");
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println();
		}
	}
	
	private String getUrl(Scanner scanner){
		System.out.print("Url:");
		String url = scanner.nextLine();
		if(url == null)
			url = "";
		return url;
	}
	
	private String getMessage(Scanner scanner) throws UnsupportedEncodingException{
		System.out.print("Message:");
		String message = scanner.nextLine();
		if(message == null || "".equals(message.trim())){
			System.err.println("Message must be set!");
			return getMessage(scanner);
		}
		return message;
	}
	
	private String getRecevierId(Scanner scanner){
		System.out.print("Receiver Id:");
		String id = scanner.nextLine();
		if(id == null || "".equals(id.trim())){
			System.err.println("Receiver Id must be set!");
			return getRecevierId(scanner);
		}
		return id;
	}
	
	private String getHost(Scanner scanner){
		System.out.print("Host:");
		String host = scanner.nextLine();
		if(host == null || "".equals(host.trim())){
			System.err.println("host must be set!");
			return getHost(scanner);
		}
		return host;
	}
	
	private int getPort(Scanner scanner){
		System.out.print("Port:");
		String str = scanner.nextLine();
		if(str == null || "".equals(str.trim())){
			System.err.println("port must be set!");
			return getPort(scanner);
		}
		int port;
		try{
			port = Integer.parseInt(str);
		}catch(NumberFormatException ex){
			System.err.println("port must be a integer type!");
			return getPort(scanner);
		}
		return port;
	}
	
	private NotiJSender getSender(Scanner scanner){
		System.out.println("Send Type[s:sync, a:async]:");
		String cmd = scanner.nextLine();
		if(cmd == null || "".equals(cmd.trim())){
			System.err.println("Send Type must be set!");
			return getSender(scanner);
		}
		if("s".equals(cmd)){
			return SyncSender.getInstance();
		}else if("a".equals(cmd)){
			int queueSize = getQueueSize(scanner);
			return AsyncSender.getInstance(queueSize);
		}else{
			System.err.println("Send Type is not valid!");
			return getSender(scanner);
		}
	}
	
	private int getQueueSize(Scanner scanner){
		System.out.println("Queue Size:");
		String str = scanner.nextLine();
		if(str == null || "".equals(str.trim())){
			System.err.println("Queue Size must be set!");
			return getQueueSize(scanner);
		}
		int size;
		try{
			size = Integer.parseInt(str);
		}catch(NumberFormatException ex){
			System.err.println("Queue Size must be a integer type!");
			return getQueueSize(scanner);
		}
		return size;
		
	}
	
	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		new Console().display();
	}

}
