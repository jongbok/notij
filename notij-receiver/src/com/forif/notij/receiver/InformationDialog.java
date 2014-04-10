/*
 * @(#) InformationApplicationWindow.java 1.0, 2014. 3. 20.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.notij.receiver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 3. 20.
 * 
 */
public class InformationDialog extends Dialog {

	private String userid = null;
	private String userName = null; 
	private boolean connected = false;
	private String errorMsg = null;
	private List<Control> controls = new ArrayList<Control>();
	/**
	 * @param parentShell
	 */
	public InformationDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.ON_TOP | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		setBlockOnOpen(false);
	}
	
	protected Control createContents(Composite parent){
		ViewForm form = new ViewForm(parent, SWT.NONE);
		form.setSize(400, 290);
		final Composite shell = new Composite(form.getShell(), SWT.NONE);
		Label lbApp1 = new Label(shell, SWT.NONE);
		lbApp1.setText("Version:");
		lbApp1.setBounds(10, 10, 50, 20);
		controls.add(lbApp1);
		Label lbApp2 = new Label(shell, SWT.NONE);
		lbApp2.setText("noti-J 1.0" );
		lbApp2.setBounds(UIUtils.getBoundsH(lbApp1, 10, 100));
		controls.add(lbApp2);
		
		Label lbStatus1 = new Label(shell, SWT.NONE);
		lbStatus1.setText("State:");
		lbStatus1.setBounds(UIUtils.getBoundsV(lbApp1, 10));
		controls.add(lbStatus1);
		Label lbStatus2 = new Label(shell, SWT.NONE);
		lbStatus2.setText(connected?"connected":"disconnect");
		lbStatus2.setForeground(Display.getDefault().getSystemColor(connected?SWT.COLOR_BLUE: SWT.COLOR_RED));
		lbStatus2.setBounds(UIUtils.getBoundsV(lbApp2, 10));
		controls.add(lbStatus2);
		
		Label lbUser1 = new Label(shell, SWT.NONE);
		lbUser1.setText("Account:");
		lbUser1.setBounds(UIUtils.getBoundsV(lbStatus1, 10));
		controls.add(lbUser1);
		Label lbUser2 = new Label(shell, SWT.NONE);
		lbUser2.setText(userid + "(" + userName + ")");
		lbUser2.setBounds(UIUtils.getBoundsV(lbStatus2, 10));
		controls.add(lbUser2);
		
		Label lbLic1 = new Label(shell, SWT.NONE);
		lbLic1.setText("License:");
		lbLic1.setBounds(UIUtils.getBoundsV(lbUser1, 10));
		controls.add(lbLic1);
		Label lbLic2 = new Label(shell, SWT.NONE);
		lbLic2.setText("GPL 3.0");
		lbLic2.setBounds(UIUtils.getBoundsV(lbUser2, 10));
		controls.add(lbLic2);
		
		Label lbDev1 = new Label(shell, SWT.NONE);
		lbDev1.setText("Author:");
		lbDev1.setBounds(UIUtils.getBoundsV(lbLic1, 10));
		controls.add(lbDev1);
		Label lbDev2 = new Label(shell, SWT.NONE);
		lbDev2.setText("Jong-Bok,Park(asdkf20@naver.com)");
		lbDev2.setBounds(UIUtils.getBoundsV(lbLic2, 10, 300));
		controls.add(lbDev2);
		
		Text txError = new Text(shell, SWT.NONE|SWT.WRAP);
		txError.setText(errorMsg == null? "": errorMsg);
		txError.setBounds(UIUtils.getBoundsV(lbDev1, 10, 300, 100));
		txError.setEditable(false);
		controls.add(txError);
		controls.add(form);
		return form;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public boolean close(){
		for(Control c : controls){
			c.dispose();
		}
		this.getShell().dispose();
		return super.close();
	}
}
