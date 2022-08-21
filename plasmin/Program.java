/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu and A.Utsuki
 *
 * $Id: Program.java,v 1.9 2004/11/24 09:14:04 fukuyasu Exp $
 *
 * Copyright (C) 2003-2004 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class Program extends Panel {
	private Vector instructions;
	private java.awt.List i_list;

	private Choice op_select;
	private Choice t_select;
	private Choice s_select;
	private TextField v_text;

	private Button clr_button;

	private Button add_button;
	private Button ins_button;
	private Button edt_button;
	private Button del_button;
	private Button cpy_button;
	private Button undo_button;

	private VirtualMachine vm;

	private Instruction make_instruction() throws SEException {
		Instruction inst;
		inst = new Instruction(vm, op_select.getSelectedItem(), t_select.getSelectedItem(), s_select.getSelectedItem(),
				v_text.getText());
		return inst;
	}

	private Choice opcode_select() {
		Choice choice = new Choice();

		Vector v = Opcode.getOpcodeList();
		choice.add("");
		for (Enumeration e = v.elements(); e.hasMoreElements();) {
			choice.add((String) e.nextElement());
		}
		return choice;
	}

	private Choice register_select(VirtualMachine vm) {
		Choice choice = new Choice();

		Vector v = vm.getRegister().getRegisterList();
		choice.add("");
		for (Enumeration e = v.elements(); e.hasMoreElements();) {
			choice.add((String) e.nextElement());
		}
		return choice;
	}

	private TextField value_field() {
		TextField field = new TextField(5);
		return field;
	}

	private Panel edit_interface() {
		Panel panel = new Panel();

		panel.setLayout(new BorderLayout());
		Panel op_panel = new Panel();
		op_panel.add(op_select = opcode_select());
		op_panel.add(t_select = register_select(vm));
		op_panel.add(s_select = register_select(vm));
		op_panel.add(v_text = value_field());

		clr_button = new Button("クリア");
		clr_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearEditInterface();
			}
		});
		op_panel.add(clr_button);

		panel.add(op_panel, "North");

		Panel bt_panel = new Panel();

		add_button = new Button("追加");
		add_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					vm.regist();
					undo_button.setEnabled(true);
					appendInstruction(make_instruction());
				} catch (SEException se) {
					vm.syntaxError();
				}
			}
		});
		bt_panel.add(add_button);

		ins_button = new Button("挿入");
		ins_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					vm.regist();
					undo_button.setEnabled(true);
					insertInstruction(make_instruction());
				} catch (SEException se) {
					vm.syntaxError();
				}
			}
		});
		bt_panel.add(ins_button);

		edt_button = new Button("変更");
		edt_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					vm.regist();
					undo_button.setEnabled(true);
					replaceInstruction(make_instruction());
				} catch (SEException se) {
					vm.syntaxError();
				}
			}
		});
		bt_panel.add(edt_button);

		del_button = new Button("削除");
		del_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.regist();
				undo_button.setEnabled(true);
				deleteInstruction();
			}
		});
		bt_panel.add(del_button);

		cpy_button = new Button("コピー");
		cpy_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyInstruction();
			}
		});
		bt_panel.add(cpy_button);

		undo_button = new Button("Undo");
		undo_button.setEnabled(false);

		undo_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				undoInstruction();
				if (vm.isUndo() == false) {
					undo_button.setEnabled(false);
				}
			}
		});
		bt_panel.add(undo_button);

		panel.add(bt_panel, "Center");

		return panel;
	}

	private Panel updown_button() {
		Panel panel = new Panel();
		Panel ud_panel = new Panel();
		ud_panel.setLayout(new GridLayout(0, 1));

		Button b_up = new Button("▲");
		b_up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.regist();
				undo_button.setEnabled(true);
				moveupInstruction();
			}
		});

		Button b_down = new Button("▼");
		b_down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.regist();
				undo_button.setEnabled(true);
				movedownInstruction();
			}
		});

		ud_panel.add(b_up);
		ud_panel.add(b_down);
		panel.add(ud_panel);
		return panel;
	}

	private Instruction get_instruction() {
		Instruction inst = null;
		int i = i_list.getSelectedIndex();
		if (i != -1) {
			inst = (Instruction) instructions.get(i);
		}
		return inst;
	}

	private String fstring(int i, int n) {
		String str = "";
		int x = 1;
		for (int j = 0; j < n - 1; j++)
			x *= 10;
		while (i < x && x > 1) {
			str += " ";
			x /= 10;
		}
		str += i;
		return str;
	}

	private String instruction_string(int pc, Instruction inst) {
		String str = "";
		str += fstring(pc, 4);
		str += " ";
		str += inst;
		return str;
	}

	private void update_instructions(int i) {
		int k = i_list.getItemCount();
		for (int j = i; j < instructions.size(); j++) {
			Instruction u_inst = (Instruction) instructions.get(j);
			if (j < k) {
				i_list.replaceItem(instruction_string(j + 1, u_inst), j);
			} else {
				i_list.add(instruction_string(j + 1, u_inst));
			}
		}
	}

	Program(VirtualMachine v) {
		vm = v;

		setLayout(new BorderLayout());

		add(new Label("Program", Label.CENTER), "North");

		i_list = new java.awt.List();

		Panel op_panel = new Panel();
		op_panel.setLayout(new BorderLayout());
		op_panel.add(i_list, "Center");
		op_panel.add(edit_interface(), "South");
		op_panel.add(updown_button(), "East");
		add(op_panel, "Center");

		instructions = new Vector();
	}

	public Button getUndoButton() {
		return undo_button;
	}

	public boolean execute(VirtualMachine vm) {
		Instruction inst = get_instruction();
		return (inst != null ? inst.execute(vm) : false);
	}

	public void appendInstruction() throws SEException {
		Instruction inst = make_instruction();
		appendInstruction(inst);
	}

	public void appendInstruction(Instruction inst) {
		instructions.add(inst);
		int pc = instructions.size();
		i_list.add(instruction_string(pc, inst));
		setProgramCounter(pc);
		vm.setEdit(true);
	}

	public void insertInstruction() throws SEException {
		Instruction inst = make_instruction();
		insertInstruction(inst);
	}

	public void insertInstruction(Instruction inst) {
		int i = i_list.getSelectedIndex();
		if (i != -1) {
			for (int j = 0; j < instructions.size(); j++) {
				Instruction i_tmp = (Instruction) instructions.get(j);
				int jt = i_tmp.getJumpTarget();
				if (jt >= i + 1) {
					i_tmp.setJumpTarget(jt + 1);
				}
			}
			instructions.insertElementAt(inst, i);
			update_instructions(0);
			setProgramCounter(i + 1);
			vm.setEdit(true);
		} else {
			appendInstruction(inst);
		}
	}

	public void replaceInstruction() throws SEException {
		Instruction inst = make_instruction();
		replaceInstruction(inst);
	}

	public void replaceInstruction(Instruction inst) {
		int i = i_list.getSelectedIndex();
		if (i != -1) {
			instructions.setElementAt(inst, i);
			i_list.replaceItem(instruction_string(i + 1, inst), i);
			setProgramCounter(i + 1);
			vm.setEdit(true);
		}
	}

	public void deleteInstruction() {
		int i = i_list.getSelectedIndex();
		if (i != -1) {
			instructions.remove(i);
			i_list.remove(i);
			for (int j = 0; j < instructions.size(); j++) {
				Instruction i_tmp = (Instruction) instructions.get(j);
				int jt = i_tmp.getJumpTarget();
				if (jt > i + 1) {
					i_tmp.setJumpTarget(jt - 1);
				}
			}
			update_instructions(0);
			setProgramCounter(i + 1);
			vm.setEdit(true);
		}
	}

	public void copyInstruction() {
		int i = i_list.getSelectedIndex();
		if (i != -1) {
			Instruction inst = get_instruction();
			op_select.select(Opcode.toString(inst.getOpcode()));
			t_select.select(inst.getTargetRegister());
			s_select.select(inst.getSourceRegister());
			v_text.setText(inst.getValueString());
		}
	}

	public void undoInstruction() {
		if (vm.isUndo() == true) {
			vm.undo();
		}
	}

	public void moveupInstruction() {
		int i = i_list.getSelectedIndex();
		if (i != -1 && i > 0) {
			Instruction inst = get_instruction();
			decProgramCounter();
			instructions.setElementAt(get_instruction(), i);
			instructions.setElementAt(inst, i - 1);
			update_instructions(0);
			setProgramCounter(i);
			vm.setEdit(true);
		}
	}

	public void movedownInstruction() {
		int i = i_list.getSelectedIndex();
		if (i != -1 && i < instructions.size() - 1) {
			Instruction inst = get_instruction();
			incProgramCounter();
			instructions.setElementAt(get_instruction(), i);
			instructions.setElementAt(inst, i + 1);
			update_instructions(0);
			setProgramCounter(i + 2);
			vm.setEdit(true);
		}
	}

	public boolean incProgramCounter() {
		int pc;
		pc = getProgramCounter();
		return setProgramCounter(pc + 1);
	}

	public boolean decProgramCounter() {
		int pc;
		pc = getProgramCounter();
		return setProgramCounter(pc - 1);
	}

	public int getProgramCounter() {
		int i = i_list.getSelectedIndex();
		return (i != -1 ? i + 1 : 1);
	}

	public boolean setProgramCounter(int pc) {
		boolean res = true;
		if (pc < 1) {
			pc = 1;
			res = false;
		}
		if (pc > instructions.size()) {
			pc = instructions.size();
			res = false;
		}
		i_list.select(pc - 1);
		return res;
	}

	public void resetProgramCounter() {
		setProgramCounter(1);
	}

	public void clearEditInterface() {
		op_select.select(0);
		t_select.select(0);
		s_select.select(0);
		v_text.setText("");
	}

	protected Vector getInstructions() {
		return instructions;
	}

	public void setInstructions(Vector insts) {
		instructions = insts;
		i_list.removeAll();
		int pc = 0;
		for (Enumeration e = instructions.elements(); e.hasMoreElements();) {
			pc++;
			i_list.add(instruction_string(pc, (Instruction) e.nextElement()));
		}
	}

	public void newFile() {
		instructions.clear();
		i_list.removeAll();
	}

	public boolean loadFile(File file) {
		String fname = file.getPath();
		FileInputStream fis;
		try {
			fis = new FileInputStream(fname);
		} catch (IOException e) {
			return false;
		}

		Program p_tmp = new Program(vm);

		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		try {
			String s;
			while ((s = br.readLine()) != null) {
				p_tmp.appendInstruction(new Instruction(vm, s));
			}
		} catch (IOException e) {
			return false;
		} catch (NoSuchElementException e) {
			return false;
		} catch (SEException e) {
			vm.syntaxError();
		}

		setInstructions(p_tmp.getInstructions());
		resetProgramCounter();

		return true;
	}

	public boolean saveFile(File file) {
		if (file == null) {
			return false;
		}

		String fname = file.getPath();

		try {
			if (file.exists()) {
				File bak_file = new File(fname + ".bak");
				if (!file.renameTo(bak_file)) {
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fname);
		} catch (IOException e) {
			return false;
		}

		PrintStream ps = new PrintStream(fos);
		int pc = 0;
		for (Enumeration e = instructions.elements(); e.hasMoreElements();) {
			pc++;
			ps.print(pc);
			ps.println("\t" + (Instruction) e.nextElement());
		}

		return true;
	}

	public void executionStart() {
		op_select.setEnabled(false);
		t_select.setEnabled(false);
		s_select.setEnabled(false);
		v_text.setEnabled(false);
		clr_button.setEnabled(false);
		add_button.setEnabled(false);
		ins_button.setEnabled(false);
		edt_button.setEnabled(false);
		del_button.setEnabled(false);
		cpy_button.setEnabled(false);
	}

	public void executionStop() {
		op_select.setEnabled(true);
		t_select.setEnabled(true);
		s_select.setEnabled(true);
		v_text.setEnabled(true);
		clr_button.setEnabled(true);
		add_button.setEnabled(true);
		ins_button.setEnabled(true);
		edt_button.setEnabled(true);
		del_button.setEnabled(true);
		cpy_button.setEnabled(true);
	}
}
