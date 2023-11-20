package NetworkPrograming;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//setLocation((int)((dmen.width - getSize().width)/2), 
//(int)((dmen.height - getSize().height))/2);
// 어떤 환경이든 위치를 정중앙에 배치


// 시작화면
public class StartUI extends JFrame {

	public StartUI() {
		setLayout(null);

		JLabel title = new JLabel("캐치워드");
		title.setBounds(330, 175, 300, 100);
		title.setBackground(new Color(0, 0, 0));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setForeground(Color.WHITE);

		Font buttonFont = title.getFont();
		title.setFont(new Font(buttonFont.getName(), Font.BOLD, 20));

		Font labelFont = title.getFont();
		title.setFont(new Font(labelFont.getName(), Font.BOLD, 65));
		add(title);

		// 추후에 다시 만들 예정
		ImagePanel backgroundPanel = new ImagePanel("/NetworkPrograming/Pic/CW_logo.png");
		backgroundPanel.setSize(new Dimension(1000, 700));
		add(backgroundPanel);

		JButton s_Button = new JButton("시작하기");
		s_Button.setBounds(400, 425, 150, 75);
		s_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateRoomUI S_Frame = new CreateRoomUI();
				S_Frame.setVisible(true);
				dispose(); // 현재의 프레임을 닫습니다.
			}
		});
		s_Button.setForeground(new Color(255, 255, 255));
		s_Button.setBackground(new Color(30, 144, 255));
		add(s_Button);

		setTitle("캐치 워드");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
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


	public static void main(String[] args) {
		new StartUI();
	}

}
