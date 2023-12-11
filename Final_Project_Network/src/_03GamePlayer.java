
// 게임 실행자 클래스
// 정답을 맞추는 사람으로 힌트 입력 불가

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;


public class _03GamePlayer extends JFrame {

	private String serverAddress;
	private int serverPort;
	private Socket socket;
	private Writer out;
	private Reader in;
	private Thread receiveThread = null;

	private JLabel timerLabel, remainingTurnsLabel;
	private JTextArea userInfoDisplay, t_userAnswerDisplay, rulesTextArea;
	private JButton b_send, r_button, e_button;
	private String secretAnswer;
	private JTextField t_input;
	private JScrollPane scrollPane;

	private List<Boolean> userReadyList = new ArrayList<>(); // 유저들의 준비 여부를 저장하는 리스트 추가
	private boolean isReady = false;

	private static int userCount = 1;
	private int remainingTurns1;
	private Timer timer;


	public _03GamePlayer(String serverAddress, int serverPort) {
		super("네프 메인 게임 화면 구성");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());
		buildGUI();
		setVisible(true);

		this.serverAddress = serverAddress;
		this.serverPort = serverPort;

		connectToServer(); // 서버에 연결
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
		gbc.weighty = 1;
		add(secondDisplay, gbc);

		JPanel thirdDisplay = third_Display();
		thirdDisplay.setBorder(new LineBorder(Color.BLACK));
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(thirdDisplay, gbc);

	}


	// (1) 로고랑 유저 목록 표시 화면
	private JPanel first_Display() {
		JPanel first = new JPanel();
		first.setLayout(new BorderLayout());

		ImagePanel labelLogo = new ImagePanel("/ImageFile/CW_logo.png");
		labelLogo.setPreferredSize(new Dimension(150, 110)); // 이미지 크기 조절
		first.add(labelLogo, BorderLayout.NORTH);

		JPanel userInfoPanel = user_Info_Display();
		first.add(userInfoPanel, BorderLayout.CENTER);

		return first;
	}


	// (2) 출제자의 질문이 적히는 센터 화면
	private JPanel second_Display() {
		JPanel second = new JPanel();
		second.setLayout(new BorderLayout());

		JPanel timer = timerPanel();
		second.add(timer, BorderLayout.NORTH);

		JPanel mainQuestionPanel = main_Question_Display();
		second.add(mainQuestionPanel, BorderLayout.CENTER);

		JPanel inputPanel = input_Display();
		second.add(inputPanel, BorderLayout.SOUTH);

		return second;
	}


	// (3) 유저 정답 입력 표시창
	private JPanel third_Display() {
		JPanel third = new JPanel();
		third.setLayout(new BorderLayout());

		e_button = new JButton("종료하기");
		e_button.setEnabled(false);
		e_button.setPreferredSize(new Dimension(e_button.getPreferredSize().width, 40));
		e_button.addActionListener((ActionEvent e) -> {
			_04GameOver gameOver = new _04GameOver();
			dispose();
		});

		r_button = new JButton("준비하기");
		r_button.setEnabled(true);
		r_button.setPreferredSize(new Dimension(r_button.getPreferredSize().width, 40));
		r_button.addActionListener(e -> {
			_03GameHost gameFrame = getGameFrame();
			if (gameFrame != null && !isReady) {
				setUserReady(userCount, true);
				r_button.setEnabled(false);
				isReady = true;
				sendMessageToServerIfAllReady();
			}

			rulesTextArea.setText("");
		});

		JPanel userAnswerPanel = user_answer_Display();

		third.add(userAnswerPanel, BorderLayout.CENTER);
		third.add(r_button, BorderLayout.SOUTH);
		third.add(e_button, BorderLayout.NORTH);

		return third;
	}


	// 타이머 공간 지정
	private JPanel timerPanel() {
		JPanel p = new JPanel(new BorderLayout());

		remainingTurnsLabel = new JLabel("남은 횟수: " + remainingTurns1); // 변경
		remainingTurnsLabel.setBounds(30, 100, 200, 50);
		remainingTurnsLabel.setFont(new Font("고딕", Font.PLAIN, 20));
		remainingTurnsLabel.setPreferredSize(new Dimension(150, 40));

		timerLabel = new JLabel();
		timerLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

		p.add(remainingTurnsLabel, BorderLayout.WEST);
		p.add(timerLabel, BorderLayout.EAST);

		return p;
	}


	// 타이머 작동 기능
	private void startTimer() {
		remainingTurns1--;
		remainingTurnsLabel.setText("남은 횟수: " + remainingTurns1);

		timerLabel.setText("15"); // 시작할 타이머 초기 값

		if (remainingTurns1 > 0) {
			b_send.setEnabled(true);
			t_input.setEnabled(true);
		} else {
			b_send.setEnabled(false);
			t_input.setEnabled(false);
		}

		timer = new Timer(1000, ev -> {
			int remainingTime = Integer.parseInt(timerLabel.getText());
			if (remainingTime > 0) {
				timerLabel.setText(Integer.toString(remainingTime - 1));
			} else {
				timer.stop();
				handleTimerEnd();
			}

		});

		timer.start();
	}


	private void handleTimerEnd() {
		if (remainingTurns1 == 0) {
			r_button.setEnabled(false); // 힌트 입력 기회가 없을 때 버튼 비활성화
			stopGame();
			e_button.setEnabled(true);
		}

	}


	// (1)_1 유저 입장정보 출력되는 공간 지정
	private JPanel user_Info_Display() {
		++userCount;
		JPanel p = new JPanel(new BorderLayout());

		userInfoDisplay = new JTextArea();
		userInfoDisplay.setEditable(false);
		userInfoDisplay.setFont(new Font("Arial", Font.PLAIN, 20));

		p.add(new JScrollPane(userInfoDisplay), BorderLayout.CENTER);

		return p;
	}


	// (2)_1 질문들이 출력되는 공간 지정
	private JPanel main_Question_Display() {
		JPanel p = new JPanel(new BorderLayout());

		rulesTextArea = new JTextArea();
		rulesTextArea.setEditable(false);
		rulesTextArea.setWrapStyleWord(true);
		rulesTextArea.setLineWrap(true);
		rulesTextArea.setText(
		    "\n\n\n\n\n\n\n규칙\r\n\n"
		        + "- 게임 출제자는 시작 전, 실행자가 맞출 단어를 선정한다.\r\n\n"
		        + "- 출제자가 단어를 선정하는 과정부터 실행자 중 한 명이 정답을\r\n\n"
		        + "  맞추는 과정까지 실행자는 단어를 볼 수 없다.\r\n\n"
		        + "- 출제자는 게임이 시작되고, 단어에 대한 설명을 한 줄 씩 적는다.\r\n\n"
		        + "- 실행자는 그 설명을 보고 연상되는 단어를 입력한다.\r\n\n"
		        + "- 실행자 중 단어의 정답이 없으면, 다음 설명으로 넘어간다.\r\n\n"
		        + "- 이 과정을 반복 후, 실행자가 정답을 말하면 게임이 종료된다.\r\n\n"
		        + "- 게임 종료 시 결과창이 나타난다.\r\n\n");

		rulesTextArea.setFont(new Font("굴림", Font.BOLD, 14));

		scrollPane = new JScrollPane(rulesTextArea);
		scrollPane.setPreferredSize(new Dimension(280, 330));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		p.add(scrollPane, BorderLayout.CENTER);

		return p;
	}


	// (2)_2 출제자나 실행자가 질문과 정답을 적는 공간 지정
	private JPanel input_Display() {
		JPanel p = new JPanel(new BorderLayout());

		t_input = new JTextField(18);
		b_send = new JButton("보내기");
		b_send.setPreferredSize(new Dimension(b_send.getPreferredSize().width, 40));

		b_send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		t_input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		p.add(t_input, BorderLayout.CENTER);
		p.add(b_send, BorderLayout.EAST);

		return p;
	}


	// (3)_1 실행자가 적은 정답 단어 출력되는 공간 지정
	private JPanel user_answer_Display() {
		JPanel p = new JPanel(new BorderLayout());

		t_userAnswerDisplay = new JTextArea();
		t_userAnswerDisplay.setEditable(false);
		t_userAnswerDisplay.setWrapStyleWord(true);
		t_userAnswerDisplay.setLineWrap(true);
		t_userAnswerDisplay.setPreferredSize(new Dimension(150, 200));

		p.add(new JScrollPane(t_userAnswerDisplay), BorderLayout.CENTER);

		return p;
	}


	// 남은 횟수 감소 기능
	public void setRemainingTurns(String value) {
		value.trim();
		remainingTurns1 = Integer.parseInt(value.trim());
		remainingTurnsLabel.setText("남은 횟수: " + remainingTurns1);
	}


	private void printHintDisplay(String message) {
		rulesTextArea.append(message + "\n");
		rulesTextArea.setCaretPosition(rulesTextArea.getDocument().getLength());
	}


	private void printUserAnswerDisplay(String message) {
		t_userAnswerDisplay.append(message + "\n");
		t_userAnswerDisplay.setCaretPosition(t_userAnswerDisplay.getDocument().getLength());

	}


	public void setSecretAnswer(String secretAnswer) {
		this.secretAnswer = secretAnswer;
	}


	public void setUserReady(int userCount, boolean isReady) {
		if (userCount >= 0 && userCount < userReadyList.size()) {
			userReadyList.set(userCount, isReady);
			sendMessageToServerIfAllReady();
		}

	}


	private void sendMessageToServerIfAllReady() {
		boolean allReady = !userReadyList.contains(false);
		if (allReady) {
			sendMessageToServer("AllReady");
		}

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


	private void connectToServer() {
		try {
			socket = new Socket(serverAddress, serverPort);
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			for (int i = 1; i <= userCount; i++) {
				userInfoDisplay.append("User" + i + "\n\n");
			}

			updateConnectedUserCount(userCount);

			receiveThread = new Thread(() -> {
				while (receiveThread == Thread.currentThread()) {
					receiveMessage();
				}

			});
			receiveThread.start();

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("클라이언트 연결 오류> " + e.getMessage());
			JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다. 프로그램을 종료합니다.");
			System.exit(-1);
		}

	}


	private void sendMessage() {

		String userAnswer = t_input.getText().trim();
		String message = "User: " + userAnswer + "\n";

		if (isReady && remainingTurns1 > 0) {
			checkAnswer(userAnswer);
			sendMessageToServer("ANSWER:" + userAnswer);
		}

		if (!message.isEmpty()) {
			try {
				((BufferedWriter) out).write(message);
				out.flush();
				t_input.setEnabled(false);
				b_send.setEnabled(false);
			} catch (IOException e) {
				System.err.println("클라이언트 메시지 전송 오류> " + e.getMessage());
			}

		}

		t_input.setText("");
	}


	private void checkAnswer(String userAnswer) {
		if (isReady && secretAnswer != null) {
			if (userAnswer != null && userAnswer.trim().equalsIgnoreCase(secretAnswer.trim())) {
				printUserAnswerDisplay("정답을 맞췄습니다!");
				sendMessageToServer("ANSWER_CORRECT");

				stopGame();
			}

		} else {
			printUserAnswerDisplay("틀렸습니다. 다시 시도하세요.");
			sendMessageToServer("ANSWER_INCORRECT");
		}

	}


	private void stopGame() {
		if (timer != null && timer.isRunning()) {
			timer.stop();
		}

		t_input.setEnabled(false);
		b_send.setEnabled(false);
	}


	private void sendMessageToServer(String message) {
		try {
			((BufferedWriter) out).write(message + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.println("클라이언트 메시지 전송 오류> " + e.getMessage());
		}

	}


	private void receiveMessage() {
		try {
			String inMsg = ((BufferedReader) in).readLine();
			if (inMsg != null) {
				if (inMsg.startsWith("SecretAnswer")) {
					String secretAnswerFromServer = inMsg.substring("SecretAnswer".length());

					setSecretAnswer(secretAnswerFromServer);
				} else if (inMsg.startsWith("HINT:")) {
					String hint = inMsg.substring(5);
					printHintDisplay("HINT:" + hint);
					startTimer();
				} else if (inMsg.equals("STOP_GAME")) {
					stopGame();
					printUserAnswerDisplay("정답을 맞췄습니다!");
					printUserAnswerDisplay("게임을 종료합니다.");
					e_button.setEnabled(true);
				} else {
					printUserAnswerDisplay(inMsg);
				}

			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "클라이언트 일반 수신 오류> " + e.getMessage());
		}

	}


	// 기존 유저 정보를 초기화하지 않고, 새로운 유저 수만큼 추가
	private void updateConnectedUserCount(int newUserCount) {
		SwingUtilities.invokeLater(() -> {
			int currentLineCount = userInfoDisplay.getLineCount();
			for (int i = currentLineCount + 1; i <= newUserCount; i++) {
				userInfoDisplay.append("User" + i + "\n\n");
			}

		});
	}


	private _03GameHost getGameFrame() {
		for (Frame frame : Frame.getFrames()) {
			if (frame instanceof _03GameHost) { return (_03GameHost) frame; }

		}

		return null;
	}


	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;

		SwingUtilities.invokeLater(() -> {
		    new _03GamePlayer(serverAddress, serverPort);
		});

	}
}