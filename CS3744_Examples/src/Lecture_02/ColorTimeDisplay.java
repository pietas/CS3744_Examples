package Lecture_02;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.util.Date;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.LineBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lecture 2: A simple Java Swing example that shows the current time and date.
 * The current time and date is updated when a user presses the button.
 * Non-default colors, border and fonts are used to illustrate Swing component customization.
 * 
 * @author Denis Gracanin
 * @version 1
 */
@SuppressWarnings("serial")
public class ColorTimeDisplay extends JFrame implements ActionListener {
private JButton button = null;
private JLabel time = null;
	/**
	 * Creates an instance of <code>ColorTimeDisplay</code> class.
	 * 
	 * @param title The title of the application window.
	 */
	public ColorTimeDisplay(String title) {
		super(title);
		setLayout(new GridLayout(2, 1));
		button = new JButton("Current Time");
		button.addActionListener(this);
		add(button);
		time = new JLabel((new Date()).toString(), JLabel.CENTER);
		time.setForeground(Color.RED);
		time.setOpaque(true);
		time.setBackground(Color.WHITE);
		time.setBorder(new LineBorder(Color.BLUE, 10));
		time.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
		add(time);
	}

	/**
	 * The main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		ColorTimeDisplay td = new ColorTimeDisplay("Lecture 2: Color Time Display");
		td.setSize(500, 200);
		td.setVisible(true);
	}

	/**
	 * Sets the current date when the button is pressed.
	 * 
	 * @param e Action event object.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		time.setText((new Date()).toString());
	}

}

