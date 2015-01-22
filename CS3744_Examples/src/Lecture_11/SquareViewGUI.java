package Lecture_11;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Lecture 12: A GUI view (Swing based) of the square data that supports interactive user input.
 * 
 * @author Denis Gracanin
 * @version 1
 */
public class SquareViewGUI extends JComponent {
	private static final long serialVersionUID = 1L;
	private ArrayList<ChangeListener> listeners = null;
	private JSlider squareScale = null;
	private JColorChooser squareColor = null;
	private JButton resetButton = null;
	private int defaultScale = 0;
	private Color defaultColor = null;
	private Color oldColor = null;
	
	/**
	 * Creates an instance of <code>SquareViewGUI</code> with the specified values for scale and color.
	 * 
	 * @param s the default scale value
	 * @param c the default color value
	 */
	public SquareViewGUI(int s, Color c) {
		super();
		defaultScale = s;
		defaultColor = c;
		listeners = new ArrayList<ChangeListener>();
		setLayout(new BorderLayout());
		JPanel scalePanel = new JPanel(new GridLayout(2,1));
		scalePanel.add(new JLabel("Scale (%)", JLabel.CENTER));
		squareScale = new JSlider(JSlider.HORIZONTAL, 0, 100, s);
		squareScale.addChangeListener(new ChangeListener() {

			/**
			 * Fires the <code>SquareViewGUI</code>'s change event when scale changes.
			 *
			 * @param e the change event
			 */
			public void stateChanged(ChangeEvent e) {
				fireStateChanged();
			}
			
		});
		squareScale.setMajorTickSpacing(10);
		squareScale.setMinorTickSpacing(5);
		squareScale.setPaintTicks(true);
		squareScale.setPaintLabels(true);
		scalePanel.add(squareScale);
		add(scalePanel, BorderLayout.PAGE_START);
		JPanel colorPanel = new JPanel(new BorderLayout());
		colorPanel.add(new JLabel("Color", JLabel.CENTER), BorderLayout.PAGE_START);
		squareColor = new JColorChooser();
		squareColor.getSelectionModel().addChangeListener(new ChangeListener() {

			/**
			 * Fires the <code>SquareViewGUI</code>'s change event when color changes.
			 *
			 * @param e the change event
			 */
			@Override
			public void stateChanged(ChangeEvent e) {
				fireStateChanged();
			}
			
		});
		squareColor.getSelectionModel().setSelectedColor(c);
		oldColor = c;
		colorPanel.add(squareColor, BorderLayout.CENTER);		
		add(colorPanel, BorderLayout.CENTER);
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			/**
			 * Sets the default values for scale and color.
			 *
			 * @param e the mouse event
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				squareScale.setValue((int) (defaultScale));
				squareColor.getSelectionModel().setSelectedColor(defaultColor);				
			}
		});
		add(resetButton, BorderLayout.PAGE_END);
	}
	
	/**
	 * Returns the color.
	 * 
	 * @return the color
	 */
	public Color getColor() {
		return squareColor.getSelectionModel().getSelectedColor();
	}
	
	/**
	 * Sets the color.
	 * 
	 * @param c the new color value.
	 */
	public void setColor(Color c) {
		if (!c.equals(oldColor)) {
			squareColor.getSelectionModel().setSelectedColor(c);
			oldColor = c;
			fireStateChanged();
		}
	}
	
	/**
	 * Returns the scale.
	 * 
	 * @return the scale
	 */
	public int getScale() {
		return squareScale.getValue();
	}
	
	/**
	 * Sets the scale.
	 * 
	 * @param s the new slide value.
	 */
	public void setScale(int s) {
		if (s != squareScale.getValue()) {
			squareScale.setValue(s);
			fireStateChanged();
		}
	}
	
	/**
	 * Updates the GUI with the specified scale and color.
	 * 
	 * @param s the scale to set
	 * @param c the color to set
	 */
	public void draw(int s, Color c) {
		setScale(s);
		setColor(c);
	}

	/**
	 * Adds a change listener.
	 * 
	 * @param l change listener
	 */
	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}

	/**
	 * Removes a change listener.
	 * 
	 * @param l change listener
	 */
	public void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}

	/**
	 * Fires a change event.
	 */
	protected void fireStateChanged() {
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener l : listeners) {
			l.stateChanged(e);
		}
	}

}