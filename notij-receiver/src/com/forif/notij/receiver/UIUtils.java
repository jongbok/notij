/*
 * @(#) UIUtils.java 1.0, 2014. 2. 11.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
package com.forif.notij.receiver;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

import com.forif.notij.NotiJException;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 2. 17.
 * 
 */
public final class UIUtils {
	
	public final static short LEFT = 1;
	public final static short RIGHT = 2;
	public final static short UP = 3;
	public final static short DOWN = 4;
	
	public final static Rectangle getBoundsH(Control control, int hGap, int width, int height, short direction){
		Rectangle src = control.getBounds();
		switch(direction){
			case LEFT:
				return new Rectangle(src.x - src.width - hGap, src.y, width, height);
			case RIGHT:
				return new Rectangle(src.x + src.width + hGap, src.y, width, height);
			default:
				throw new NotiJException("Direction is not allow! [LEFT,RIGHT]");
		}
	}

	public final static Rectangle getBoundsH(Control control, int hGap, int width, int height ){
		return getBoundsH(control, hGap, width, height, RIGHT);
	}

	public final static Rectangle getBoundsH(Control control, int hGap, int width, short direction){
		return getBoundsH(control, hGap, width, control.getBounds().height, direction);
	}
	
	public final static Rectangle getBoundsH(Control control, int hGap, int width){
		return getBoundsH(control, hGap, width, control.getBounds().height);
	}

	public final static Rectangle getBoundsH(Control control, int hGap, short direction){
		return getBoundsH(control, hGap, control.getBounds().width, direction);
	}
	
	public final static Rectangle getBoundsH(Control control, int hGap){
		return getBoundsH(control, hGap, control.getBounds().width);
	}

	public final static Rectangle getBoundsH(Control control, short direction){
		return getBoundsH(control, 10, direction);
	}
	
	public final static Rectangle getBoundsH(Control control){
		return getBoundsH(control, 10);
	}

	public final static Rectangle getBoundsV(Control control, int vGap, int width, int height, short direction){
		Rectangle src = control.getBounds();
		switch(direction){
			case UP:
				return new Rectangle(src.x, src.y - src.height - vGap, width, height);
			case DOWN:
				return new Rectangle(src.x, src.y + src.height + vGap, width, height);
			default:
				throw new NotiJException("Direction is not allow! [UP,DOWN]");
		}
	}
	
	public final static Rectangle getBoundsV(Control control, int vGap, int width, int height){
		return getBoundsV(control, vGap, width, height, DOWN);
	}

	public final static Rectangle getBoundsV(Control control, int vGap, int width, short direction){
		return getBoundsV(control, vGap, width, control.getBounds().height, direction);
	}
	
	public final static Rectangle getBoundsV(Control control, int vGap, int width){
		return getBoundsV(control, vGap, width, control.getBounds().height);
	}

	public final static Rectangle getBoundsV(Control control, int vGap, short direction){
		return getBoundsV(control, vGap, control.getBounds().width, direction);
	}

	public final static Rectangle getBoundsV(Control control, int vGap){
		return getBoundsV(control, vGap, control.getBounds().width);
	}

	public final static Rectangle getBoundsV(Control control, short direction){
		return getBoundsV(control, 10, direction);
	}

	public final static Rectangle getBoundsV(Control control){
		return getBoundsV(control, 10);
	}
	
}
