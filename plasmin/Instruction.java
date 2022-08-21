/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu
 *
 * $Id: Instruction.java,v 1.8 2004/11/24 09:54:25 fukuyasu Exp $
 *
 * Copyright (C) 2003-2004 The Plasmin Project. All rights reserved.
 */

package plasmin;

import java.util.*;

public class Instruction {
	private int opcode;
	private String treg, sreg;
	private int value;

	private void set_instruction(VirtualMachine vm, String op, String t, String s, String v) throws SEException {
		opcode = Opcode.parseOpcode(op);
		if (Opcode.hasTargetRegister(opcode)) {
			int sort = Opcode.getTargetRegisterSort(opcode);
			if (vm.getRegister().isDataRegister(t)) {
				if (sort == Opcode.P) {
					throw (new SEException());
				}
			} else if (vm.getRegister().isPointerRegister(t)) {
				if (sort == Opcode.D) {
					throw (new SEException());
				}
			}
			treg = vm.getRegister().getName(t);
		} else {
			treg = "";
		}

		if (Opcode.hasSourceRegister(opcode)) {
			int sort = Opcode.getSourceRegisterSort(opcode);
			if (vm.getRegister().isDataRegister(s)) {
				if (sort == Opcode.P) {
					throw (new SEException());
				}
			} else if (vm.getRegister().isPointerRegister(s)) {
				if (sort == Opcode.D) {
					throw (new SEException());
				}
			}
			sreg = vm.getRegister().getName(s);
		} else {
			sreg = "";
		}

		if (Opcode.hasValue(opcode)) {
			try {
				value = Integer.parseInt(v);
			} catch (NumberFormatException e) {
				value = 0;
			}
		} else {
			value = 0;
		}
	}

	Instruction(VirtualMachine vm, String str) throws NoSuchElementException, SEException {
		StringTokenizer st = new StringTokenizer(str);

		st.nextToken(); // Ignore first token.

		String op = st.nextToken();
		int opc = Opcode.parseOpcode(op);
		String t = (Opcode.hasTargetRegister(opc) ? st.nextToken() : "");
		String s = (Opcode.hasSourceRegister(opc) ? st.nextToken() : "");
		String v = (Opcode.hasValue(opc) ? st.nextToken() : "");

		set_instruction(vm, op, t, s, v);
	}

	Instruction(VirtualMachine vm, String op, String t, String s, String v) throws SEException {
		set_instruction(vm, op, t, s, v);
	}

	public String toString() {
		String res = "";

		res += Opcode.toString(opcode);
		res += (Opcode.hasTargetRegister(opcode) ? " " + treg : "");
		res += (Opcode.hasSourceRegister(opcode) ? " " + sreg : "");
		res += (Opcode.hasValue(opcode) ? " " + value : "");

		return res;
	}

	public boolean execute(VirtualMachine vm) {
		return Opcode.execute(vm, opcode, treg, sreg, value);
	}

	public int getJumpTarget() {
		return (Opcode.isJump(opcode) ? value : -1);
	}

	public void setJumpTarget(int jt) {
		if (Opcode.isJump(opcode)) {
			value = jt;
		}
	}

	public int getOpcode() {
		return opcode;
	}

	public String getTargetRegister() {
		return (Opcode.hasTargetRegister(opcode) ? treg : "");
	}

	public String getSourceRegister() {
		return (Opcode.hasSourceRegister(opcode) ? sreg : "");
	}

	public String getValueString() {
		return (Opcode.hasValue(opcode) ? String.valueOf(value) : "");
	}
}
