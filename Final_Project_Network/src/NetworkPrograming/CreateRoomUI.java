package NetworkPrograming;


import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


public class CreateRoomUI extends JFrame {
	private JButton c_room; // 방생성
	private JButton e_room; // 방입장
	private JButton g_turns; // 고개횟수
	private JLabel label; // 표시
	private JTextField roomName; // 방생성 입력란


	public CreateRoomUI() {
		setTitle("네프 방생성 화면 구성");
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
		JPanel p = new JPanel();

		p.setLayout(null);

		label = new JLabel("현재 생성된 방");
		label.setBounds(100, 10, 200, 100);
		label.setFont(new Font("굴림", Font.BOLD, 25));
		p.add(label);

		e_room = new JButton("방 1");
		e_room.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GamePlayer T_Frame = new GamePlayer();
				T_Frame.setVisible(true);
				dispose(); // 현재의 프레임을 닫습니다.
			}
		});

		e_room.setBounds(100, 100, 500, 75);
		p.add(e_room);

		return p;
	}


	// 방 생성 화면
	private JPanel secondDisplay() {
		JPanel p = new JPanel();
		p.setLayout(null);

		label = new JLabel("방생성하기");
		label.setBounds(30, 10, 200, 100);
		label.setFont(new Font("굴림", Font.BOLD, 25));
		p.add(label);

		label = new JLabel("방이름");
		label.setBounds(35, 200, 100, 50);
		p.add(label);

		roomName = new JTextField(30);
		roomName.setBounds(35, 250, 250, 30);
		p.add(roomName);

		label = new JLabel("고개회수");
		label.setBounds(35, 330, 100, 50);
		p.add(label);

		g_turns = new JButton("5");
		g_turns.setBounds(35, 390, 50, 50);
		p.add(g_turns);

		g_turns = new JButton("7");
		g_turns.setBounds(85, 390, 50, 50);
		p.add(g_turns);

		g_turns = new JButton("10");
		g_turns.setBounds(135, 390, 50, 50);
		p.add(g_turns);

		g_turns = new JButton("15");
		g_turns.setBounds(185, 390, 50, 50);
		p.add(g_turns);

		g_turns = new JButton("20");
		g_turns.setBounds(235, 390, 50, 50);
		p.add(g_turns);

		c_room = new JButton("생성");
		c_room.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GamePlayer secondFrame = new GamePlayer();
				secondFrame.setVisible(true);
				dispose(); // 현재의 프레임을 닫습니다.
			}
		});

		c_room.setBounds(228, 633, 100, 30);
		p.add(c_room);

		return p;
	}


	public static void main(String[] args) {
		new CreateRoomUI();
	}
}
