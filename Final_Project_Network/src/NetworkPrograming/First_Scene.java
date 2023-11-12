package NetworkPrograming;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//setLocation((int)((dmen.width - getSize().width)/2), 
//(int)((dmen.height - getSize().height))/2);
// 어떤 환경이든 위치를 정중앙에 배치


// 시작화면
public class First_Scene extends JFrame {

	public First_Scene() {
		setLayout(null);

		JLabel title = new JLabel("캐치 워드");
		title.setBounds(390, 200, 300, 100);
		title.setForeground(Color.WHITE);

		Font labelFont = title.getFont();
		title.setFont(new Font(labelFont.getName(), Font.PLAIN, 50));
		add(title);

		ImagePanel backgroundPanel = new ImagePanel("/NetworkPrograming/Pic/ocean.jpg");
		backgroundPanel.setSize(new Dimension(1000, 700));
		add(backgroundPanel);

		JButton s_Button = new JButton("시작하기");
		s_Button.setBounds(440, 550, 100, 35);
		add(s_Button);

		setTitle("캐치 워드");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}


	// Custom JPanel to display an image
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
		new First_Scene();
	}

}
