/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu
 *
 * $Id: Executer.java,v 1.4 2004/11/24 09:54:25 fukuyasu Exp $
 *
 * Copyright (C) 2003-2004 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.awt.*;

public class Executer extends Thread {
	private VirtualMachine vm;
	private boolean executing;

	Executer(VirtualMachine v) {
		vm = v;
		executing = true;
	}

	public void run() {
		vm.executionStart();
		while (executing) {
			try {
				sleep(vm.getSpeed() * 5);
			} catch (InterruptedException e) {
				System.exit(0);
			}
			if (!vm.executeInstruction()) {
				executing = false;
			}
		}
		vm.executionStop();
	}

	public void stopExecution() {
		executing = false;
	}

	public boolean isRunning() {
		return executing;
	}
}
