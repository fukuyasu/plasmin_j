/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu
 *
 * $Id: Memory.java,v 1.7 2004/11/24 09:54:25 fukuyasu Exp $
 *
 * Copyright (C) 2004 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.awt.*;
import java.awt.event.*;

public class Memory extends Frame {
	private static final int MEM_NUM = 100;
	private static final int ROW_NUM = 10;
	private MData[] mem;
	private static final MData dummy = new MData("dummy");
	private static final int ADDR_BASE = 8000;

	private Checkbox char_cb;

	private VirtualMachine vm;

	private MData get_mem(int addr) throws SVException {
		if (addr < ADDR_BASE || MEM_NUM + ADDR_BASE <= addr) {
			throw (new SVException());
		} else {
			return mem[addr - ADDR_BASE];
		}
	}

	private int search_addr;

	private int search_free_memory(int size) {
		search_addr = ADDR_BASE;
		while (search_addr < ADDR_BASE + MEM_NUM) {
			int addr = search_addr;
			try {
				if (is_free(search_addr, size)) {
					return addr;
				}
			} catch (SVException e) {
				/* Never catch SVException. */
			}
			search_addr++;
		}
		return 0;
	}

	private boolean is_free(int addr, int size) throws SVException {
		search_addr = addr;
		if (addr >= ADDR_BASE + MEM_NUM) {
			return false;
		}
		MData m = get_mem(addr);
		if (m.isEnabled()) {
			int d;
			if ((d = m.getAllocSize()) != 0) {
				search_addr += d - 1;
			}
			return false;
		}
		return (size > 1 ? is_free(addr + 1, size - 1) : true);
	}

	private void toggle_char_mode() {
		for (int i = 0; i < MEM_NUM; i++) {
			mem[i].setCharMode(this);
		}
	}

	private Panel memory_table() {
		Panel m_table = new Panel();
		m_table.setLayout(new GridLayout(ROW_NUM + 1, 0));

		Panel dummy = new Panel();
		m_table.add(dummy);

		int i, j;
		for (i = 0; i < MEM_NUM / ROW_NUM; i++) {
			Panel p = new Panel();
			p.setLayout(new BorderLayout());
			p.add(new Label(String.valueOf(ADDR_BASE + i * ROW_NUM), Label.CENTER), "South");
			m_table.add(p);
		}
		for (j = 0; j < ROW_NUM; j++) {
			Panel q = new Panel();
			q.add(new Label(String.valueOf(j), Label.RIGHT));
			m_table.add(q);
			for (i = 0; i < MEM_NUM / ROW_NUM; i++) {
				Panel p = new Panel();
				p.add(mem[i * ROW_NUM + j]);
				m_table.add(p);
			}
		}

		return m_table;
	}

	Memory(VirtualMachine v) {
		vm = v;

		mem = new MData[MEM_NUM];
		for (int i = 0; i < MEM_NUM; i++) {
			mem[i] = new MData();
			mem[i].setEnabled(false);
		}

		/*
		 * User Interface.
		 */

		setTitle("Plasmin-Memory");
		setSize(750, 500);
		setLayout(new BorderLayout());

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				vm.toggleMemory();
			}
		});

		add(memory_table(), "Center");

		Panel p = new Panel();
		Button close_b = new Button("閉じる");
		close_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.toggleMemory();
			}
		});
		p.add(close_b);

		char_cb = new Checkbox("Char");
		char_cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				toggle_char_mode();
			}
		});
		p.add(char_cb);
		add(p, "South");
	}

	public void setValue(int addr, int v) throws SVException {
		MData m = get_mem(addr);
		if (!m.isEnabled()) {
			throw (new SVException());
		}
		m.setValue(this, v);
	}

	public void resetValue(int addr) throws SVException {
		get_mem(addr).resetValue();
	}

	public int getValue(int addr) throws SVException {
		return get_mem(addr).getValue(this);
	}

	public int allocateMemory(int size) throws SVException {
		int addr = search_free_memory(size);
		if (addr != 0) {
			get_mem(addr).setAllocSize(size);
			for (int i = 0; i < size; i++) {
				get_mem(addr + i).setEnabled(true);
			}
		}
		return addr;
	}

	public void freeMemory(int addr) throws SVException {
		MData m = get_mem(addr);
		if (!m.isEnabled()) {
			throw (new SVException());
		}
		int size = m.getAllocSize();
		for (int i = 0; i < size; i++) {
			get_mem(i + addr).setEnabled(false);
		}
	}

	public void resetAllMemory() {
		for (int i = 0; i < MEM_NUM; i++) {
			mem[i].resetValue();
		}
	}

	public boolean isCharMode() {
		return char_cb.getState();
	}
}

class MData extends Data {
	private int alloc_size;
	private int value;

	MData() {
		super();
		alloc_size = 0;
	}

	MData(String n) {
		super(n);
		alloc_size = 0;
	}

	public void setEnabled(boolean b) {
		if (b) {
			setText("");
		} else {
			setText("-----");
			alloc_size = 0;
		}
		super.setEnabled(b);
	}

	public int getAllocSize() {
		return alloc_size;
	}

	public void setAllocSize(int size) {
		alloc_size = size;
	}

	public void setValue(Memory m, int v) {
		value = v;
		if (m.isCharMode()) {
			if (0 < v && v < 128 && !Character.isISOControl((char) v)) {
				setText(String.valueOf((char) v));
			} else if (v == 0) {
				setText("'\\0'");
			} else {
				setText("***");
			}
		} else {
			super.setValue(v);
		}
	}

	public void resetValue() {
		setEnabled(false);
	}

	private int get_char_value() {
		int v;
		String str = getText();
		if (str.equals("")) {
			v = super.getValue();
		} else if (str.equals("***")) {
			v = value;
		} else if (str.equals("'\\0'")) {
			v = 0;
		} else {
			v = (int) str.charAt(0);
		}
		return v;
	}

	public int getValue(Memory m) {
		int v = 0;
		if (m.isCharMode()) {
			v = get_char_value();
		} else {
			v = super.getValue();
		}
		value = v;
		return v;
	}

	public void setCharMode(Memory m) {
		if (isEnabled()) {
			if (m.isCharMode()) {
				setValue(m, super.getValue());
			} else {
				setValue(m, get_char_value());
			}
		}
	}
}
