package Lecture_11;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Lecture 11: A simple application that displays a square and sets scale and color.
 * Transparency is not supported.
 *  
 * @author Denis Gracanin
 * @version 2
 */
public class ScaledSquare extends JFrame implements ChangeListener {
	private static final long serialVersionUID = 2L;
	private final static String TITLE = "Scale Square";
	private SquareViewGUI viewGUI = null;
	private SquareView view = null;
	private SquareModel model = null;

	/**
	 * Creates an instance of <code>SquareController</code>.
	 */
	public ScaledSquare() {
		super(TITLE);
		model = new SquareModel();
		model.addChangeListener(this);
		setLayout(new GridLayout(1,2));
		viewGUI = new SquareViewGUI((int) (model.getScale() * 100), model.getColor());
		viewGUI.addChangeListener(this);
		add(viewGUI);
		view = new SquareView(model.getScale(), model.getColor());
		view.setBackground(Color.YELLOW);
		view.addChangeListener(this);
		view.setFocusable(true);
		add(view);
	}

	/**
	 * The main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		ScaledSquare frame = new ScaledSquare();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 400);
		frame.setVisible(true);
	}

	/**
	 * Processes a state changed event by setting:
	 * - the scale value based on the slider value or on the view value
	 * - the color value based on the color chooser value
	 * and updating the graphic view.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == view) {
			if (view.getReset()) {
				model.reset();
			}
			else {
				model.setScale(view.getValue());
			}
		}
		else if (source == viewGUI) {
			model.setScale(viewGUI.getScale() / 100.0);
			model.setColor(viewGUI.getColor());
		}
		else if (source == model) {
			view.draw(model.getScale(), model.getColor());
			viewGUI.draw((int) (model.getScale() * 100), model.getColor());
		}
	}

}