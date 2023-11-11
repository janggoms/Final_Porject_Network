package NetworkPrograming;


import java.io.Serializable;

import javax.swing.JFrame;


public class Common_Size implements Serializable {
	public class b_size extends JFrame {
		void b_size() {
			setSize(1000, 700);
			setLocationRelativeTo(null);

			setExtendedState(JFrame.MAXIMIZED_BOTH);

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
		}

	}
}
