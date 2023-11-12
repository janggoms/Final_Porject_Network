package NetworkPrograming;


import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


public class CreateRoomUI extends JFrame {
	private JButton c_room, e_room, g_turns;
	private JLabel label;
	private JTextArea createdRoom;
	private JTextField roomName;


	public CreateRoomUI() {
		setTitle("캐치 워드");
		setSize(1000, 700);

		setLocationRelativeTo(null);
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());

		buildGUI();
		setVisible(true);
	}


	private void buildGUI() {
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel first = firstDisplay();
		first.setBorder(new LineBorder(Color.black));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 2.0;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;

		add(first, gbc);

		JPanel second = secondDisplay();
		second.setBorder(new LineBorder(Color.BLACK));

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;

		add(second, gbc);
	}


	// 방 입장 화면
	private JPanel firstDisplay() {
		JPanel first = new JPanel();

		first.setLayout(null);

		label = new JLabel("현재 생성된 방");
		label.setBounds(100, 10, 100, 50);
		first.add(label);

		e_room = new JButton("방 1");
		e_room.setBounds(100, 100, 500, 75);
		first.add(e_room);

		// Font font = label.getFont();
		// first.setFont(new Font(font.getName(), Font.PLAIN, 40));

		return first;
	}


	// 방 생성 화면
	private JPanel secondDisplay() {
		JPanel second = new JPanel();
		second.setLayout(null);

		label = new JLabel("방생성하기");
		label.setBounds(30, 10, 100, 50);
		second.add(label);

		label = new JLabel("방이름");
		label.setBounds(35, 200, 100, 50);
		second.add(label);

		roomName = new JTextField(30);
		roomName.setBounds(25, 250, 250, 30);
		second.add(roomName);

		label = new JLabel("고개회수");
		label.setBounds(35, 300, 100, 50);
		second.add(label);

		g_turns = new JButton("5");
		g_turns.setBounds(35, 350, 50, 50);
		second.add(g_turns);

		g_turns = new JButton("7");
		g_turns.setBounds(85, 350, 50, 50);
		second.add(g_turns);

		g_turns = new JButton("10");
		g_turns.setBounds(135, 350, 50, 50);
		second.add(g_turns);

		g_turns = new JButton("15");
		g_turns.setBounds(185, 350, 50, 50);
		second.add(g_turns);

		g_turns = new JButton("20");
		g_turns.setBounds(235, 350, 50, 50);
		second.add(g_turns);

		c_room = new JButton("생성");
		c_room.setBounds(228, 633, 100, 30);
		second.add(c_room);

		return second;
	}


	public static void main(String[] args) {
		new CreateRoomUI();
	}
}
