package NetworkPrograming;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;


public class GameUI extends JFrame {

	private JLabel labelLogo;
	private JTextArea userInfoDisplay;
	private JTextArea t_questionDisplay;
	private JTextField t_Input;
	private JLabel labelAnswerLogo;
	private JTextArea t_userAnswerDisplay;


	public GameUI() {
		super("네프 메인 게임 화면 구성");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());

		buildGUI();
		setVisible(true);
	}


	// 최초 뼈대 1:2:1 비율의 화면 구성
	private void buildGUI() {
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel firstDisplay = first_Display();
		firstDisplay.setBorder(new LineBorder(Color.BLACK));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(firstDisplay, gbc);

		JPanel secondDisplay = second_Display();
		secondDisplay.setBorder(new LineBorder(Color.BLACK));
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 2;
		add(secondDisplay, gbc);

		JPanel thirdDisplay = third_Display();
		thirdDisplay.setBorder(new LineBorder(Color.BLACK));
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 1;
		add(thirdDisplay, gbc);
	}


	// (1) 로고와 유저입장 정보가 뜨는 왼쪽 화면
	private JPanel first_Display() {
		JPanel first = new JPanel();
		first.setLayout(new BorderLayout());

		labelLogo = new JLabel("로고");
		first.add(labelLogo, BorderLayout.NORTH);

		JPanel userInfoPanel = user_Info_Display();
		first.add(userInfoPanel, BorderLayout.CENTER);

		return first;
	}


	// (1)_1 유저 입장정보 출력되는 공간 지정
	private JPanel user_Info_Display() {
		JPanel p = new JPanel(new BorderLayout());

		userInfoDisplay = new JTextArea();
		userInfoDisplay.setEditable(false);

		p.add(new JScrollPane(userInfoDisplay), BorderLayout.CENTER);

		return p;
	}


	// (2) 출제자의 질문이 적히는 센터 화면
	private JPanel second_Display() {
		JPanel second = new JPanel();
		second.setLayout(new BorderLayout());

		labelLogo = new JLabel("시간제한 바 or 질문 횟수");
		second.add(labelLogo, BorderLayout.NORTH);

		JPanel mainQuestionPanel = main_Question_Display();
		second.add(mainQuestionPanel, BorderLayout.CENTER);

		JPanel inputPanel = input_Display();
		second.add(inputPanel, BorderLayout.SOUTH);

		return second;
	}


	// (2)_1 질문들이 출력되는 공간 지정
	private JPanel main_Question_Display() {
		JPanel p = new JPanel(new BorderLayout());

		t_questionDisplay = new JTextArea();
		t_questionDisplay.setEditable(false);

		p.add(new JScrollPane(t_questionDisplay), BorderLayout.CENTER);

		return p;
	}


	// (2)_2 출제자나 실행자가 질문과 정답을 적는 공간 지정
	private JPanel input_Display() {
		JPanel p = new JPanel(new BorderLayout());

		t_Input = new JTextField(18);
		JButton b_send = new JButton("보내기");

		p.add(t_Input, BorderLayout.CENTER);
		p.add(b_send, BorderLayout.EAST);

		return p;
	}


	// (3) 실행자의 정답 정보가 보여지는 오른쪽 화면
	private JPanel third_Display() {
		JPanel third = new JPanel();
		third.setLayout(new BorderLayout());

		labelAnswerLogo = new JLabel("정답 정보");
		third.add(labelAnswerLogo, BorderLayout.NORTH);

		JPanel userAnswerPanel = user_answer_Display();
		third.add(userAnswerPanel, BorderLayout.CENTER);

		return third;
	}


	// (3)_1 실행자가 적은 정답 단어 출력되는 공간 지정
	private JPanel user_answer_Display() {
		JPanel p = new JPanel(new BorderLayout());

		t_userAnswerDisplay = new JTextArea();
		t_userAnswerDisplay.setEditable(false);

		p.add(new JScrollPane(t_userAnswerDisplay), BorderLayout.CENTER);

		return p;
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
		    new GameUI();
		});
	}
}
