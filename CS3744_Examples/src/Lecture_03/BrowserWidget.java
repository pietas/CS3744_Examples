package Lecture_03;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;


/**
 * Lecture 3: <code>BrowserWidget</class> is an example of a custom widget.
 * 
 * @author Denis Gracanin
 * @version 1
 */
@SuppressWarnings("serial")
public class BrowserWidget extends JComponent implements ActionListener {
	private JTextPane textPane = null;
	private JTextField urlText = null;
	private JScrollPane scrollPane = null;
	private JFrame frame = null;
	private String title;

	/**
	 * Creates an instance of <code>BrowserWidget</code> class.
	 * The frame is null and the title is empty.
	 */
	public BrowserWidget() {
		this(null, "");
	}
	
	/**
	 * Creates an instance of <code>BrowserWidget</code> class.
	 * The frame is null and the title is empty.
	 *
	 * @param f The application window.
	 * @param t The default application window title.
	 */
	public BrowserWidget(JFrame f, String t) {
		textPane = new JTextPane();
		textPane.setEditable(false);
		scrollPane = new JScrollPane(textPane);
		urlText = new JTextField();
		urlText.addActionListener(this);
		setFrame(f, t);
		setLayout(new BorderLayout());
		add(urlText, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Sets the text in the URL field.
	 * 
	 * @param s The URL value.
	 */
	public void setText(String s) {
		urlText.setText(s);
	}
	
	/**
	 * Gets the text in the URL field.
	 * 
	 * @return The URL text.
	 */
	public String getText() {
		return urlText.getText();
	}

	/**
	 * The action event handler updates the displayed URL.
	 * 
	 * @param e The generated event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		setURL(urlText.getText());
	}
	
	/**
	 * Sets the frame and the default title.
	 *
	 * @param f The application window.
	 * @param t The default application window title.
	 */
	public void setFrame(JFrame f, String t) {
		frame = f;
		title =  t;
	}

	/**
	 * A utility method to display the URL.
	 * 
	 * @param url The URL value.
	 */
	private void setURL(String url) {
		try {
			textPane.setPage(url);
			textPane.setBackground(Color.WHITE);
			frame.setTitle(title + ": " + url);
		}
		catch (IOException e) {
			textPane.setEditable(true);
			textPane.selectAll();
			textPane.cut();
			textPane.setEditable(false);
			textPane.setBackground(Color.RED);
			frame.setTitle(title);
		}
	}

}