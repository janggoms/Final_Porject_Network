// 게임 실행자 클래스

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

   private JLabel labelLogo, labelAnswerLogo, timerLabel, remainingTurns;
   private JTextArea userInfoDisplay, t_userAnswerDisplay, rulesTextArea;
   private JButton b_send, s_button;
   private String selectedCheckbox, secretAnswer, hint;

   private JTextField t_input;
   private JScrollPane scrollPane;
   private Timer timer;

   private List<Boolean> userReadyList = new ArrayList<>(); // 유저들의 준비 여부를 저장하는 리스트 추가
   private ArrayList<String> answers = new ArrayList<>(); // 참가자들의 정답을 저장할 리스트

   private boolean showRules = true;
   private boolean timerStarted = false;

   private int count = 30; // 초기 카운트 값
   private static int userCount = 1;
   
   private String lastHint = "";

   private Socket socket;
   private Writer out;
   private Reader in;

   private Thread receiveThread = null;
   private ServerListener serverListener;


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
      startServerListener(); // 서버로부터의 메시지를 수신하는 리스너 시작
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

      JPanel timer = updateTimer();
      second.add(timer, BorderLayout.NORTH);

      JPanel mainQuestionPanel = main_Question_Display();
      second.add(mainQuestionPanel, BorderLayout.CENTER);

      JPanel inputPanel = input_Display();
      second.add(inputPanel, BorderLayout.SOUTH);

      return second;
   }

   
   private JPanel third_Display() {
         JPanel third = new JPanel();
         third.setLayout(new BorderLayout());

         labelAnswerLogo = new JLabel("정답 정보");
         labelAnswerLogo.setFont(new Font("NamunGothic", Font.ITALIC, 30));
         labelAnswerLogo.setHorizontalAlignment(SwingConstants.CENTER);
         labelAnswerLogo.setEnabled(false);

         s_button = new JButton("준비하기");
         s_button.setEnabled(true);
         s_button.setPreferredSize(new Dimension(s_button.getPreferredSize().width, 40));
         s_button.addActionListener(e -> {
            _03GameHost gameFrame = getGameFrame();
            if (gameFrame != null) {
               gameFrame.setUserReady(userCount - 1, true); // 유저가 준비했음을 전달
               showRules = false; // 규칙을 숨김
               rulesTextArea.setText(""); // 규칙 내용을 제거
            }

            timerStarted = true; // 타이머 시작
            // timer.start(); // 타이머 시작
            timer.stop();
         });
         JPanel userAnswerPanel = user_answer_Display();

         third.add(userAnswerPanel, BorderLayout.CENTER);
         third.add(s_button, BorderLayout.SOUTH);
         third.add(labelAnswerLogo, BorderLayout.NORTH);

         return third;
      }
   
   
   public JPanel updateTimer() {
         JPanel p = new JPanel(new BorderLayout());

         remainingTurns = new JLabel("남은 횟수: ");
         remainingTurns.setBounds(30, 100, 200, 50);
         remainingTurns.setFont(new Font("고딕", Font.PLAIN, 20));

         JLabel timerLabel = new JLabel();
         timerLabel.setFont(new Font("Arial", Font.PLAIN, 30));
         timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

         timer = new Timer(1000, e -> {
            if (timerStarted && --count > 0) {
               timerLabel.setText(Integer.toString(count));
            } else {
               timer.stop();
               timerLabel.setText("Next Question");
               // 타이머가 종료될 때 두 번째 힌트를 받는 부분 추가
               String secondHint = JOptionPane.showInputDialog(null, "두 번째 힌트를 입력하세요.");
               if (secondHint != null && !secondHint.isEmpty()) {
                  printHintDisplay("두 번째 힌트: " + secondHint);
               } else {
                  JOptionPane.showMessageDialog(null, "두 번째 힌트를 입력하세요.");
               }

            }

         });

         timerLabel.setText(Integer.toString(count));

         p.add(remainingTurns, BorderLayout.WEST);
         p.add(timerLabel, BorderLayout.EAST);

         return p;
      }
   
   

   // (1)_1 유저 입장정보 출력되는 공간 지정
   private JPanel user_Info_Display() {
      int userNumber = ++userCount; // userCount를 증가시켜 사용자 번호 부여
      JPanel p = new JPanel(new BorderLayout());

      userInfoDisplay = new JTextArea();
      userInfoDisplay.setEditable(false);
      userInfoDisplay.setFont(new Font("Arial", Font.PLAIN, 20));
      // userInfoDisplay.append("User" + userNumber + "\n\n");

      p.add(new JScrollPane(userInfoDisplay), BorderLayout.CENTER);

      return p;
   }


   // (2)_1 질문들이 출력되는 공간 지정
   private JPanel main_Question_Display() {
      JPanel p = new JPanel(new BorderLayout());

      rulesTextArea = new JTextArea();
      rulesTextArea.setEditable(false);
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


   // (2)_2 출제자나 실행자가 질문과 정답을 적는 공간 지정
   private JPanel input_Display() {
	      JPanel p = new JPanel(new BorderLayout());

	      t_input = new JTextField(18);
	      b_send = new JButton("보내기");
	      b_send.setPreferredSize(new Dimension(b_send.getPreferredSize().width, 40));

	      b_send.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	            // String userAnswer = t_userAnswerDisplay.getText(); // 유저가 입력한 답변
	            sendMessage();
	            // receiveMessage();
	            // boolean isCorrect = checkAnswer(userAnswer); // 답변 확인
	            //
	            // if (isCorrect) {
	            // printDisplay("정답입니다!");
	            // } else {
	            // printDisplay("오답입니다.");
	            // }
	         }
	      });

	      t_input.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	            sendMessage();
	            receiveMessage();
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

      p.add(new JScrollPane(t_userAnswerDisplay), BorderLayout.CENTER);

      return p;
   }

   public void setRemainingTurns(String value) {
      selectedCheckbox = value;
      remainingTurns.setText("남은 횟수: " + selectedCheckbox);
   }
   
   private void printHintDisplay(String message) {
      rulesTextArea.append(message + "\n");
      rulesTextArea.setCaretPosition(rulesTextArea.getDocument().getLength());
   }
   
   private void printUserAnswerDisplay(String message) {
	      t_userAnswerDisplay.append(message + "\n");
	      t_userAnswerDisplay.setCaretPosition(t_userAnswerDisplay.getDocument().getLength());

	   }


   private void printUserInfoDisplay(String message) {
      userInfoDisplay.append(message + "\n");
      userInfoDisplay.setCaretPosition(userInfoDisplay.getDocument().getLength());

   }
   
   
   public void setSecretAnswer(String answer) {
         this.secretAnswer = answer;
      }


   public void setUserReady(int userNumber, boolean isReady) {
      if (userNumber >= 0 && userNumber < userReadyList.size()) {
         userReadyList.set(userNumber, isReady);
         checkAllUsersReady();
      }

   }
   
   // (3)_2 실행자가 입력한 단어 출력
   private void processAnswer() {
      if (secretAnswer != null && !secretAnswer.isEmpty()) {
         String userAnswer = t_input.getText(); // 사용자가 입력한 정답
         checkAnswer(userAnswer);
         printHintDisplay("사용자 입력: " + userAnswer); // 사용자 입력을 JTextArea에 출력
      } else {
         printHintDisplay("정답이 설정되지 않았습니다."); // JTextArea에 출력
      }

   }


   public void checkAnswer(String userAnswer) {
      if (userAnswer.equalsIgnoreCase(secretAnswer)) {
         printHintDisplay("정답입니다!"); // 화면에 정답 메시지 출력
      } else {
         printHintDisplay("오답입니다."); // 화면에 오답 메시지 출력
         answers.add(userAnswer); // 오답일 경우 리스트에 추가 (선택사항)
      }

   }


   private void checkAllUsersReady() {
      boolean allReady = !userReadyList.contains(false);
      if (allReady) {
         s_button.setEnabled(true); // 모든 유저가 준비했을 때 시작 버튼 활성화
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
             userInfoDisplay.append("User" + i + "\n\n"); // append로 변경
         }

         receiveThread = new Thread(new Runnable() {
             @Override
             public void run() {
                while (receiveThread == Thread.currentThread()) {
                   receiveMessage();
                }
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
      String message = t_input.getText();
      if (message.isEmpty()) return;

      try {
          ((BufferedWriter) out).write(message + "\n");
          out.flush(); // 버퍼 비우기

         t_userAnswerDisplay.append("나: " + message + "\n");
      } catch (IOException e) {
         System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
         System.exit(-1);
      }

      t_input.setText("");
   }
   
   
   private void sendUserID() {
	      String uid = userInfoDisplay.getText();

	      try {
	         ((BufferedWriter) out).write("/uid:" + uid + "\n");
	         out.flush();
	         printUserInfoDisplay(uid);
	      } catch (IOException e) {
	         System.err.println("클라이언트 읽기 전송 오류>" + e.getMessage());
	         System.exit(-1);
	      }

	      userInfoDisplay.setText("");
	   }
   
   private void receiveMessage() {
	      try {
	         String inMsg = ((BufferedReader) in).readLine();
	         if (inMsg != null) {
	            if (inMsg.startsWith("HINT:")) {
	               String hint = inMsg.substring(5); // "HINT:" 이후의 문자열을 힌트로 가져옵니다.
	               if (!hint.equals(lastHint)) {
	                  printHintDisplay("HINT:" + hint);
	                  lastHint = hint;
	               }

	            } else {
	               printUserAnswerDisplay(inMsg);
	            }

	         }

	      } catch (IOException e) {
	         JOptionPane.showMessageDialog(null, "클라이언트 일반 수신 오류> " + e.getMessage());
	      }

	   }


//   private void receiveMessage() {
//      try {
//         String inMsg = ((BufferedReader) in).readLine();
//         t_userAnswerDisplay.append("서버:\t" + inMsg + "\n");
//      } catch (IOException e) {
//         System.exit(-1);
//      }
//
//   }
   
   
   private void startServerListener() {
       try {
           serverListener = new ServerListener(new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")));
           serverListener.start();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
   
   
   private class ServerListener extends Thread {
       private BufferedReader serverIn;

       public ServerListener(BufferedReader serverIn) {
           this.serverIn = serverIn;
       }

       @Override
       public void run() {
           try {
               String serverMessage;
               while ((serverMessage = serverIn.readLine()) != null) {
                   // 서버로부터 메시지를 받았을 때의 처리
                   handleServerMessage(serverMessage);
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       // 서버로부터 받은 메시지를 처리하는 메서드
       private void handleServerMessage(String message) {
           // 메시지에 따라 처리
           if (message.startsWith("USER_COUNT")) {
               // 서버로부터 전달받은 유저 수 정보를 업데이트
               int newCount = Integer.parseInt(message.split(":")[1]);
               updateConnectedUserCount(newCount);
           } else if (message.startsWith("USER_READY")) {
               // 서버로부터 유저의 준비 상태를 업데이트
               String[] parts = message.split(":");
               int userNumber = Integer.parseInt(parts[1]);
               boolean isReady = Boolean.parseBoolean(parts[2]);
               setUserReady(userNumber, isReady);
           } else {
               // 다른 메시지에 대한 처리
           }
       }

    // 유저의 준비 상태를 업데이트하는 메서드
       private void setUserReady(int userNumber, boolean isReady) {
           SwingUtilities.invokeLater(() -> {
               // 여기에서 UI 업데이트 로직 추가
               // 예: setReadyStatusForUser(userNumber, isReady);
               if (userNumber >= 0 && userNumber < userReadyList.size()) {
                   userReadyList.set(userNumber, isReady);
                   checkAllUsersReady();
               }
           });
       }

       // 연결된 클라이언트 수를 UI에 업데이트하는 메서드
       private void updateConnectedUserCount(int userCount) {
           SwingUtilities.invokeLater(() -> {
               // 여기에서 UI 업데이트 로직 추가

               // 기존 유저 정보를 초기화하고 새로 받은 유저 수만큼 추가
               userInfoDisplay.setText("");
               for (int i = 1; i <= userCount; i++) {
                   userInfoDisplay.append("User" + i + "\n\n");
               }
           });
       }
       
       // 유저의 입장 및 퇴장을 UI에 업데이트하는 메서드 추가
       private void updateUserList(String userAction) {
           SwingUtilities.invokeLater(() -> {
               // 여기에서 UI 업데이트 로직 추가
               userInfoDisplay.append(userAction + "\n");
           });
       }
       
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