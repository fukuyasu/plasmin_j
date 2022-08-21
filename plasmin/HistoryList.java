/*
 * Date:	2004/11/24
 *
 * Author:	A.Utsuki and N.Fukuyasu
 *
 * $Id: HistoryList.java,v 1.3 2004/11/24 09:54:25 fukuyasu Exp $
 *
 * Copyright (C) 2004 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.io.*;
import java.util.Vector;

public class HistoryList {
	private int maxSize;
	private Vector history;
	private int front;
	private int rear;
	private VirtualMachine vm;

	public HistoryList(int s) {
		maxSize = s + 1;
		history = new Vector(maxSize);
		front = 0;
		rear = -1;
	}

	public void regist(Program p) {
		History u = new History(p);
		Vector inst = p.getInstructions();
		int pc = p.getProgramCounter();

		if (rear == maxSize - 1)
			rear = -1;
		rear++;
		u.setHistory(pc, (Vector) inst.clone());
		if (rear < history.size()) {
			history.remove(rear);
			if (front == maxSize - 1)
				front = 0;
			if (front == rear)
				front++;
		}
		history.add(rear, u);
	}

	public History undo() {
		History tmp;

		if (rear < 0)
			rear = maxSize - 1;
		tmp = (History) history.get(rear);

		rear--;
		if (rear < 0)
			rear = maxSize - 1;

		return tmp;
	}

	public boolean isUndo() {
		return (front != rear);
	}

	public int getFront() {
		return front;
	}

	public int getRear() {
		return rear;
	}

	public void setRear(int r) {
		rear = r;
	}
}

class History {
	private Program p;
	private Vector instructions;
	private int pc;

	public History(Program ph) {
		p = ph;
		instructions = (Vector) p.getInstructions().clone();
		pc = p.getProgramCounter();

	}

	public void setHistory(int c, Vector history) {
		instructions = (Vector) history.clone();
		pc = c;
	}

	public Program getProgram() {
		return p;
	}

	public Vector getInstructions() {
		return instructions;
	}

	public int getProgramCounter() {
		return pc;
	}
}
