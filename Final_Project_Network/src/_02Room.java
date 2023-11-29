// 방생성 화면으로 생성하기 버튼을 눌르면 호스트 화면으로, 방 목록에서 버튼을 누르면 플레이어 화면으로 넘어감

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


public class _02Room extends JFrame {

	public String remainingTurns;

	private JButton c_room, e_room; // 방생성, 방입장
	private JLabel label; // 표시
	private JTextField roomName; // 방생성 입력란
	private String[] names = { " 5", " 7", "10", "15", "20" };

	private List<JButton> roomButtons = new ArrayList<>(); // 생성된 방 버튼을 관리


	public _02Room() {
		setTitle("네프 방생성 화면 구성");
		setSize(1000, 700);

		setLocationRelativeTo(null);
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(null); // 레이아웃 관리자를 사용하지 않음

		buildGUI();
		setVisible(true);
	}


	private void buildGUI() {
		JPanel first = firstDisplay();
		first.setBorder(new LineBorder(Color.black));
		add(first);

		JPanel second = secondDisplay();
		second.setBorder(new LineBorder(Color.BLACK));
		add(second);
	}


	private JPanel firstDisplay() {
		JPanel p = new JPanel();
		p.setBounds(0, 0, 700, 700); // 첫 번째 패널 위치 및 크기 설정
		p.setLayout(null);

		label = new JLabel("현재 생성된 방");
		label.setBounds(100, 10, 200, 100);
		label.setFont(new Font("굴림", Font.BOLD, 25));
		p.add(label);

		return p;
	}


	private JPanel secondDisplay() {
		JPanel p = new JPanel();
		p.setBounds(700, 0, 300, 700);
		p.setLayout(null);

		label = new JLabel("방생성하기");
		label.setBounds(30, 10, 200, 100);
		label.setFont(new Font("굴림", Font.BOLD, 25));
		p.add(label);

		label = new JLabel("방이름");
		label.setBounds(35, 100, 100, 50);
		label.setFont(new Font("고딕", Font.PLAIN, 20));
		p.add(label);

		roomName = new JTextField(30);
		roomName.setBounds(35, 150, 200, 30);
		p.add(roomName);

		label = new JLabel("고개횟수");
		label.setBounds(35, 250, 300, 50);
		label.setFont(new Font("고딕", Font.PLAIN, 20));
		p.add(label);

		JCheckBox[] g_turnsCheckBoxes = new JCheckBox[5];
		ButtonGroup buttonGroup = new ButtonGroup();

		for (int i = 0; i < g_turnsCheckBoxes.length; i++) {
			g_turnsCheckBoxes[i] = new JCheckBox(names[i]);
			g_turnsCheckBoxes[i].setBounds(5 + i * 55, 300, 55, 60);
			g_turnsCheckBoxes[i].setFont(new Font("맑은고딕", Font.PLAIN, 20));
			g_turnsCheckBoxes[i].setBorderPainted(true);
			buttonGroup.add(g_turnsCheckBoxes[i]);
			p.add(g_turnsCheckBoxes[i]);
		}

		c_room = new JButton("생성");
		c_room.setFont(new Font("고딕", Font.BOLD, 20));
		c_room.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createRoomButton();

				int port = 54321;
				_03GameHost gameHost = new _03GameHost(port);

				for (int i = 0; i < g_turnsCheckBoxes.length; i++) {
					if (g_turnsCheckBoxes[i].isSelected()) {
						remainingTurns = names[i];
						break;
					}

				}

				gameHost.setRemainingTurns(remainingTurns);
				gameHost.setVisible(true);
				// dispose();
			}
		});

		c_room.setBounds(0, 600, 300, 60);
		p.add(c_room);

		return p;
	}


	// 방생성 버튼을 누르면 화면에 해당 방을 입장할 수 있는 버튼이 나타남
	private void createRoomButton() {
		String roomNameText = roomName.getText().trim(); // 공백제거
		if (!roomNameText.isEmpty()) { // 방이름이 비어있으면 생성X
			JButton newRoomButton = new JButton(roomNameText);
			roomButtons.add(newRoomButton);
			refreshRoomButtons();
		}

	}


	// 방 목록 업데이트. 생성한 방이 고정된 좌표값에 대입됨
	private void refreshRoomButtons() {
		JPanel firstPanel = (JPanel) getContentPane().getComponent(0);
		firstPanel.removeAll();

		label = new JLabel("현재 생성된 방");
		label.setBounds(100, 10, 200, 100);
		label.setFont(new Font("굴림", Font.BOLD, 25));
		firstPanel.add(label);

		int y = 100;
		for (JButton roomButton : roomButtons) {
			roomButton.setBounds(100, y, 500, 60);
			y += 70;
			firstPanel.add(roomButton);

			roomButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					int port = 54321;
					String serverAddress = "localhost";
					_03GamePlayer gamePlayer = new _03GamePlayer(serverAddress, port);

					gamePlayer.setRemainingTurns(remainingTurns);
					gamePlayer.setVisible(true);
					// dispose();
				}
			});
		}

		revalidate();
		repaint();
	}


	public static void main(String[] args) {
		new _02Room();
	}
}