package Lecture_03;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Lecture 3: A simple Java Swing example that shows how to use menu and editor pane
 * to create a simple browser using a custom browser widget.
 * 
 * @author Denis Gracanin
 * @version 1
 */
@SuppressWarnings("serial")
public class SimpleBrowserWidget extends JFrame implements ActionListener
{ 
	private final static String TITLE = "Simple Browser Widget";
	private JMenuBar menuBar = null;
	private JMenu fileMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem openMenuItem = null;
	private JMenuItem quitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private BrowserWidget browserWidget = null;

	/**
	 * Creates an instance of <code>SimpleBrowser</code> class.
	 * The default title is used.
	 */
	public SimpleBrowserWidget() {
		this(TITLE);
	}

	/**
	 * Creates an instance of <code>SimpleBrowser</code> class.
	 * 
	 * @param title The title of the application window.
	 */
	public SimpleBrowserWidget(String title) {
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
		aboutMenuItem = new JMenuItem("About Simple Browser Widget");
		aboutMenuItem.addActionListener(this);
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
		
		browserWidget = new BrowserWidget();
		browserWidget.setFrame(this, TITLE);
		setLayout(new BorderLayout());
		add(browserWidget, BorderLayout.CENTER);
	}

	/**
	 * The main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
        final SimpleBrowserWidget editor = new SimpleBrowserWidget();
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
			browserWidget.setText((String) JOptionPane.showInputDialog(this, "Enter the URL:"));
		}
		else if (source == quitMenuItem) {
			System.exit(0);
		}
		else if (source == aboutMenuItem) {
			JOptionPane.showMessageDialog(this, "Simple Browser Widget version 1.");
		}
		else if (source == browserWidget) {

		}
	}
	
}