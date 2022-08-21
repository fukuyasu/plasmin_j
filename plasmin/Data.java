/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu
 *
 * $Id: Data.java,v 1.3 2004/11/24 09:54:25 fukuyasu Exp $
 *
 * Copyright (C) 2003-2004 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.awt.*;

public class Data extends TextField {
	private String name;

	Data() {
		super(4);
	}

	Data(String n) {
		super(4);
		name = n;
	}

	public String getName() {
		return name;
	}

	public void setValue(int v) {
		setText(String.valueOf(v));
	}

	public void resetValue() {
		setText("");
	}

	public int getValue() {
		int num;
		try {
			num = Integer.parseInt(getText());
		} catch (NumberFormatException e) {
			num = (int) Math.round(Math.random() * 9999);
		}
		return num;
	}
}
