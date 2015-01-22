package Lecture_07;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;


/**
 * Lecture 7: A custom table model that contains an orthographic projection matrix.
 * 
 * @author Denis Gracanin
 * @version 1
 */
public class OrthoModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	private static final int SIZE = 4;
	private float left = -1.0f;
	private float right = 1.0f;
	private float bottom = -1.0f;
	private float top = 1.0f;
	private float near = 1.0f;
	private float far = -1.0f;

	/**
	 * Creates an instance of <code>TransformModel</code> with the identity matrix set.
	 */
	public OrthoModel() {
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
			Float.parseFloat((String) value);
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
	 * Get the left border value.
	 * 
	 * @return Left border.
	 */
	public float getLeft() {
		return left;
	}
	
	/**
	 * Set the left border value.
	 * .
	 * @param l Left border.
	 */
	public void setLeft(float l) {
		left = l;
		update();
	}

	/**
	 * Get the right border value.
	 * 
	 * @return Right border.
	 */
	public float getRight() {
		return right;
	}

	/**
	 * Set the right border value.
	 * .
	 * @param r Right border.
	 */
	public void setRight(float r) {
		right = r;
		update();
	}
	
	/**
	 * Get the bottom border value.
	 * 
	 * @return Bottom border.
	 */
	public float getBottom() {
		return bottom;
	}

	/**
	 * Set the bottom border value.
	 * .
	 * @param b Bottom border.
	 */
	public void setBottom(float b) {
		bottom = b;
		update();
	}

	/**
	 * Get the top border value.
	 * 
	 * @return Top border.
	 */
	public float getTop() {
		return top;
	}

	/**
	 * Set the top border value.
	 * .
	 * @param t Top border.
	 */
	public void setTop(float t) {
		top = t;
		update();
	}

	/**
	 * Get the near border value.
	 * 
	 * @return Near border.
	 */
	public float getNear() {
		return near;
	}

	/**
	 * Set the near border value.
	 * .
	 * @param n Near border.
	 */
	public void setNear(float n) {
		near = n;
		update();
	}

	/**
	 * Get the far border value.
	 * 
	 * @return Far border.
	 */
	public float getFar() {
		return far;
	}

	/**
	 * Set the far border value.
	 * .
	 * @param f Far border.
	 */
	public void setFar(float f) {
		far = f;
		update();
	}

	/**
	 * Set the transformation.
	 */
	private void update() {
		setIdentity();
		setValueAt(Float.toString(2.0f / (right - left)), 0, 0);
		setValueAt(Float.toString(-1.0f * (right + left) / (right - left)), 0, 3);
		setValueAt(Float.toString(2.0f / (top - bottom)), 1, 1);
		setValueAt(Float.toString(-1.0f * (top + bottom) / (top - bottom)), 1, 3);
		setValueAt(Float.toString(2.0f / (far - near)), 2, 2);
		setValueAt(Float.toString(-1.0f * (far + near) / (far - near)), 2, 3);
	}
}