/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu
 *
 * $Id: Opcode.java,v 1.12 2004/11/24 09:54:25 fukuyasu Exp $
 *
 * Copyright (C) 2004 The Plasmin Project. All rights reserved.
 */

/*
 * 各 Opcode の意味，および文字列との相互変換
 */

package plasmin;

import java.util.Vector;

public class Opcode {
	private int opc;
	private String opstr;
	private boolean has_treg, has_sreg, has_value;
	private int s_treg, s_sreg;

	private static final int
			LDR = 10, CPR = 11,
			INC = 20, DEC = 21,
			INP = 30,
			LRM = 40, STM = 41, ALM = 42, FRM = 43,
			RND = 50, GTS = 51,
			JMP = 80, JEQ = 81, JLT = 82,
			END = 90, NOP = 99;

	public static final int N = 0, D = 1, P = 2, DP = 3;

	private static final Opcode[] opcodes = {
			new Opcode(LDR, "LDR", D,  N,  true),
			new Opcode(CPR, "CPR", DP, DP, false),
			new Opcode(INC, "INC", DP, N,  false),
			new Opcode(DEC, "DEC", DP, N,  false),
			// new Opcode(INP, "INP", D, N, false),
			new Opcode(JMP, "JMP", N,  N,  true),
			new Opcode(JEQ, "JEQ", DP, DP, true),
			new Opcode(JLT, "JLT", D,  D,  true),
			new Opcode(LRM, "LRM", D,  P,  false),
			new Opcode(STM, "STM", P,  D,  false),
			new Opcode(ALM, "ALM", P,  D,  false),
			new Opcode(FRM, "FRM", P,  N,  false),
			new Opcode(RND, "RND", P,  D,  false),
			new Opcode(GTS, "GTS", P,  D,  false),
			new Opcode(END, "END", N,  N,  false),
			new Opcode(NOP, "NOP", N,  N,  false),
		};

	private static Opcode get_opcode(String s) {
		int i = 0;
		if (s.equals("LMR"))
			s = "LRM";
		while (opcodes[i].opc != NOP && !opcodes[i].opstr.equals(s))
			i++;
		return opcodes[i];
	}

	private static Opcode get_opcode(int op) {
		int i = 0;
		while (opcodes[i].opc != NOP && opcodes[i].opc != op)
			i++;
		return opcodes[i];
	}

	Opcode(int op, String str, int treg, int sreg, boolean value) {
		opc = op;
		opstr = str;
		has_treg = (treg != N);
		has_sreg = (sreg != N);
		has_value = value;
		s_treg = treg;
		s_sreg = sreg;
	}

	public static boolean hasTargetRegister(int op) {
		return get_opcode(op).has_treg;
	}

	public static boolean hasSourceRegister(int op) {
		return get_opcode(op).has_sreg;
	}

	public static boolean hasValue(int op) {
		return get_opcode(op).has_value;
	}

	public static int getTargetRegisterSort(int op) {
		return get_opcode(op).s_treg;
	}

	public static int getSourceRegisterSort(int op) {
		return get_opcode(op).s_sreg;
	}

	public static boolean isJump(int op) {
		boolean res;
		switch (op) {
		case JEQ:
		case JLT:
		case JMP:
			res = true;
			break;
		default:
			res = false;
			break;
		}
		return res;
	}

	public static Vector getOpcodeList() {
		Vector v = new Vector();
		for (int i = 0; i < opcodes.length; i++) {
			v.add(opcodes[i].opstr);
		}
		return v;
	}

	public static int parseOpcode(String s) {
		return get_opcode(s).opc;
	}

	public static String toString(int op) {
		return get_opcode(op).opstr;
	}

	public static boolean execute(VirtualMachine vm, int op, String t, String s, int v) {
		boolean res = true;

		try {
			switch (op) {
			case LDR:
				vm.setRegisterValue(t, v);
				res = vm.incProgramCounter();
				break;
			case CPR:
				vm.setRegisterValue(t, vm.getRegisterValue(s));
				res = vm.incProgramCounter();
				break;
			case INC:
				vm.setRegisterValue(t, vm.getRegisterValue(t) + 1);
				res = vm.incProgramCounter();
				break;
			case DEC:
				vm.setRegisterValue(t, vm.getRegisterValue(t) - 1);
				res = vm.incProgramCounter();
				break;
			case INP:
				res = vm.incProgramCounter();
				break;
			case JMP:
				res = vm.setProgramCounter(v);
				break;
			case JEQ:
				if (vm.getRegisterValue(t) == vm.getRegisterValue(s)) {
					res = vm.setProgramCounter(v);
				} else {
					res = vm.incProgramCounter();
				}
				break;
			case JLT:
				if (vm.getRegisterValue(t) < vm.getRegisterValue(s)) {
					res = vm.setProgramCounter(v);
				} else {
					res = vm.incProgramCounter();
				}
				break;
			case LRM:
				vm.setRegisterValue(t, vm.getMemoryValue(s));
				res = vm.incProgramCounter();
				break;
			case STM:
				vm.setMemoryValue(t, vm.getRegisterValue(s));
				res = vm.incProgramCounter();
				break;
			case ALM:
				vm.allocateMemory(t, s);
				res = vm.incProgramCounter();
				break;
			case FRM:
				vm.freeMemory(t);
				res = vm.incProgramCounter();
				break;
			case RND:
				vm.setRandomNumbers(t, s);
				res = vm.incProgramCounter();
				break;
			case GTS:
				vm.getString(t, s);
				res = vm.incProgramCounter();
				break;
			case NOP:
				res = vm.incProgramCounter();
				break;
			case END:
			default:
				res = false;
			}
		} catch (SVException e) {
			vm.segmentationFault();
			res = false;
		}
		if (op != END) {
			vm.incStepNum();
		}
		return res;
	}
}
