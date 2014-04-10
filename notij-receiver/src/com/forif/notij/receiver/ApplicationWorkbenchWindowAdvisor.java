package com.forif.notij.receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.framework.Bundle;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private final int DIALOG_WIDTH = 300;
	private final int DIALOG_HEIGHT = 200;
	private IWorkbenchWindow window;
	private TrayItem trayItem;
	private Image trayImage;
	private Point location = null;
	private Socket socket = null;
	private BufferedReader input = null;
	private PrintWriter output = null;
	private String host = null;
	private int port = 0;
	private String userid = null;
	private String userName = null;
	private String errorMsg = "";
	private String passwd = null;
	private boolean connected = false;

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
        host = System.getProperty("notij.host");
        try{
        	port = Integer.parseInt(System.getProperty("notij.port"));
        }catch(NumberFormatException e){
        	errorMsg = e.getMessage();
        	e.printStackTrace();
        }
        userid = System.getProperty("notij.userid");
        passwd = System.getProperty("notij.passwd");
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(false);
        configurer.setTitle("noti-J Receiver");
        configurer.setShowMenuBar(false);
        configurer.setShowPerspectiveBar(false);
        configurer.setShowProgressIndicator(false);
        window = getWindowConfigurer().getWindow();
		window.getShell().setMinimized(true);
		window.getShell().setVisible(false);
    }
    
	@Override
	public void dispose() {
		if (trayImage != null) {
			trayImage.dispose();
			trayItem.dispose();
		}
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		try {
			trayItem = initTaskItem(window);
		} catch (IOException e) {
			errorMsg = e.getMessage();
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		if (trayItem != null) {
			minimize();
			hookMenu();
		}
		
        Rectangle bound = Display.getDefault().getMonitors()[0].getBounds();
        this.location = new Point(bound.x + bound.width - DIALOG_WIDTH, bound.y + bound.height - DIALOG_HEIGHT);
        
        connect();
        Thread rcvThread = new Thread(new ReceiveRunnable());
        rcvThread.start();
	}
	
	@Override
	public boolean preWindowShellClose() {
		getWindowConfigurer().getWindow().getShell().notifyListeners(SWT.Iconify, new Event());
		return false;
	}

	private TrayItem initTaskItem(IWorkbenchWindow window) throws IOException {
		Bundle bundle = Platform.getBundle("com.forif.notij.receiver");
		URL url = FileLocator.find(bundle, new Path("/icons/alt_window_16.gif"), null);
		url = FileLocator.toFileURL(url);
		final Tray tray = window.getShell().getDisplay().getSystemTray();
		TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		trayImage = new Image(window.getShell().getDisplay(), url.getPath().toString());
		trayItem.setImage(trayImage);
		trayItem.setToolTipText("noti-J");
		return trayItem;
	}

	private void minimize() {
		window.getShell().addShellListener(new ShellAdapter() {
			public void shellIconified(ShellEvent e) {
				window.getShell().setVisible(false);
				window.getShell().setMaximized(false);
			}
			
			public void shellClosed(ShellEvent e){
				window.getShell().setVisible(false);
				window.getShell().setMaximized(false);
			}
		});
	}

	private void hookMenu() {
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Menu menu = new Menu(window.getShell(), SWT.POP_UP);
				MenuItem info = new MenuItem(menu, SWT.NONE);
				info.setText("About");
				info.addListener(SWT.Selection, new Listener(){
					@Override
					public void handleEvent(Event event) {
						InformationDialog iwin = new InformationDialog(window.getShell());
						iwin.setConnected(connected);
						iwin.setUserid(userid);
						iwin.setUserName(userName);
						iwin.setErrorMsg(errorMsg);
						iwin.open();
						iwin.getShell().setSize(400, 300);
					}
				});

				MenuItem exit = new MenuItem(menu, SWT.NONE);
				exit.setText("Exit");
				exit.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						if(output != null)
							output.close();
						if(input != null)
							try{ input.close(); }catch(IOException ex){}
						if(socket != null)
							try{ socket.close(); }catch(IOException ex){}
						window.close();
					}
				});
				menu.setVisible(true);
			}
		});
	}
    
	private void connect(){
		Map<String, String> data = new HashMap<String, String>(3);
		data.put("cmd", "login");
		data.put("id", userid);
		data.put("passwd", passwd);
		ObjectMapper mapper = new ObjectMapper();
		try {
			socket = new Socket(host, port);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			output = new PrintWriter(socket.getOutputStream());
			System.out.println("socket recovery : " + socket.isConnected());
			output.println(mapper.writer().writeValueAsString(data));
			output.flush();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			e.printStackTrace();
		}
	}
	
	class ReceiveRunnable implements Runnable{

		@Override
		public void run() {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<Map<String, String>> type = new TypeReference<Map<String, String>>(){};
			
			while(true){
				try{
					String line = input.readLine();
					if(line == null)
						break;
					final Map<String, String> data = mapper.readValue(line, type);
					String cmd = data.get("cmd");
					if("login".equals(cmd)){
						userName = data.get("name");
						connected = true;
						errorMsg = "";
						System.out.println("Response login!");
					}else if("live".equals(cmd)){
						System.out.println("Check live!");
					}else{
						window.getShell().getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								String text = data.get("message");
								String url = data.get("url");
								Dialog dialog = new MessageDialog(window.getShell(), text, url);
								dialog.open();
								dialog.getShell().setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
								dialog.getShell().setLocation(location);
							}
						});
					}
				}catch(NullPointerException e){
					errorMsg = e.getMessage();
					try { Thread.sleep(5000); } catch (InterruptedException e1) { }
					connect();
				}catch(IOException e){
					connected = false;
					errorMsg = e.getMessage();
					if(output != null )
						output.close();
					if(input != null)
						try{ input.close(); }catch(IOException ex){}
					if(socket != null)
						try{ socket.close(); }catch(IOException ex){}
					try { Thread.sleep(5000); } catch (InterruptedException e1) { }
					connect();
				}
			}
		}
	}
	
}
