package NetworkPrograming;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


public class CreateRoomUI extends JFrame {
	
    public String remainingTurns;
	
	private JButton c_room; // 방생성
	private JButton e_room; // 방입장
	private JLabel label; // 표시
	private JTextField roomName; // 방생성 입력란
	
	private String [] names = {" 5", " 7", "10", "15", "20"};
	

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

	
	private JPanel secondDisplay() {
		
		JPanel p = new JPanel();
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
		roomName.setBounds(35, 150, 250, 30);
		p.add(roomName);

		label = new JLabel("고개횟수");
		label.setBounds(35, 250, 300, 50);
        label.setFont(new Font("고딕", Font.PLAIN, 20));
		p.add(label);
		
	    JCheckBox[] g_turnsCheckBoxes = new JCheckBox[5];
	    ButtonGroup buttonGroup = new ButtonGroup();
	    
        for (int i = 0; i < g_turnsCheckBoxes.length; i++) {
            g_turnsCheckBoxes[i] = new JCheckBox(names[i]);
            g_turnsCheckBoxes[i].setBounds(20 + i * 55, 300, 55, 60);
            g_turnsCheckBoxes[i].setFont(new Font("맑은고딕", Font.PLAIN, 20));
            g_turnsCheckBoxes[i].setBorderPainted(true); // 체크박스에 테두리
            buttonGroup.add(g_turnsCheckBoxes[i]); // 체크박스 1개만 선택 가능
            p.add(g_turnsCheckBoxes[i]);
            
        }
        
		c_room = new JButton("생성");
		c_room.setFont(new Font("고딕", Font.BOLD, 20));
	    c_room.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            GameHost secondFrame = new GameHost();

	            // 체크박스를 반복하여 선택된 것을 찾습니다.
	            for (int i = 0; i < g_turnsCheckBoxes.length; i++) {
	                if (g_turnsCheckBoxes[i].isSelected()) {
	                    remainingTurns = names[i];
	                    secondFrame.setRemainingTurns(remainingTurns);
	                    break;
	                }
	            }

              secondFrame.setVisible(true);
	            dispose();
	        }
	    });

		c_room.setBounds(0, 600, 328, 60);
		p.add(c_room);

		return p;
	}

	public static void main(String[] args) {
		new CreateRoomUI();
	}
}
