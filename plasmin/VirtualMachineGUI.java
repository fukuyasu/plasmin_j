/*
 * Date:	2003/10/01
 *
 * Author:	N.Fukuyasu and A.Utsuki
 *
 * $Id: VirtualMachineGUI.java,v 1.12 2006/06/16 05:34:46 fukuyasu Exp $
 *
 * Copyright (C) 2003-2006 The Plasmin Project.  All rights reserved.
 */

package plasmin;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

/*
 * User Interface of Virtual Machine - Plasmin -
 */

public class VirtualMachineGUI extends Frame {
	private VirtualMachine vm;
	private StepNum step_num;
	private SpeedBar speed_bar;

	private Button start_button;
	private Button stop_button;
	private Button step_button;
	private Button reset_button;

	private MenuItem item_new;
	private MenuItem item_open;
	private MenuItem item_save;
	private MenuItem item_saveas;
	private MenuItem item_exit;

	private MenuItem item_add;
	private MenuItem item_ins;
	private MenuItem item_edt;
	private MenuItem item_del;
	private MenuItem item_cpy;
	private MenuItem item_clr;
	private MenuItem item_undo;

	private MenuItem item_start;
	private MenuItem item_stop;
	private MenuItem item_step;
	private MenuItem item_reset;

	private MenuItem item_memory;

	private MenuItem item_about;

	VirtualMachineGUI(VirtualMachine v) {
		vm = v;

		setTitle();
		setSize(700, 500);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		setLayout(new BorderLayout());

		/* Menu bar */
		setMenuBar(menu_bar());

		/************************************************************/

		add(v.getProgram(), "Center");
		Panel panel = new Panel();
		panel.setLayout(new BorderLayout());
		panel.add(v.getRegister(), "Center");
		panel.add(exec_button(), "South");
		add(panel, "East");
	}

	public void setTitle() {
		String title = getProgramName();
		String fname = vm.getFileName();
		title += (fname != null ? " - " + fname : "");
		title += (vm.isEdit() ? " *" : "");
		super.setTitle(title);
	}

	public String getProgramName() {
		return vm.getProgramName();
	}

	public void saveAs() {
		saveas();
	}

	public void incStepNum() {
		step_num.incStepNum();
	}

	public void resetStepNum() {
		step_num.resetStepNum();
	}

	public int getSpeed() {
		return speed_bar.getSpeed();
	}

	/************************************************************/

	public void executionStart() {
		stop_button.setEnabled(true);
		item_stop.setEnabled(true);

		/********************************************************/

		start_button.setEnabled(false);
		step_button.setEnabled(false);
		reset_button.setEnabled(false);

		item_new.setEnabled(false);
		item_open.setEnabled(false);
		item_save.setEnabled(false);
		item_saveas.setEnabled(false);

		item_add.setEnabled(false);
		item_ins.setEnabled(false);
		item_edt.setEnabled(false);
		item_del.setEnabled(false);
		item_cpy.setEnabled(false);
		item_clr.setEnabled(false);

		item_start.setEnabled(false);
		item_step.setEnabled(false);
		item_reset.setEnabled(false);
	}

	public void executionStop() {
		start_button.setEnabled(true);
		step_button.setEnabled(true);
		reset_button.setEnabled(true);

		item_new.setEnabled(true);
		item_open.setEnabled(true);
		item_save.setEnabled(true);
		item_saveas.setEnabled(true);

		item_add.setEnabled(true);
		item_ins.setEnabled(true);
		item_edt.setEnabled(true);
		item_del.setEnabled(true);
		item_cpy.setEnabled(true);
		item_clr.setEnabled(true);

		item_start.setEnabled(true);
		item_step.setEnabled(true);
		item_reset.setEnabled(true);

		/********************************************************/

		stop_button.setEnabled(false);
		item_stop.setEnabled(false);
	}

	public void memoryVisible(boolean b) {
		if (b) {
			item_memory.setLabel("メモリを非表示にする");
		} else {
			item_memory.setLabel("メモリを表示する");
		}
		// item_memory.setState(b);
	}

	/************************************************************/

	/*
	 * Menu Bar of GUI.
	 */

	private Menu file_menu() {
		Menu menu = new Menu("ファイル");

		item_new = new MenuItem("新規");
		item_new.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newfile();
			}
		});
		menu.add(item_new);

		item_open = new MenuItem("ファイルを開く...");
		item_open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		menu.add(item_open);

		item_save = new MenuItem("上書き保存");
		item_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		menu.add(item_save);

		item_saveas = new MenuItem("名前を付けて保存...");
		item_saveas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveas();
			}
		});
		menu.add(item_saveas);

		menu.addSeparator();

		item_exit = new MenuItem("終了");
		item_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		menu.add(item_exit);

		return menu;
	}

	private Menu edit_menu() {
		Menu menu = new Menu("編集");

		item_add = new MenuItem("追加");
		item_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					vm.regist();
					vm.getProgram().getUndoButton().setEnabled(true);
					vm.getProgram().appendInstruction();
				} catch (SEException se) {
					vm.syntaxError();
				}
			}
		});
		menu.add(item_add);

		item_ins = new MenuItem("挿入");
		item_ins.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					vm.regist();
					vm.getProgram().getUndoButton().setEnabled(true);
					vm.getProgram().insertInstruction();
				} catch (SEException se) {
					vm.syntaxError();
				}
			}
		});
		menu.add(item_ins);

		item_edt = new MenuItem("変更");
		item_edt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					vm.regist();
					vm.getProgram().getUndoButton().setEnabled(true);
					vm.getProgram().replaceInstruction();
				} catch (SEException se) {
					vm.syntaxError();
				}
			}
		});
		menu.add(item_edt);

		item_del = new MenuItem("削除");
		item_del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.regist();
				vm.getProgram().getUndoButton().setEnabled(true);
				vm.getProgram().deleteInstruction();
			}
		});
		menu.add(item_del);

		item_cpy = new MenuItem("コピー");
		item_cpy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.getProgram().copyInstruction();
			}
		});
		menu.add(item_cpy);

		item_clr = new MenuItem("クリア");
		item_clr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.getProgram().clearEditInterface();
			}
		});
		menu.add(item_clr);

		item_undo = new MenuItem("Undo");
		item_undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.getProgram().undoInstruction();
				if (vm.isUndo() == false) {
					vm.getProgram().getUndoButton().setEnabled(false);
				}
			}
		});
		menu.add(item_undo);

		return menu;
	}

	private Menu exec_menu() {
		Menu menu = new Menu("実行");

		item_start = new MenuItem("実行");
		item_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.start();
			}
		});
		menu.add(item_start);

		item_stop = new MenuItem("停止");
		item_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.stop();
			}
		});
		menu.add(item_stop);

		item_step = new MenuItem("ステップ");
		item_step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.step();
			}
		});
		menu.add(item_step);

		item_reset = new MenuItem("リセット");
		item_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.reset();
			}
		});
		menu.add(item_reset);

		return menu;
	}

	private Menu disp_menu() {
		Menu menu = new Menu("表示");

		item_memory = new MenuItem();
		memoryVisible(false);
		item_memory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println("memory button pressed");
				vm.toggleMemory();
			}
		});
		menu.add(item_memory);
		return menu;
	}

	private Menu help_menu() {
		Menu menu = new Menu("ヘルプ");

		item_about = new MenuItem(getProgramName() + "について");
		item_about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				about();
			}
		});
		menu.add(item_about);

		return menu;
	}

	private MenuBar menu_bar() {
		MenuBar mb = new MenuBar();

		mb.add(file_menu());
		mb.add(edit_menu());
		mb.add(exec_menu());
		mb.add(disp_menu());

		mb.setHelpMenu(help_menu());

		return mb;
	}

	private Panel exec_button() {
		Panel p = new Panel();
		p.setLayout(new BorderLayout());

		speed_bar = new SpeedBar();
		p.add(speed_bar, "North");

		Panel p1 = new Panel();
		start_button = new Button("実行");
		start_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.start();
			}
		});
		p1.add(start_button);

		stop_button = new Button("停止");
		stop_button.setEnabled(false);
		stop_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.stop();
			}
		});
		p1.add(stop_button);

		step_button = new Button("ステップ");
		step_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.step();
			}
		});
		p1.add(step_button);
		p.add(p1, "Center");

		Panel p2 = new Panel();

		Label step_num_head = new Label("ステップ数:");
		p2.add(step_num_head);
		p2.add(step_num = new StepNum());

		reset_button = new Button("リセット");
		reset_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vm.reset();
			}
		});
		p2.add(reset_button);

		p.add(p2, "South");

		return p;
	}

	private void about() {
		About ad = new About(this);
		ad.setVisible(true);
	}

	private void newfile() {
		if (vm.isEdit()) {
			String title = "読み込み";
			String message = "編集中のプログラムがあります．\n" + "保存しますか?";
			Confirm cfm = new Confirm(this, title, message, "破棄する");
			cfm.setVisible(true);
			if (cfm.isOk()) {
				save();
				return;
			}
		}

		vm.newFile();
		setTitle();
	}

	private void load() {
		if (vm.isEdit()) {
			String title = "読み込み";
			String message = "編集中のプログラムがあります．\n" + "保存しますか?";
			Confirm cfm = new Confirm(this, title, message, "破棄する");
			cfm.show();
			if (cfm.isOk()) {
				save();
				return;
			}
		}

		FileDialog fdlg = new FileDialog(this, "読み込み", FileDialog.LOAD);
		fdlg.show();
		if (fdlg.getFile() != null) {
			File file = new File(fdlg.getDirectory(), fdlg.getFile());
			if (!file.exists()) {
				String title = "エラー";
				String message = file.getPath() + "は存在しません．";
				Confirm cfm = new Confirm(this, title, message);
				cfm.show();
			} else {
				if (!vm.loadFile(file)) {
					String title = "エラー";
					String message = "ファイルの読み込みに失敗しました．";
					Confirm cfm = new Confirm(this, title, message);
					cfm.show();
				}
			}
		}
		setTitle();
	}

	private void save() {
		if (!vm.saveFile()) {
			String title = "エラー";
			String message = "ファイルの保存に失敗しました．";
			Confirm cfm = new Confirm(this, title, message);
			cfm.show();
		}
	}

	private void saveas() {
		FileDialog fdlg = new FileDialog(this, "保存", FileDialog.SAVE);
		fdlg.show();
		if (fdlg.getFile() != null) {
			boolean exec_save = true;
			File file = new File(fdlg.getDirectory(), fdlg.getFile());
			if (file.exists()) {
				exec_save = false;
				String message = file.getPath() + "はすでに存在します．\n置き換えますか?";
				Confirm cfm = new Confirm(this, "保存", message, "キャンセル");
				cfm.show();
				if (cfm.isOk()) {
					exec_save = true;
				}
			}
			if (exec_save) {
				if (!vm.saveFile(file)) {
					String title = "エラー";
					String message = "ファイルの保存に失敗しました．";
					Confirm cfm = new Confirm(this, title, message);
					cfm.show();
				}
			}
		}
		setTitle();
	}

	private void exit() {
		if (vm.isEdit()) {
			String title = "読み込み";
			String message = "編集中のプログラムがあります．\n" + "保存しますか?";
			Confirm cfm = new Confirm(this, title, message, "破棄する");
			cfm.show();
			if (cfm.isOk()) {
				save();
				return;
			}
		}
		vm.exit();
	}
}

class SpeedBar extends Panel {
	private Scrollbar speed_bar;

	SpeedBar() {
		setLayout(new BorderLayout());
		add(new Label("速", Label.LEFT), "West");
		add(new Label("遅", Label.RIGHT), "East");
		speed_bar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 6, 0, 106);
		add(speed_bar, "South");
	}

	public int getSpeed() {
		return speed_bar.getValue();
	}
}

class StepNum extends Label {
	private int step_num;

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

	StepNum() {
		resetStepNum();
		setAlignment(Label.RIGHT);
	}

	public void incStepNum() {
		step_num++;
		setText(fstring(step_num, 4));
	}

	public void resetStepNum() {
		step_num = 0;
		setText(fstring(step_num, 4));
	}
}
