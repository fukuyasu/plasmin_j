/*
 * Date:	2003/10/06
 *
 * Author:	N.Fukuyasu
 *
 * $Id: Confirm.java,v 1.3 2004/11/24 09:54:25 fukuyasu Exp $
 *
 * Copyright (C) 2003-2004 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.awt.*;
import java.awt.event.*;

public class Confirm extends Dialog {
	private boolean ok;

	Confirm(VirtualMachineGUI vm_gui, String title, String message) {
		this(vm_gui, title, message, null);
	}

	Confirm(VirtualMachineGUI vm_gui, String title, String message, String cancel) {
		super(vm_gui, title, true);

		setLayout(new BorderLayout());
		setSize(600, 200);
		add(new TextArea(message, 5, 80), "Center");

		add(ok_button(cancel), "South");
		ok = false;
	}

	private Panel ok_button(String cancel) {
		Panel p = new Panel();

		Button ok_b = new Button("OK");
		ok_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok = true;
				hide();
			}
		});
		p.add(ok_b);

		if (cancel != null) {
			Button cancel_b = new Button(cancel);
			cancel_b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					hide();
				}
			});
			p.add(cancel_b);
		}

		return p;
	}

	public boolean isOk() {
		return ok;
	}
}
