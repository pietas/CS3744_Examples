package Lecture_02;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.util.Date;

/**
 * Lecture 2: A simple Java Swing example that shows the current time and date.
 *
 * @author Denis Gracanin
 * @version 1
 */
@SuppressWarnings("serial")
public class TimeDisplay extends JFrame {

	/**
	 * Creates an instance of <code>TimeDisplay</code> class.
	 * 
	 * @param title The title of the application window.
	 */
	public TimeDisplay(String title) {
		super(title);
		setLayout(new GridLayout(2,1));
		add(new JLabel("Current Time", JLabel.CENTER));
		add(new JLabel((new Date()).toString(), JLabel.CENTER));
	}

	/**
	 * The main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		TimeDisplay td = new TimeDisplay("Lecture 2: Time Display");
		td.setSize(300, 100);
		td.setVisible(true);
	}

}
