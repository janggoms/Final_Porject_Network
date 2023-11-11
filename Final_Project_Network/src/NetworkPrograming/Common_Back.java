package NetworkPrograming;


import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class Common_Back implements Serializable {
	ImageIcon image;
	JScrollPane s_pane;


	public class back extends JFrame {
		void back() {
			// image = new ImageIcon("c:");

			JPanel background = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					g.drawImage(image.getImage(), 0, 0, null);
					setOpaque(false);
					super.paintComponent(g);
				}

			};

		}

	}

}
