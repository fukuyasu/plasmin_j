/*
 * Date:	2003/10/10
 *
 * Author:	N.Fukuyasu
 *
 * $Id: VMStandardInput.java,v 1.2 2004/11/24 09:54:25 fukuyasu Exp $
 *
 * Copyright (C) 2003-2004 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.awt.*;
import java.awt.event.*;

public class VMStandardInput extends Dialog {
	private TextField str;

	private Panel standard_input() {
		Panel p = new Panel();

		str = new TextField(20);
		str.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		p.add(str);

		Button ok_b = new Button("OK");
		ok_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		p.add(ok_b);

		Button reset_b = new Button("リセット");
		reset_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				str.setText("");
			}
		});
		p.add(reset_b);

		return p;
	}

	VMStandardInput(VirtualMachineGUI vm_gui) {
		super(vm_gui, true);

		setTitle("Standard Input");
		setSize(400, 100);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				hide();
			}
		});

		add(standard_input());
	}

	public String getString() {
		return str.getText();
	}
}
