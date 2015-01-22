package Lecture_10;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;


/**
 * Lecture 10: A custom table model that contains a transformation matrix.
 * 
 * @author Denis Gracanin
 * @version 1
 */
public class TransformModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	private static final int SIZE = 4;
	private float rotation = 0.0f;
	private float scaleX = 1.0f;
	private float scaleY = 1.0f;
	private float translationX = 0.0f;
	private float translationY = 0.0f;

	/**
	 * Creates an instance of <code>TransformModel</code> with the identity matrix set.
	 */
	public TransformModel() {
		this.setNumRows(SIZE);
		this.setColumnCount(SIZE);
		setIdentity();
	}

	/**
	 * Overriden to check if the new value is a number.
	 * 
	 * @param value New cell value.
	 * @param row Row index.
	 * @param column Column index.
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		try {
			float f = Float.parseFloat((String) value);
			super.setValueAt(value, row, col);
		}
		catch (NumberFormatException e) {
		}	
	}

	/**
	 * Sets the transformation to an identity matrix.
	 */
	public void setIdentity() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				setValueAt((i == j ? "1" : "0"), i, j);
			}
		}
	}

	/**
	 * Get the transformation matrix as an array of floats (row by row).
	 * 
	 * @return Transformation matrix.
	 */
	public float[] getTransform() {
		float buffer[] = new float[SIZE * SIZE];
		int count = 0;
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				buffer[count++] = Float.parseFloat((String) getValueAt(i, j));
			}
		}		
		return buffer;
	}

	/**
	 * Set the rotation value.
	 * .
	 * @param s Rotation.
	 */
	public void setRotation(float r) {
		rotation = r;
		update();
	}

	/**
	 * Set the scale in X value.
	 * .
	 * @param s Scale in X.
	 */
	public void setScaleX(float s) {
		scaleX = s;
		update();
	}
	
	/**
	 * Set the scale in Y value.
	 * .
	 * @param s Scale in Y.
	 */
	public void setScaleY(float s) {
		scaleY = s;
		update();
	}

	/**
	 * Set the translation in X value.
	 * .
	 * @param t Translation in X.
	 */
	public void setTranslationX(float t) {
		translationX = t;
		update();
	}

	/**
	 * Set the translation in Y value.
	 * .
	 * @param t Translation in Y.
	 */
	public void setTranslationY(float t) {
		translationY = t;
		update();
	}

	/**
	 * Set the transformation to the identity.
	 */
	private void update() {
		setIdentity();
		double angle = rotation * Math.PI / 180;
		setValueAt(Double.toString(Math.cos(angle) * scaleX), 0, 0);
		setValueAt(Double.toString(- Math.sin(angle) * scaleY), 0, 1);
		setValueAt(Double.toString(translationX), 0, 3);
		setValueAt(Double.toString(Math.sin(angle) * scaleX), 1, 0);
		setValueAt(Double.toString(Math.cos(angle) * scaleY), 1, 1);
		setValueAt(Double.toString(translationY), 1, 3);
	}
}