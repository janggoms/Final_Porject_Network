package NetworkPrograming;

// 게임 출제자 화면 -> 즉 사회자. 질문 적어주는 사람

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;


public class GameHost extends JFrame {

	private JLabel labelLogo;
	private JTextArea userInfoDisplay;
	private JTextArea t_questionDisplay;
	private JTextField t_Input;
	private JLabel labelAnswerLogo;
	private JTextArea t_userAnswerDisplay;
	
    private JLabel remainingTurns;
    private String selectedCheckbox;
    
	private JLabel timerLabel;
	private Timer timer;
	private int count = 30; // 초기 카운트 값

	public GameHost() {
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


	private JPanel first_Display() {
		JPanel first = new JPanel();
		first.setLayout(new BorderLayout());

		ImagePanel labelLogo = new ImagePanel("/NetworkPrograming/Pic/CW_logo.png");
		labelLogo.setPreferredSize(new Dimension(150, 110)); // 이미지 크기 조절
		first.add(labelLogo, BorderLayout.NORTH);

		JPanel userInfoPanel = user_Info_Display();
		first.add(userInfoPanel, BorderLayout.CENTER);

		return first;
	}


	class ImagePanel extends JPanel {
		private ImageIcon imageIcon;


		public ImagePanel(String imagePath) {
			imageIcon = new ImageIcon(getClass().getResource(imagePath));
		}


		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
		}
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

		JPanel timer = updateTimer();
		second.add(timer, BorderLayout.NORTH);

		JPanel mainQuestionPanel = main_Question_Display();
		second.add(mainQuestionPanel, BorderLayout.CENTER);

		JPanel inputPanel = input_Display();
		second.add(inputPanel, BorderLayout.SOUTH);

		return second;
	}


	public JPanel updateTimer() {
	    JPanel p = new JPanel(new BorderLayout());

	    remainingTurns = new JLabel("남은 횟수: ");
	    remainingTurns.setBounds(30, 100, 200, 50);
	    remainingTurns.setFont(new Font("고딕", Font.PLAIN, 20));
	    add(remainingTurns);

	    JLabel timerLabel = new JLabel();
	    timerLabel.setFont(new Font("Arial", Font.PLAIN, 30));
	    timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

	    timerLabel.setText(Integer.toString(count));

	    timer = new Timer(1000, e -> {
	        if (--count > 0) {
	            timerLabel.setText(Integer.toString(count));
	        } else {
	            timer.stop();
	            timerLabel.setText("Next Question");
	        }
	    });

	    timer.start();

	    p.add(remainingTurns, BorderLayout.WEST);
	    p.add(timerLabel, BorderLayout.EAST);

	    return p;
	}

	public void setRemainingTurns(String value) {
	    selectedCheckbox = value;
	    remainingTurns.setText("남은 횟수: " + selectedCheckbox);
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

		b_send.setPreferredSize(new Dimension(b_send.getPreferredSize().width, 40));

		p.add(t_Input, BorderLayout.CENTER);
		p.add(b_send, BorderLayout.EAST);

		return p;
	}


	// (3) 실행자의 정답 정보가 보여지는 오른쪽 화면
	private JPanel third_Display() {
		JPanel third = new JPanel();
		third.setLayout(new BorderLayout());

		labelAnswerLogo = new JLabel("정답 정보");
		labelAnswerLogo.setFont(new Font("NamunGothic", Font.ITALIC, 30));
		labelAnswerLogo.setHorizontalAlignment(SwingConstants.CENTER);

		JButton s_button = new JButton("시작하기");
		s_button.setPreferredSize(new Dimension(s_button.getPreferredSize().width, 40));

		JPanel userAnswerPanel = user_answer_Display();

		third.add(userAnswerPanel, BorderLayout.CENTER);
		third.add(s_button, BorderLayout.SOUTH);
		third.add(labelAnswerLogo, BorderLayout.NORTH);

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
		    new GameHost();
		});
	}
}