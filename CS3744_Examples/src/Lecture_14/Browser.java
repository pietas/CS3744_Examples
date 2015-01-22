package Lecture_14;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Lecture 14: <code>Browser</class> is an example of using concurrency in a custom Swing widget.
 * 
 * @author Denis Gracanin
 * @version 1
 */
public class Browser extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static String TITLE = "Browser: Thread Demo";
	private JTextPane htmlText = null;
	private JTextField urlText = null;
	private JScrollPane scrollPane = null;
	private JButton goButton = null;
	private JButton cancelButton = null;
	private BrowseTask browseTask = null;

	/**
	 * Creates an instance of <code>Browser</code> class.
	 * The default title is provided.
	 */
	public Browser() {
		this(TITLE);
	}

	/**
	 * Creates an instance of <code>Browser</code> class, sets the size and shows it.
	 *
	 * @param t The application window title.
	 */
	public Browser(String t) {
		super(t);
		addWindowListener(
				/**
				 * Custom window adapter class that terminates the running task when the window is closed.
				 *  
				 * @author Denis Gracanin
				 * @version 1
				 */
				new WindowAdapter() {
					/**
					 * Handles the running thread when the application window is closed.
					 * 
					 * @param e Window event.
					 */
					public void windowClosing(WindowEvent windowevent) {
						if (browseTask != null) {
							browseTask.cancel(true);
							browseTask = null;
						}
						dispose();
						System.exit(0);
					}
				});
		htmlText = new JTextPane();
		htmlText.setEditable(false);
		scrollPane = new JScrollPane(htmlText);

		urlText = new JTextField();
		urlText.setActionCommand("G");
		urlText.addActionListener(this);
		urlText.setToolTipText("Type in the URL text.");

		goButton = new JButton("Go");
		goButton.setMnemonic(KeyEvent.VK_G);
		goButton.setActionCommand("G");
		goButton.addActionListener(this);
		goButton.setToolTipText("Click this button to load the URL.");

		cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.setEnabled(false);
		cancelButton.setActionCommand("C");
		cancelButton.addActionListener(this);
		cancelButton.setToolTipText("Click this button to cancel loading the URL.");

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(goButton);
		buttonPanel.add(cancelButton);

		setLayout(new BorderLayout());
		add(urlText, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		setSize(600, 500);
		setVisible(true);
	}

	/**
	 * The action event handler updates the displayed URL.
	 * 
	 * @param e The generated event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("G")) {
			goButton.setEnabled(false);
			urlText.setEnabled(false);
			cancelButton.setEnabled(true);
			htmlText.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			(browseTask = new BrowseTask()).execute();
		}
		else if (e.getActionCommand().equals("C")) {
			goButton.setEnabled(true);
			urlText.setEnabled(true);
			cancelButton.setEnabled(false);
			browseTask.cancel(true);
			htmlText.setCursor(null);
			browseTask = null;
		}
	}

	/**
	 * The main method.
	 * Creates the frame (application window) object.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Browser();
			}
		});

	}

	/**
	 * Custom <code>SwingWorker</code> class used to read the content of the URL.
	 * 
	 * @author Denis Gracanin
	 * @version 1
	 */
	private class BrowseTask extends SwingWorker<String, Void> {
		/**
		 * Open the URL and read the content line by line.
		 * 
		 * @return URL content.
		 */
		@Override
		public String doInBackground() {
			URL url;
			try {
				url = new URL(urlText.getText());
			} catch (MalformedURLException e) {
				return "Malformed URL.";
			}
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String inputLine = null;
				String text = "";
				while ((inputLine = in.readLine()) != null) {
					text = text + "\n" + inputLine;
				}
				in.close();
				return text;
			} catch (IOException e) {
				return "I/O Exception.";
			}
		}

		/**
		 * Update the data when thread completes.
		 */
		@Override
		public void done() {
			try {
				goButton.setEnabled(true);
				urlText.setEnabled(true);
				cancelButton.setEnabled(false);
				htmlText.setCursor(null);
				htmlText.setText(get());
			}
			catch (CancellationException e) {
				htmlText.setText(e.toString());
			}
			catch (ExecutionException e) {
				htmlText.setText(e.toString());
			}
			catch (InterruptedException e) {
				htmlText.setText(e.toString());
			}
		}
	}

}