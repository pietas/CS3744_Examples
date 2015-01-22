package Lecture_13;

import javax.swing.table.DefaultTableModel;


public class HW2Model extends DefaultTableModel {
	private float[] selectedData = null;
	private float[] vertexData = null;
	
	public float[] getAllData() {
		int r = getRowCount();
		vertexData = new float[2 * r];
		for (int i = 0; i < r; i++) {
			vertexData[2 * i] = Float.parseFloat((String) getValueAt(i, 0));
			vertexData[2 * i + 1] = Float.parseFloat((String) getValueAt(i, 1));
		}
		return vertexData;
	}
	
	public float[] getSelectedData(int[] indices) {
		int s =  indices.length;
		if (s > 0) {
			selectedData = new float[2 * s];
			int j = 0;
			for (int i = 0; i < s; i++) {
				selectedData[j++] = Float.parseFloat((String) getValueAt(indices[i], 0));
				selectedData[j++] = Float.parseFloat((String) getValueAt(indices[i], 1));
			}
		}
		else {
			selectedData = null;
		}
		return selectedData;
	}

}
