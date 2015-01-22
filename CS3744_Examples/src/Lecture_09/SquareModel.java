package Lecture_09;
import java.awt.Color;

/**
 * Lecture 8: A simple model to store square data: scale and color.
 *  
 * @author Denis Gracanin
 * @version 1
 */
public class SquareModel {
	private double scale = DEFAULT_SCALE;
	private Color color = DEFAULT_COLOR;

	/**
	 * The default scale value.
	 */
	public static double DEFAULT_SCALE = 0.5;
	/**
	 * The default color value.
	 */
	public static Color DEFAULT_COLOR = Color.BLUE;

	/**
	 * Creates an instance of <code>SquareModel</code> with default values for scale and color.
	 */
	public SquareModel() {
		this(DEFAULT_SCALE, DEFAULT_COLOR);
	}

	/**
	 * Creates an instance of <code>SquareModel</code> with the specified values for scale and color.
	 *
	 * @param s the scale to set
	 * @param c the color to set
	 */
	public SquareModel(double s, Color c) {
		setScale(s);
		setColor(c);
	}

	/**
	 * Returns the scale.
	 * 
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * Sets the scale. The value is clamped within [0,1] interval.
	 * 
	 * @param scale the scale to set
	 */
	public void setScale(double s) {
		scale = (s < 0 ? 0 : (s > 1 ? 1 : s));
	}

	/**
	 * Returns the color.
	 * 
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color.
	 * 
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Resets to the default values for scale and color.
	 */
	public void reset() {
		setScale(DEFAULT_SCALE);
		setColor(DEFAULT_COLOR);
	}

}