package Player;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


// 시작화면
public class _01Start_P extends JFrame {

	public _01Start_P() {
		setLayout(null);
		ImagePanel backgroundPanel = new ImagePanel("/ImageFile/sky.png");
		backgroundPanel.setSize(new Dimension(1000, 700));
		add(backgroundPanel);

		JButton s_Button = new JButton("시작하기");
		s_Button.setBounds(400, 425, 150, 75);
		s_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_02Room_P S_Frame = new _02Room_P();
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
		new _01Start_P();
	}

}
