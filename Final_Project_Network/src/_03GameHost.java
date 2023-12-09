// 게임 출제자 클래스
// 질문을 보내는 사람이며, 정답을 맞출 수 없다.

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;


public class _03GameHost extends JFrame {
   private int port;
   private ServerSocket serverSocket = null;
   private BufferedWriter out;
   private Thread acceptThread = null;
   private Vector<ClientHandler> users = new Vector<ClientHandler>();

   private JLabel labelLogo, labelAnswerLogo, timerLabel, remainingTurns;
   private JTextArea userInfoDisplay, rulesTextArea, t_userAnswerDisplay;
   private JButton b_send, s_button;
   private String selectedCheckbox, secretAnswer, hint;

   private JTextField t_input;
   private JScrollPane scrollPane;
   private Timer timer;

   private List<Boolean> userReadyList = new ArrayList<>(); // 유저들의 준비 여부를 저장하는 리스트 추가
   private ArrayList<String> answers = new ArrayList<>(); // 참가자들의 정답을 저장할 리스트
   private boolean timerStarted = false;
   
   private int remainingTurns1;
   private int count = 10; // 초기 카운트 값
   private int userCount = 0;
   
    private JLabel remainingTurnsLabel;
  


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

      JPanel timer = TimerPanel();
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

      labelAnswerLogo = new JLabel("정답 정보");
      labelAnswerLogo.setFont(new Font("NamunGothic", Font.ITALIC, 30));
      labelAnswerLogo.setHorizontalAlignment(SwingConstants.CENTER);

      s_button = new JButton("시작하기");
      s_button.setPreferredSize(new Dimension(s_button.getPreferredSize().width, 40));

      JPanel userAnswerPanel = user_answer_Display(hint);
      third.add(userAnswerPanel, BorderLayout.CENTER);
      third.add(s_button, BorderLayout.SOUTH);
      third.add(labelAnswerLogo, BorderLayout.NORTH);

      return third;
   }
   
   
   // 타이머 공간 지정
   private JPanel TimerPanel() {
      JPanel p = new JPanel(new BorderLayout());

      remainingTurns = new JLabel("남은 횟수: ");
      remainingTurns.setBounds(30, 100, 200, 50);
      remainingTurns.setFont(new Font("고딕", Font.PLAIN, 20));
      
      remainingTurnsLabel = new JLabel("남은 횟수: " + remainingTurns1); // 변경
      remainingTurnsLabel.setBounds(30, 100, 200, 50);
      remainingTurnsLabel.setFont(new Font("고딕", Font.PLAIN, 20));

      timerLabel = new JLabel();
      timerLabel.setFont(new Font("Arial", Font.PLAIN, 30));
      timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

      p.add(remainingTurns, BorderLayout.WEST);
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
      b_send.addActionListener(e -> {
         t_input.setText("");
      });
      b_send.setPreferredSize(new Dimension(b_send.getPreferredSize().width, 40));

      p.add(t_input, BorderLayout.CENTER);
      p.add(b_send, BorderLayout.EAST);

      return p;
   }
   
   
   // (3)_1 실행자가 적은 정답 단어 출력되는 공간 지정
   private JPanel user_answer_Display(String msg) {
      JPanel p = new JPanel(new BorderLayout());

      t_userAnswerDisplay = new JTextArea();
      t_userAnswerDisplay.setEditable(false);
      t_userAnswerDisplay.append(msg + "\n");

      p.add(new JScrollPane(t_userAnswerDisplay), BorderLayout.CENTER);

      return p;
   }
   
   
   public void setRemainingTurns(String value) {
       selectedCheckbox = value.trim();
       remainingTurns1 = Integer.parseInt(value.trim());
       remainingTurns.setText("남은 횟수: " + remainingTurns1);

       s_button.addActionListener(e -> {
           rulesTextArea.setText("");
           if (!timerStarted && remainingTurns1 > 0) {
               secretAnswer = JOptionPane.showInputDialog(null, "정답을 입력하세요.");

               if (secretAnswer != null && !secretAnswer.isEmpty()) {
                   b_send.setEnabled(true);
                   s_button.setEnabled(false);
                   hint = JOptionPane.showInputDialog(null, "힌트를 입력하세요.");

                   if (hint != null && !hint.isEmpty()) {
                       printHintDisplay("HINT:" + hint);
                       timerStarted = true;
                       remainingTurns1--;

                       remainingTurns.setText("남은 횟수: " + remainingTurns1);

                       if (remainingTurns1 == 0) {
                           JOptionPane.showMessageDialog(null, "더 이상 힌트 입력 기회가 없습니다.");
                           // 남은 횟수가 0이 되면 입력 기회를 중단합니다.
                           s_button.setEnabled(false);
                       }

                       timerLabel.setText("10"); // 타이머 시작 전 초기값 설정
                       timer = new Timer(1000, ev -> {
                           int remainingTime = Integer.parseInt(timerLabel.getText());
                           if (remainingTime > 0) {
                        	   sendHintToClients(hint);
                               timerLabel.setText(Integer.toString(remainingTime - 1));
                           } else {
                               timer.stop();
                               if (remainingTurns1 > 0) {
                                   hint = JOptionPane.showInputDialog(null, "힌트를 입력하세요.");
                                   if (hint != null && !hint.isEmpty()) {
                                       printHintDisplay("HINT:" + hint);
                                       remainingTurns1--;
                                       remainingTurns.setText("남은 횟수: " + remainingTurns1);

                                       timerLabel.setText("10"); // 새로운 힌트를 위해 타이머 초기화
                                       timer.start(); // 타이머 다시 시작
                                   } else {
                                       JOptionPane.showMessageDialog(null, "힌트를 입력하세요.");
                                   }

                               } else {
                                   JOptionPane.showMessageDialog(null, "더 이상 힌트 입력 기회가 없습니다.");
                                   s_button.setEnabled(false);
                               }

                           }
                       });

                       timer.start();
                   } else {
                       JOptionPane.showMessageDialog(null, "힌트를 입력하세요.");
                   }

               } else {
                   JOptionPane.showMessageDialog(null, "정답을 입력하세요.");
               }

           } else {
               JOptionPane.showMessageDialog(null, "이미 정답을 입력했거나 힌트를 모두 사용했습니다.");
           }
       });
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
   
   
   private void printUserInfoDisplay(String message) {
	      userInfoDisplay.append("User" + users + "\n\n");
	      userInfoDisplay.setCaretPosition(userInfoDisplay.getDocument().getLength()); // 화면

	   }


	   private void printUserAnswerDisplay(String message) {
	      t_userAnswerDisplay.append(message + "\n");
	      t_userAnswerDisplay.setCaretPosition(t_userAnswerDisplay.getDocument().getLength()); // 화면
	                                                                                           // 자동
	                                                                                           // 스크롤
	   }
	   
   
   // // (3)_2 실행자가 입력한 단어 출력
   private void processAnswer() {
      if (secretAnswer != null && !secretAnswer.isEmpty()) {
         String participantAnswer = t_input.getText(); // 사용자가 입력한 정답
         checkAnswer(participantAnswer);
         user_answer_Display("사용자 입력: " + participantAnswer); // 사용자 입력을 JTextArea에 출력
      } else {
         user_answer_Display("정답이 설정되지 않았습니다."); // JTextArea에 출력
      }

   }


   public void checkAnswer(String userAnswer) {
      if (userAnswer.equalsIgnoreCase(secretAnswer)) {
         user_answer_Display("정답입니다!"); // 화면에 정답 메시지 출력
      } else {
         user_answer_Display("오답입니다."); // 화면에 오답 메시지 출력
         answers.add(userAnswer); // 오답일 경우 리스트에 추가 (선택사항)
      }

   }
   
   public void setSecretAnswer(String answer) {
      this.secretAnswer = answer;
   }
   
   public void setHint(String hint) {
	      this.hint = hint;
	   }
   

   private void sendMessage(String message) {
      if (message.isEmpty()) return;

      try {
         out.write(message + "\n");
         out.flush();

         rulesTextArea.append("나: " + message + "\n");
      } catch (IOException e) {
         System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
         System.exit(-1);
      }

      rulesTextArea.setText("");
   }



   public void setUserReady(int userNumber, boolean isReady) {
      if (userNumber >= 0 && userNumber < userReadyList.size()) {
         userReadyList.set(userNumber, isReady);
         checkAllUsersReady();
      }

   }


   private void checkAllUsersReady() {
      boolean allReady = !userReadyList.contains(false);
      if (allReady) {
         s_button.setEnabled(true); // 모든 유저가 준비했을 때 시작 버튼 활성화
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

   
   // 새로운 유저가 들어올 때마다 클라이언트에게 유저 목록을 전송하는 메서드
   private void sendUserListToClients() {
       SwingUtilities.invokeLater(() -> {
           synchronized (users) {
               StringBuilder userListMessage = new StringBuilder("USER_LIST:");
               for (int i = 1; i <= userCount; i++) {
                   userListMessage.append("User").append(i);
                   if (i < userCount) {
                       userListMessage.append("\n");
                   }
               }

//               // 모든 클라이언트에게 유저 목록을 전송
//               for (ClientHandler clientHandler : users) {
//                   clientHandler.sendMessage(userListMessage.toString());
//               }
           }
       });
   }
   
   
//   private void sendMessage(String message) {
//       if (message.isEmpty()) return;
//
//       SwingUtilities.invokeLater(() -> {
//           try {
//               out.write(message + "\n");
//               out.flush();
//           } catch (IOException e) {
//               System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
//               System.exit(-1);
//           }
//       });
//   }
   

   // 서버(호스트)와 클라이언트(플레이어)간 연결
   private void startServer() {
       try {
           serverSocket = new ServerSocket(port);
           printHintDisplay("서버가 시작되었습니다.");

           acceptThread = new Thread(() -> {
               while (acceptThread == Thread.currentThread()) {
                   Socket cs = null;
                   try {
                       cs = serverSocket.accept();
                       userCount++;
                       int currentUserNumber = userCount;

                       t_userAnswerDisplay.append("클라이언트가 연결되었습니다.\n");
                       userInfoDisplay.append("User" + currentUserNumber + "\n\n");
                       
                       BufferedWriter out = new BufferedWriter(new OutputStreamWriter(cs.getOutputStream(), "UTF-8"));
                           
                       ClientHandler cHandler = new ClientHandler(cs, out);
                       users.add(cHandler); // 클라이언트 핸들러 목록에 추가
                       cHandler.start();

                       // 새로운 유저가 들어왔을 때 유저 목록을 모든 클라이언트에게 전송
                       sendUserListToClients();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           });

           acceptThread.start();

       } catch (IOException e) {
           e.printStackTrace();
       }
   }


   private class ClientHandler extends Thread {
      private Socket cs;
      private BufferedWriter out;

      public ClientHandler(Socket cs, BufferedWriter out) {
         this.cs = cs;
         this.out = out;
      }

      private void initializeStreams(Socket cs) {
         try {
            BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(cs.getOutputStream(), "UTF-8"));
         } catch (IOException e) {
            printHintDisplay("서버 스트림 초기화 오류> " + e.getMessage());
         }
      }

      private void receiveMessages(Socket cs) {
         try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(cs.getInputStream(), "UTF-8"));

            String message;
            while ((message = in.readLine()) != null) {
               printHintDisplay("User: " + message);
            }

            printHintDisplay("User가 연결을 종료했습니다.");
         } catch (IOException e) {
            printHintDisplay("서버 읽기 오류> " + e.getMessage());
         } finally {
            try {
               cs.close();
            } catch (IOException e) {
               System.err.println("서버 닫기 오류> " + e.getMessage());
               System.exit(-1);
            }

         }

      }


      private void sendMessage() {

          String message = t_input.getText();
          if (message.isEmpty())  return;

          try {
             out.write(message + "\n");
             out.flush();

             rulesTextArea.append("나: " + message + "\n");
          } catch (IOException e) {
             System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
             System.exit(-1);
          }

          t_input.setText("");
       }
      
      
      
      private void sendHint(String hint) {
          try {
             out.write("HINT:" + hint + "\n");
             out.flush();
          } catch (IOException e) {
             System.err.println("클라이언트 힌트 전송 오류: " + e.getMessage());
          }

       }
      
      private void broadcasting(String message) {
          for (ClientHandler c : users) {
             c.sendMessage();
          }
       }


      @Override
      public void run() {
         initializeStreams(cs);
         receiveMessages(cs);
      }
   }


   public static void main(String[] args) {
      int port = 54321;

      SwingUtilities.invokeLater(() -> {
         _03GameHost server = new _03GameHost(port);
         server.startServer(); // 서버 시작 메서드 호출
      });
   }
}