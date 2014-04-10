/*
 * @(#) MessageDialog.java 1.0, 2014. 3. 20.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.notij.receiver;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 3. 20.
 * 
 */
public class MessageDialog extends Dialog {

	private String text = null;
	private String url = null;
	private Label label = null;
	/**
	 * @param parentShell
	 */
	public MessageDialog(Shell parentShell, String text, String url) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.ON_TOP | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		setBlockOnOpen(false);
		this.text = text;
		this.url = url;
	}

	/**
	 * @param parentShell
	 */
	public MessageDialog(IShellProvider parentShell) {
		super(parentShell);
	}
	
	protected Control createContents(Composite parent){
	    label = new Label(parent, SWT.WRAP | SWT.BORDER);
	    label.setText(text);
	    label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	    GridData gd = new GridData(295, 195);
	    label.setLayoutData(gd);
	    label.setCursor(new Cursor(label.getDisplay(), SWT.CURSOR_HAND));
		label.setForeground(label.getDisplay().getSystemColor(SWT.COLOR_BLUE));
	    label.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				if(url != null && !url.trim().equals(""))
					Program.launch(url);
				MessageDialog.this.close();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		} );
	    parent.pack();
		return label;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public boolean close(){
		if(label != null)
			label.dispose();
		this.getShell().dispose();
		return super.close();
	}

}
