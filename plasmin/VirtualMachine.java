/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu and A.Utsuki
 *
 * $Id: VirtualMachine.java,v 1.13 2006/06/16 05:33:19 fukuyasu Exp $
 *
 * Copyright (C) 2003-2005 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.io.File;
import java.util.Vector;

public class VirtualMachine {
	private static final String program_name = "Plasmin";

	private Register r;
	private Memory m;
	private Program p;
	private VirtualMachineGUI vm_gui;
	private Executer ex;
	private HistoryList h_list;

	private File f;
	private boolean edit;

	VirtualMachine() {
		r = new Register();
		m = new Memory(this);
		p = new Program(this);
		ex = null;
		f = null;
		edit = false;

		vm_gui = new VirtualMachineGUI(this);

		h_list = new HistoryList(10);
		regist();
	}

	public Register getRegister() {
		return r;
	}

	public Memory getMemory() {
		return m;
	}

	public Program getProgram() {
		return p;
	}

	public String getProgramName() {
		return program_name;
	}

	public String getFileName() {
		return (f != null ? f.getName() : null);
	}

	/************************************************************/

	public void start() {
		if (ex == null || !ex.isRunning()) {
			ex = new Executer(this);
			ex.start();
		}
	}

	public void stop() {
		if (ex != null && ex.isRunning()) {
			ex.stopExecution();
		}
	}

	public void step() {
		if (ex == null || !ex.isRunning()) {
			executeInstruction();
		}
	}

	public void reset() {
		r.resetAllRegister();
		m.resetAllMemory();
		vm_gui.resetStepNum();
		p.resetProgramCounter();
	}

	public boolean executeInstruction() {
		return p.execute(this);
	}

	/* Called by Executer */
	public void executionStart() {
		vm_gui.executionStart();
		p.executionStart();
	}

	/* Called by Executer */
	public void executionStop() {
		vm_gui.executionStop();
		p.executionStop();
	}

	/************************************************************/

	public boolean incProgramCounter() {
		return p.incProgramCounter();
	}

	public boolean setProgramCounter(int pc) {
		return p.setProgramCounter(pc);
	}

	/************************************************************/

	public void incStepNum() {
		vm_gui.incStepNum();
	}

	/************************************************************/

	public int getSpeed() {
		return vm_gui.getSpeed();
	}

	/************************************************************/

	public int getRegisterValue(String t) {
		return r.getValue(t);
	}

	public void setRegisterValue(String t, int value) {
		r.setValue(t, value);
	}

	/************************************************************/

	public int getMemoryValue(String t) throws SVException {
		return m.getValue(r.getValue(t));
	}

	public void setMemoryValue(String t, int value) throws SVException {
		m.setValue(r.getValue(t), value);
	}

	public void allocateMemory(String t, String s) throws SVException {
		r.setValue(t, m.allocateMemory(r.getValue(s)));
	}

	public void freeMemory(String t) throws SVException {
		m.freeMemory(r.getValue(t));
	}

	public void setRandomNumbers(String t, String s) throws SVException {
		int n = r.getValue(s);
		for (int i = 0; i < n; i++) {
			int val = (int) Math.round(Math.random() * 126) + 1;
			m.setValue(r.getValue(t) + i, val);
		}
	}

	public void getString(String t, String s) throws SVException {
		VMStandardInput vm_stdin = new VMStandardInput(vm_gui);
		vm_stdin.show();
		String str = vm_stdin.getString();

		int n = r.getValue(s);
		int i;
		for (i = 0; i < n - 1; i++) {
			int val;
			try {
				val = (int) str.charAt(i);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
			m.setValue(r.getValue(t) + i, val);
		}
		m.setValue(r.getValue(t) + i, 0);
	}

	/************************************************************/

	public void setEdit(boolean b) {
		edit = b;
		vm_gui.setTitle();
	}

	public boolean isEdit() {
		return edit;
	}

	public void newFile() {
		p.newFile();
		setEdit(false);
		f = null;
	}

	public boolean loadFile(File file) {
		boolean edit_flag = isEdit();
		boolean res = p.loadFile(file);
		if (res) {
			setEdit(false);
			f = file;
		} else {
			setEdit(edit_flag);
		}
		return res;
	}

	public boolean saveFile() {
		boolean res;
		if (f != null) {
			if (res = p.saveFile(f)) {
				setEdit(false);
			}
			return res;
		}
		vm_gui.saveAs();
		return true;
	}

	public boolean saveFile(File file) {
		boolean res = p.saveFile(file);
		if (res) {
			setEdit(false);
			f = file;
		}
		return res;
	}

	public void exit() {
		System.exit(0);
	}

	/************************************************************/

	public void toggleMemory() {
		if (m.isVisible()) {
			m.setVisible(false);
			vm_gui.memoryVisible(false);
			// System.out.println("Memroy unVisible");
		} else {
			m.setVisible(true);
			vm_gui.memoryVisible(true);
			// System.out.println("Memroy Visible");
		}
	}

	/************************************************************/

	public void regist() {
		h_list.regist(p);
	}

	public void undo() {
		History h = h_list.undo();

		p.setInstructions(h.getInstructions());
		p.setProgramCounter(h.getProgramCounter());
	}

	public boolean isUndo() {
		return h_list.isUndo();
	}

	/************************************************************/

	public void segmentationFault() {
		Confirm sv = new Confirm(vm_gui, "Error", "Segmentation Fault.");
		sv.show();
	}

	public void syntaxError() {
		Confirm se = new Confirm(vm_gui, "Error", "Syntax Error.");
		se.show();
	}

	/************************************************************/

	public static void main(String[] args) {
		VirtualMachine vm = new VirtualMachine();
		vm.vm_gui.show();
	}
}
