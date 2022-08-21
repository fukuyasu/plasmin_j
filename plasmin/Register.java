/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu
 *
 * $Id: Register.java,v 1.6 2004/11/24 09:54:25 fukuyasu Exp $
 *
 * Copyright (C) 2003-2004 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class Register extends Panel {
	private static final int  DREG_NUM = 5;
	private static final char DREG_NAME = 'A';
	private static final int  PREG_NUM = 5;
	private static final char PREG_NAME = 'p';
	private Data[] dreg, preg;
	private static final Data dummy = new Data("dummy");

	private Panel data_register() {
		Panel panel = new Panel();

		dreg = new Data[DREG_NUM];
		Panel dr_panel = new Panel();
		dr_panel.setLayout(new GridLayout(0, 2));
		for (int i = 0; i < DREG_NUM; i++) {
			dreg[i] = new Data(String.valueOf((char) (DREG_NAME + i)));
			dr_panel.add(new Label(dreg[i].getName(), Label.CENTER));
			dr_panel.add(dreg[i]);
		}
		panel.add(dr_panel);

		return panel;
	}

	private Panel pointer_register() {
		Panel panel = new Panel();

		preg = new Data[PREG_NUM];
		Panel pr_panel = new Panel();
		pr_panel.setLayout(new GridLayout(0, 2));
		for (int i = 0; i < PREG_NUM; i++) {
			preg[i] = new Data(String.valueOf((char) (PREG_NAME + i)));
			pr_panel.add(new Label(preg[i].getName(), Label.CENTER));
			pr_panel.add(preg[i]);
		}
		panel.add(pr_panel);

		return panel;
	}

	private Data get_reg(String name) {
		int i;
		for (i = 0; i < DREG_NUM; i++) {
			if (dreg[i].getName().equals(name)) {
				return dreg[i];
			}
		}
		for (i = 0; i < PREG_NUM; i++) {
			if (preg[i].getName().equals(name)) {
				return preg[i];
			}
		}
		return dummy;
	}

	Register() {
		setLayout(new BorderLayout());
		add(new Label("Register", Label.CENTER), "North");
		add(data_register(), "West");
		add(pointer_register(), "East");
	}

	public void setValue(String name, int v) {
		get_reg(name).setValue(v);
	}

	public void resetValue(String name) {
		get_reg(name).resetValue();
	}

	public int getValue(String name) {
		return get_reg(name).getValue();
	}

	public Vector getRegisterList() {
		Vector v = new Vector();
		for (int i = 0; i < dreg.length; i++) {
			v.add(dreg[i].getName());
		}
		for (int j = 0; j < preg.length; j++) {
			v.add(preg[j].getName());
		}
		return v;
	}

	public void resetAllRegister() {
		for (int i = 0; i < dreg.length; i++) {
			dreg[i].resetValue();
		}
		for (int j = 0; j < preg.length; j++) {
			preg[j].resetValue();
		}
	}

	public String getName(String str) {
		Data reg = get_reg(str);
		if (reg == dummy) {
			reg = dreg[0];
		}
		return reg.getName();
	}

	public boolean isDataRegister(String str) {
		for (int i = 0; i < dreg.length; i++) {
			if (dreg[i].getName().equals(str)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPointerRegister(String str) {
		for (int i = 0; i < preg.length; i++) {
			if (preg[i].getName().equals(str)) {
				return true;
			}
		}
		return false;
	}
}
