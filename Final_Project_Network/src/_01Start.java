// 첫 게임 시작화면을 나타냄
// 시작하기 버튼을 누르면 _02Room 으로 넘어가 방생성 화면으로 넘어감

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
public class _01Start extends JFrame {

	public _01Start() {
		setLayout(null);
		
		ImagePanel backgroundPanel = new ImagePanel("/ImageFile/sky.png");
		backgroundPanel.setSize(new Dimension(1000, 700));
		add(backgroundPanel);

        setupButton();

		setTitle("캐치 워드");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
    private void setupButton() {
        JButton s_Button = new JButton("시작하기");
        s_Button.setBounds(400, 425, 150, 75);
        s_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				_02Room S_Frame = new _02Room();
				S_Frame.setVisible(true);
				dispose(); // 현재 프레임 닫고 02Room으로 넘어감
            }
        });
        s_Button.setForeground(new Color(255, 255, 255));
        s_Button.setBackground(new Color(30, 144, 255));
        add(s_Button);
    }

	// 배경이미지 표시
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
		new _01Start();
	}
}
