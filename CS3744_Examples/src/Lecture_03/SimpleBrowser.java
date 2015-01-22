package Lecture_03;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;


/**
 * Lecture 3: A simple Java Swing example that shows how to use menu and editor pane
 * to create a simple browser.
 * 
 * @author Denis Gracanin
 * @version 1
 */
@SuppressWarnings("serial")
public class SimpleBrowser extends JFrame implements ActionListener
{ 
	private final static String TITLE = "Simple Browser";
	private JMenuBar menuBar = null;
	private JMenu fileMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem openMenuItem = null;
	private JMenuItem quitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JScrollPane scrollPane = null;
	private JTextPane textPane = null;
	private JTextField urlText = null;

	/**
	 * Creates an instance of <code>SimpleBrowser</code> class.
	 * The default title is used.
	 */
	public SimpleBrowser() {
		this(TITLE);
	}

	/**
	 * Creates an instance of <code>SimpleBrowser</code> class.
	 * 
	 * @param title The title of the application window.
	 */
	public SimpleBrowser(String title) {
		super(title);
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		
		openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(this);
		fileMenu.add(openMenuItem);

		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(this);
		fileMenu.add(quitMenuItem);

		menuBar.add(fileMenu);

		helpMenu = new JMenu("Help");
		aboutMenuItem = new JMenuItem("About Simple Browser");
		aboutMenuItem.addActionListener(this);
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		scrollPane = new JScrollPane(textPane);
		urlText = new JTextField();
		urlText.addActionListener(this);
		setLayout(new BorderLayout());
		add(urlText, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * The main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
        final SimpleBrowser editor = new SimpleBrowser();
        editor.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                editor.dispose();
                System.exit(0);
            }
        });

        editor.setSize(500, 500);
        editor.setVisible(true);
	}

	/**
	 * The action event handler that determines the source of the event
	 * and processes the event accordingly.
	 * 
	 * @param e The generated event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == openMenuItem) {
			urlText.setText((String) JOptionPane.showInputDialog(this, "Enter the URL:"));
			setURL(urlText.getText());
		}
		else if (source == quitMenuItem) {
			System.exit(0);
		}
		else if (source == aboutMenuItem) {
			JOptionPane.showMessageDialog(this, "Simple Browser version 1.");
		}
		else if (source == urlText) {
			setURL(urlText.getText());
		}
	}
	
	/**
	 * A utility method to display the URL.
	 * 
	 * @param url
	 */
	private void setURL(String url) {
			try {
				textPane.setPage(url);
				textPane.setBackground(Color.WHITE);
				setTitle(TITLE + ": " + url);
			}
			catch (IOException e) {
				textPane.selectAll();
				textPane.cut();
				setTitle(TITLE);
				textPane.setBackground(Color.RED);
			}
	}
}