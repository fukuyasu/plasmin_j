/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu
 *
 * $Id: About.java,v 1.5 2006/06/21 10:33:07 fukuyasu Exp $
 *
 * Copyright (C) 2003-2006 The Plasmin Project.  All rights reserved.
 */

package plasmin;

import java.awt.*;
import java.awt.event.*;

public class About extends Dialog {
	private static final String copyright = "Copyright (C) 2001-2006 The Plasmin Project.  All rights reserved.";

	About(VirtualMachineGUI vm_gui) {
		super(vm_gui, true);

		String pname = vm_gui.getProgramName();
		setTitle("About " + pname);
		setSize(580, 100);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		setLayout(new BorderLayout());
		add(version_message(pname), "Center");
		add(ok_button(), "South");
	}

	private Panel version_message(String pname) {
		Panel p = new Panel();

		p.setLayout(new GridLayout(0, 1));

		String version = "";
		version += pname;
		version += " Ver.";
		version += Version.getMajorVersion();
		version += ".";
		version += Version.getMinorVersion();

		Label l_version = new Label(version, Label.CENTER);

		l_version.setFont(new Font("Dialog", Font.BOLD, 14));
		p.add(l_version);
		Label l_copyright = new Label(copyright, Label.CENTER);
		p.add(l_copyright);

		return p;
	}

	private Panel ok_button() {
		Panel p = new Panel();

		Button ok_b = new Button("OK");
		ok_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		p.add(ok_b);

		return p;
	}
}
