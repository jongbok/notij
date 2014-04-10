/*
 * @(#) NotiJException.java 1.0, 2014. 3. 24.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.notij;
/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 3. 24.
 * 
 */
public class NotiJException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7035263317110142786L;

	/**
	 * @param arg0
	 */
	public NotiJException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public NotiJException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public NotiJException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public NotiJException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
