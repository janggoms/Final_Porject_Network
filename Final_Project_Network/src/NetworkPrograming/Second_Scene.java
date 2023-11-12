package NetworkPrograming;


import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


// 방생성 화면
public class Second_Scene extends JFrame {
	private JButton c_room, e_room;


	public Second_Scene() {

		buildGUI();
		setTitle("캐치 워드");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}


	private void buildGUI() {
		// add(leftCreateDisplayPanel(), BorderLayout.CENTER);

		JPanel p_input = new JPanel(new GridLayout(0, 3));
		p_input.add(createControlPanel());
		add(p_input, BorderLayout.SOUTH);
	}


	private void leftCreateDisplayPanel() {
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(2, 2));

		// return panel1;
	}


	private void rightCreateDisplayPanel() {

	}


	private JPanel createControlPanel() {

		JPanel p = new JPanel(new GridLayout(0, 1));

		c_room = new JButton("생성하기");
		return p;
	}


	public static void main(String[] args) {
		new Second_Scene();

	}

}
