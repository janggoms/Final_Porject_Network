package NetworkPrograming;


import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class First_Scene extends JFrame {
	ImageIcon image;
	JScrollPane s_pane;


	public First_Scene() {

		buildGUI();

	}


	private void buildGUI() {

	}


	private JPanel createDisplayPanel() {
		return null;

	}


//	private JPanel createInputPanel() {
//		return null;
//
//	}


	public static void main(String[] args) {
		new First_Scene();
		b_size size = new b_size();
		size.b_size();
	}

}
