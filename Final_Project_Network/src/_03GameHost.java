// 게임 출제자 클래스
// 힌트를 보내는 사람이며, 정답을 맞출 수 없다.

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;


public class _03GameHost extends JFrame {
	private int port;
	private ServerSocket serverSocket = null;
	private Thread acceptThread = null;
	private Vector<ClientHandler> users = new Vector<ClientHandler>();

	private JLabel timerLabel, remainingTurnsLabel;
	private JTextArea userInfoDisplay, rulesTextArea, t_userAnswerDisplay;
	private JButton s_button, e_button;
	private String secretAnswer, hint;

	private JScrollPane scrollPane;

	private List<Boolean> userReadyList = new ArrayList<>(); // 유저들의 준비 여부를 저장하는 리스트 추가
	private boolean allUsersReady = false;
	private List<ClientHandler> clientHandlers = new ArrayList<>();
	private static final String USER_JOINED_MESSAGE = "새로운 User 입장: ";

	private boolean timerStarted = false;
	private int remainingTurns1;
	private static int userCount = 0;
	private Timer timer;


	public _03GameHost(int port) {
		super("네프 메인 게임 화면 구성");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());
		buildGUI();
		setVisible(true);

		this.port = port;
		startServer();

		for (int i = 0; i < userCount; i++) {
			userReadyList.add(false);
		}

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

		JPanel timer = TimerPanel();
		second.add(timer, BorderLayout.NORTH);

		JPanel mainQuestionPanel = main_Question_Display();
		second.add(mainQuestionPanel, BorderLayout.CENTER);

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

		s_button = new JButton("시작하기");
		s_button.setPreferredSize(new Dimension(s_button.getPreferredSize().width, 40));

		JPanel userAnswerPanel = user_answer_Display(hint);
		third.add(userAnswerPanel, BorderLayout.CENTER);
		third.add(s_button, BorderLayout.SOUTH);
		third.add(e_button, BorderLayout.NORTH);

		return third;
	}


	// 타이머 공간 지정
	private JPanel TimerPanel() {
		JPanel p = new JPanel(new BorderLayout());

		remainingTurnsLabel = new JLabel("남은 횟수: " + remainingTurns1);
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


	// (1)_1 유저 입장정보 출력되는 공간 지정
	private JPanel user_Info_Display() {
		int userNumber = ++userCount;
		JPanel p = new JPanel(new BorderLayout());

		userInfoDisplay = new JTextArea();
		userInfoDisplay.setEditable(false);
		userInfoDisplay.setFont(new Font("Arial", Font.PLAIN, 20));
		userInfoDisplay.append("User" + userNumber + "\n\n");
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
		        + "- 출제자가 단어를 선정하는 과정부터 실행자 중 한 명이 정답을 맞추는\r\n\n"
		        + "  과정까지 실행자는 단어를 볼 수 없다.\r\n\n"
		        + "- 출제자는 게임이 시작되고, 단어에 대한 설명을 한 줄 씩 적는다.\r\n\n"
		        + "- 실행자는 그 설명을 보고 연상되는 단어를 입력한다.\r\n\n"
		        + "- 실행자 중 단어의 정답이 없으면, 다음 설명으로 넘어간다.\r\n\n"
		        + "- 이 과정을 반복 후, 실행자 중 한 명이 정답을 말하면 게임이 종료된다.\r\n\n"
		        + "- 게임 종료 시 결과창이 나타난다.\r\n\n");

		rulesTextArea.setFont(new Font("굴림", Font.BOLD, 14));

		scrollPane = new JScrollPane(rulesTextArea);
		scrollPane.setPreferredSize(new Dimension(280, 330));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		p.add(scrollPane, BorderLayout.CENTER);

		return p;
	}


	// (3)_1 실행자가 적은 정답 단어 출력되는 공간 지정
	private JPanel user_answer_Display(String message) {
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

		s_button.setEnabled(false);
		s_button.addActionListener(e -> handleTurnStart());
	}


	private void handleTurnStart() {
		rulesTextArea.setText("");
		t_userAnswerDisplay.setText("");
		if (!timerStarted && remainingTurns1 > 0) {
			if (secretAnswer == null || secretAnswer.isEmpty()) {
				handleSecretAnswerInput();
				broadcastMessage("SecretAnswer");
			} else {
				handleHintInput();
			}

		} else {
			JOptionPane.showMessageDialog(null, "이미 정답을 입력했거나 힌트를 모두 사용했습니다.");
		}

		if (allUsersReady) {
			s_button.setEnabled(true);
		}

	}


	private void handleSecretAnswerInput() {
		secretAnswer = JOptionPane.showInputDialog(null, "정답을 입력하세요.");

		if (secretAnswer != null && !secretAnswer.isEmpty()) {
			secretAnswer = secretAnswer.trim();
			s_button.setEnabled(false);
			handleHintInput();
		} else {
			JOptionPane.showMessageDialog(null, "정답을 입력하세요.");
		}

	}


	private void handleHintInput() {
		hint = JOptionPane.showInputDialog(null, "힌트를 입력하세요.");
		sendHintToClients(hint);
		if (hint != null && !hint.isEmpty()) {
			printHintDisplay("HINT:" + hint);
			startTurnTimer();
		} else {
			JOptionPane.showMessageDialog(null, "힌트를 입력하세요.");
		}

	}


	// 타이머 기능
	private void startTurnTimer() {
		remainingTurns1--;
		remainingTurnsLabel.setText("남은 횟수: " + remainingTurns1);

		timerLabel.setText("10");
		timer = new Timer(1000, ev -> {
			int remainingTime = Integer.parseInt(timerLabel.getText());
			if (remainingTime > 0) {
				timerLabel.setText(Integer.toString(remainingTime - 1));
			} else {
				timer.stop();
				handleTurnTimerEnd();
			}

		});

		timer.start();
	}


	private void handleTurnTimerEnd() {
		if (remainingTurns1 == 0) {
			JOptionPane.showMessageDialog(null, "더 이상 힌트 입력 기회가 없습니다.");
			s_button.setEnabled(false);
			broadcastMessage("STOP_GAME");
			e_button.setEnabled(true);
		} else {
			handleHintInput();
		}

	}


	private void sendHintToClients(String hint) {
		for (ClientHandler c : users) {
			c.sendHint(hint);
		}

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


	public void setHint(String hint) {
		this.hint = hint;
	}


	private void broadcastMessage(String message) {
		for (ClientHandler c : clientHandlers) {
			c.sendMessage(message);
		}

	}


	// 배경이미지
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


	private void sendToAllClients(String message) {
		for (ClientHandler c : clientHandlers) {
			c.sendMessage(message);
		}

	}


	private void startServer() {
		try {
			serverSocket = new ServerSocket(port);
			printUserAnswerDisplay("서버가 시작되었습니다.");
			printUserAnswerDisplay("");
			printUserAnswerDisplay("방을 생성한 본인이 User1입니다.");
			printUserAnswerDisplay("");

			acceptThread = new Thread(() -> {
				while (acceptThread == Thread.currentThread()) {
					Socket clientSocket = null;
					try {
						clientSocket = serverSocket.accept();
						userCount++;
						int currentUserNumber = userCount;

						// 호스트 측에서 출력되는 문구
						userInfoDisplay.append("User" + currentUserNumber + "\n\n");
						t_userAnswerDisplay.append("User" + userCount + "가 연결되었습니다.\n");

						// 플레이어 측에서 출력되는 문구
						BufferedWriter out = new BufferedWriter(
						    new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
						ClientHandler cHandler = new ClientHandler(clientSocket, out);

						users.add(cHandler); // 클라이언트 핸들러 목록에 추가
						clientHandlers.add(cHandler);
						cHandler.start();

						sendToAllClients(USER_JOINED_MESSAGE + "User" + userCount + "가 연결되었습니다.");
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			});
			acceptThread.start();
			t_userAnswerDisplay.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private class ClientHandler extends Thread {

		private Socket clientSocket;
		private BufferedWriter out;


		public ClientHandler(Socket clientSocket, BufferedWriter out) {
			this.clientSocket = clientSocket;
			this.out = out;
			setUserReady(users.size(), false);
		}


		private void receiveMessages(Socket clientSocket) {
			try {
				BufferedReader in = new BufferedReader(
				    new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				out = new BufferedWriter(
				    new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

				String message;

				while ((message = in.readLine()) != null) {
					if (message.startsWith("ANSWER:")) {
						String userAnswer = message.substring("ANSWER:".length());
						checkAnswer(userAnswer);
					} else if (message.equals("AllReady")) {
						setUserReady(users.size(), true);
						s_button.setEnabled(true);
					} else if (message.startsWith("SecretAnswer:")) {
						String secretAnswerFromServer = message.substring("SecretAnswer".length());
						setSecretAnswer(secretAnswerFromServer);
					}

					broadcasting(message);

					if (message.startsWith(USER_JOINED_MESSAGE)) {
						message.substring(USER_JOINED_MESSAGE.length());
					} else {
						printUserAnswerDisplay(message);
					}

				}

			} catch (IOException e) {
				printUserAnswerDisplay("서버 읽기 오류> " + e.getMessage());
				System.exit(-1);
			}

		}


		public void setUserReady(int userCount, boolean isReady) {
			if (userCount >= 0 && userCount < userReadyList.size()) {
				userReadyList.set(userCount, isReady);
				allUsersReady = userReadyList.stream().allMatch(Boolean::valueOf);

				if (allUsersReady) {
					s_button.setEnabled(true);
					broadcastMessage("AllReady");
				}

			}

		}


		private void sendMessage(String message) {
			try {
				out.write(message + "\n");
				out.flush();
			} catch (IOException e) {
				System.err.println("클라이언트 메시지 전송 오류: " + e.getMessage());
			}

		}


		private void sendHint(String hint) {
			try {
				out.write("HINT:" + hint + "\n");
				out.flush();
			} catch (IOException e) {
				System.err.println("클라이언트 힌트 전송 오류: " + e.getMessage());
			}

		}


		private void checkAnswer(String userAnswer) {
			if (secretAnswer != null && secretAnswer.trim().equalsIgnoreCase(userAnswer.trim())) {
				printUserAnswerDisplay("정답을 맞췄습니다!");
				stopGame();
				broadcastMessage("STOP_GAME");
				e_button.setEnabled(true);
			} else {
				printUserAnswerDisplay("유저가 답을 맞추지 못했습니다.");
			}

		}


		private void stopGame() {
			if (timer != null && timer.isRunning()) {
				timer.stop();
			}

			s_button.setEnabled(false);
		}


		private void broadcasting(String message) {
			for (ClientHandler c : users) {
				c.sendMessage(message);
			}

		}


		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
	}


	public static void main(String[] args) {
		int port = 54321;

		SwingUtilities.invokeLater(() -> {
			_03GameHost server = new _03GameHost(port);
			server.startServer();
		});
	}

}