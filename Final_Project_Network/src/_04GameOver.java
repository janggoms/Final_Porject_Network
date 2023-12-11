
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;


public class _04GameOver extends JFrame {

	private JTextArea userOutDisplay;
	private JLabel f_result01, f_result02;
	private JButton b_restart, b_exit;


	public _04GameOver() {
		super("네프 최종 게임 종료화면 구성");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());

		buildGUI();
		setVisible(true);
	}


	private void buildGUI() {
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel firstDisplay = first_Display();
		firstDisplay.setBorder(new LineBorder(Color.BLACK));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 4;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(firstDisplay, gbc);

		JPanel secondDisplay = second_Display();
		secondDisplay.setBorder(new LineBorder(Color.BLACK));
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		add(secondDisplay, gbc);
	}


	// GAMEOVER 문구가 뜨는 화면
	private JPanel first_Display() {
		JPanel first = new JPanel(null);

		f_result01 = new JLabel("GAME OVER");
		f_result01.setBounds(210, 220, 350, 200);
		f_result01.setFont(new Font("굴림", Font.BOLD, 50));

		first.add(f_result01);

		return first;
	}


	// 유저 퇴장알림과 재시작, 퇴장 버튼이 있는 화면
	private JPanel second_Display() {
		JPanel second = new JPanel(new BorderLayout());

		userOutDisplay = new JTextArea();
		userOutDisplay.setEditable(false);
		b_restart = new JButton("재시작하기");
		b_restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int serverPort = 54321;
				_03GamePlayer playerFrame = new _03GamePlayer("localhost", serverPort);
				playerFrame.setVisible(true);
				dispose(); // 현재의 프레임을 닫습니다.
			}
		});
		b_exit = new JButton("종료하기");
		b_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_02Room S_Frame = new _02Room();
				S_Frame.setVisible(true);
				dispose(); // 현재의 프레임을 닫습니다.
			}
		});

		b_restart.setPreferredSize(new Dimension(b_restart.getPreferredSize().width, 60));
		b_exit.setPreferredSize(new Dimension(b_exit.getPreferredSize().width, 60));

		second.add(new JScrollPane(userOutDisplay), BorderLayout.CENTER);
		JPanel button2 = new JPanel(new GridLayout(2, 0));
		button2.add(b_restart);
		button2.add(b_exit);
		second.add(button2, BorderLayout.SOUTH);

		return second;
	}


	public static void main(String[] args) {
		new _04GameOver();
	}
}