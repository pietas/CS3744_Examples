package Lecture_07;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.jogamp.common.nio.Buffers;


/**
 * Lecture 7: A simple application that illustrates orthographic transformations.
 * A centered red square with an arrow (triangle) mark in the upper left corner is drawn initially.
 * Orthographic transformations can change the initial size and position of the square.
 * Table provides a direct interface to specify a transformation matrix.
 * Table modifications do not affect text fields.
 * Text fields provide an interface to specify orthographic view transformation.
 * Text fields modifications modify the table based only on the current values set by the text fields.
 * The button resets the values to correspond to the identity matrix:
 * - left is -1
 * - right is 1
 * - bottom is -1
 * - top is 1
 * - far is -1
 * - near is 1
 *  
 * @author Denis Gracanin
 * @version 1
 */
public class OrthoDrawing extends JFrame implements ActionListener, GLEventListener, TableModelListener {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "Ortho Drawing";
	private static final int SQUARE_VERTEX = 0;
	private static final int AXIS_VERTEX = 1;
	private static final int TICK_VERTEX = 2;
	private static final int ARROW_VERTEX = 3;
	private static final float LEFT_DEFAULT = -1.0f;
	private static final float RIGHT_DEFAULT = 1.0f;
	private static final float BOTTOM_DEFAULT = -1.0f;
	private static final float TOP_DEFAULT = 1.0f;
	private static final float NEAR_DEFAULT = 1.0f;
	private static final float FAR_DEFAULT = -1.0f;
	private JButton resetButton = null;
	private OrthoModel model = null;
	private JTable table = null;
	private JPanel textPanel = null;
	private GLJPanel panel = null;
	private int fragmentShader = 0;
	private int vertexShader = 0;
	private int shaderProgram = 0;
	private int vertexType = 0;
	private static final String VERTEX_SHADER =
			"#version 150\n" +
					"in vec4 vPosition;\n" +
					"uniform mat4 transform;\n" +
					"uniform int vertexType;\n" +
					"out vec4 vColor;\n" +
					"\n" +
					"void main(void) {\n" +
					"  gl_Position = transform * vec4(vPosition.x, vPosition.y, 0.0, 1.0);\n" +
					"  switch (vertexType) {\n" +
					"  case 0:\n" + // SQUARE VERTICES
					"    vColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
					"    break;\n" +
					"  case 1:\n" + // AXIS VERTICES
					"    vColor = vec4(0.0, 0.0, 1.0, 1.0);\n" +
					"    break;\n" +
					"  case 2:\n" + // TICK VERTICES
					"    vColor = vec4(0.0, 1.0, 0.0, 1.0);\n" +
					"    break;\n" +
					"  case 3:\n" + // ARROW VERTICES
					"    vColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
					"    break;\n" +
					"  }\n" +
					"}\n";
	private static final String FRAGMENT_SHADER =
			"#version 150\n" +
					"in vec4 vColor;\n" +
					"out vec4 fColor;\n" +
					"\n" +
					"void main(void) {\n" +
					"  fColor = vColor;\n" +
					"}\n";
	/**
	 * Stores the vertex data, 2 values per vertex:
	 * 1st value: x coordinate
	 * 2nd value: y coordinate
	 */
	private float vertexData[] = null;

	/**
	 * Used for Vertex Buffer Object (VBO).
	 */
	private IntBuffer intBuffer = null;

	/**
	 * Used for Vertex Array Object (VAO).
	 */
	private FloatBuffer floatBuffer = null;
	private int location;

	/**
	 * Creates an instance of <code>TransformationDrawing</code> with the default title.
	 */
	public OrthoDrawing() {
		this(TITLE);
	}

	/**
	 * Creates an instance of <code>TransformationDrawing</code> with the customized title.
	 */
	public OrthoDrawing(String title) {
		super(title);
		vertexData = new float[]{
				// SQUARE VERTICES
				-0.5f, 0.5f,    // VERTEX 1
				-0.5f, -0.5f,   // VERTEX 2
				0.5f, -0.5f,    // VERTEX 3
				-0.5f, 0.5f,    // VERTEX 4
				0.5f, -0.5f,    // VERTEX 5
				0.5f, 0.5f,     // VERTEX 6

				// AXIS VERTICES
				0.0f, -1.0f,    // VERTEX 7
				0.0f, 1.0f,     // VERTEX 8
				-1.0f, 0.0f,    // VERTEX 9
				1.0f, 0.0f,     // VERTEX 10

				// TICK VERTICES
				-0.05f, -0.5f,  // VERTEX 11
				0.05f, -0.5f,   // VERTEX 12
				-0.05f, 0.5f,   // VERTEX 13
				0.05f, 0.5f,    // VERTEX 14
				-0.5f, -0.05f,  // VERTEX 15
				-0.5f, 0.05f,   // VERTEX 16
				0.5f, -0.05f,   // VERTEX 17
				0.5f, 0.05f,    // VERTEX 18

				// ARROW VERTICES
				0.45f, 0.5f,    // VERTEX 19
				0.5f, 0.45f,    // VERTEX 20
				0.5f, 0.5f      // VERTEX 21
		};
		setLayout(new BorderLayout());

		textPanel = new JPanel(new GridLayout(0,2));

		textPanel.add(new JLabel("Value"));

		resetButton = new JButton("Default");
		resetButton.setActionCommand("D");
		resetButton.addActionListener(this);
		textPanel.add(resetButton);

		textPanel.add(new JLabel("Left"));
		textPanel.add(getTextField(LEFT_DEFAULT, "L", this));
		textPanel.add(new JLabel("Right"));
		textPanel.add(getTextField(RIGHT_DEFAULT, "R", this));
		textPanel.add(new JLabel("Bottom"));
		textPanel.add(getTextField(BOTTOM_DEFAULT, "B", this));
		textPanel.add(new JLabel("Top"));
		textPanel.add(getTextField(TOP_DEFAULT, "T", this));
		textPanel.add(new JLabel("Near"));
		textPanel.add(getTextField(NEAR_DEFAULT, "N", this));
		textPanel.add(new JLabel("Far"));
		textPanel.add(getTextField(FAR_DEFAULT, "F", this));

		JPanel controlPanel = new JPanel(new BorderLayout());

		controlPanel.add(textPanel, BorderLayout.CENTER);

		model = new OrthoModel();
		model.addTableModelListener(this);
		table = new JTable(model);
		table.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		table.setGridColor(Color.BLUE);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		controlPanel.add(table, BorderLayout.PAGE_START);

		panel = new GLJPanel(new GLCapabilities(GLProfile.getMaxProgrammable(false))); 
		panel.addGLEventListener(this);

		add(controlPanel, BorderLayout.LINE_START);
		add(panel, BorderLayout.CENTER);
	}

	/**
	 * The main method.
	 * Creates the frame (application window) object, sets the size and shows it.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		OrthoDrawing frame = new OrthoDrawing();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(650, 400);
		frame.setVisible(true);		
	}

	/**
	 * Overridden to draw the primitives in the drawable area.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();

		// Sets the background color.
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);

		// Use the shader		
		gl.glUseProgram(shaderProgram);

		// Set the VBO.
		intBuffer = Buffers.newDirectIntBuffer(1);
		gl.glGenBuffers(1, intBuffer);
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, intBuffer.get(0));

		// Set the VAO.
		floatBuffer = Buffers.newDirectFloatBuffer(vertexData);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertexData.length * Buffers.SIZEOF_FLOAT, floatBuffer, GL3.GL_STATIC_DRAW);

		// Use vertex data in the vertexData array as vPosition vertex shader variable.
		location = gl.glGetAttribLocation(shaderProgram, "vPosition");
		gl.glVertexAttribPointer(location, 2, GL3.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(location);

		// Use the current value of drawType variable as drawType vertex shader variable.
		location = gl.glGetUniformLocation(shaderProgram, "transform");
		gl.glUniformMatrix4fv(location, 1, true, Buffers.newDirectFloatBuffer(model.getTransform()));

		// Draw primitives based on the value of vertexType.

		// AXES
		vertexType = AXIS_VERTEX;
		location = gl.glGetUniformLocation(shaderProgram, "vertexType");
		gl.glUniform1i(location, vertexType);
		gl.glDrawArrays(GL3.GL_LINES, 6, 4);

		// TICKS
		vertexType = TICK_VERTEX;
		location = gl.glGetUniformLocation(shaderProgram, "vertexType");
		gl.glUniform1i(location, vertexType);
		gl.glDrawArrays(GL3.GL_LINES, 10, 8);

		// SQUARE
		vertexType = SQUARE_VERTEX;
		location = gl.glGetUniformLocation(shaderProgram, "vertexType");
		gl.glUniform1i(location, vertexType);
		gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 6);

		// ARROW
		vertexType = ARROW_VERTEX;
		location = gl.glGetUniformLocation(shaderProgram, "vertexType");
		gl.glUniform1i(location, vertexType);
		gl.glDrawArrays(GL3.GL_TRIANGLES, 18, 3);

	}

	/**
	 * Detaches and deletes the created shaders and the shader program..
	 *
	 * @param drawable OpenGL drawable.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();
		gl.glDetachShader(shaderProgram, vertexShader);
		gl.glDeleteShader(vertexShader);
		gl.glDetachShader(shaderProgram, fragmentShader);
		gl.glDeleteShader(fragmentShader);
		gl.glDeleteProgram(shaderProgram);
	}

	/**
	 * Creates the shader program from source.
	 *
	 * @param drawable OpenGL drawable.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();

		vertexShader = compile(gl, GL3.GL_VERTEX_SHADER, VERTEX_SHADER);
		fragmentShader = compile(gl, GL3.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
		shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(shaderProgram, vertexShader);
		gl.glAttachShader(shaderProgram, fragmentShader);
		gl.glLinkProgram(shaderProgram);
	}

	/**
	 * Overridden as an empty method.
	 *
	 * @param drawable OpenGL drawable.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

	/**
	 * A utility method to create a shader
	 * 
	 * @param gl The OpenGL context.
	 * @param shaderType The type of the shader.
	 * @param program The string containing the program.
	 * @return the created shader.
	 */
	private int compile(GL3 gl, int shaderType, String program) {
		int shader = gl.glCreateShader(shaderType);
		String[] lines = new String[] { program };
		int[] lengths = new int[] { lines[0].length() };
		gl.glShaderSource(shader, lines.length, lines, lengths, 0);
		gl.glCompileShader(shader);
		int[] compiled = new int[1];
		gl.glGetShaderiv(shader, GL3.GL_COMPILE_STATUS, compiled, 0);
		if(compiled[0]==0) {
			int[] logLength = new int[1];
			gl.glGetShaderiv(shader, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
			byte[] log = new byte[logLength[0]];
			gl.glGetShaderInfoLog(shader, logLength[0], (int[])null, 0, log, 0);
			System.err.println("Error compiling the shader: " + new String(log));
			System.exit(1);
		}
		return shader;
	}

	/**
	 * Resets the model to the identity matrix.
	 * 
	 * @param e Action event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		JTextField text = null;
		if (source instanceof JTextField) {
			text = (JTextField) e.getSource();
		}
		switch (e.getActionCommand()) {
		case "B": // Bottom
			model.setBottom(checkValue(text, model.getBottom(), model.getTop(), true));
			break;
		case "D": // Default (identity)
			model.setIdentity();
			Component[] c = textPanel.getComponents();
			for (int i = 0; i < c.length; i++) {
				if (c[i] instanceof JTextField) {
					JTextField t = (JTextField) c[i];
					switch (t.getName()) {
					case "B":
						t.setText(Float.toString(BOTTOM_DEFAULT));
						break;		
					case "F":
						t.setText(Float.toString(FAR_DEFAULT));
						break;
					case "L":
						t.setText(Float.toString(LEFT_DEFAULT));
						break;		
					case "N":
						t.setText(Float.toString(NEAR_DEFAULT));
						break;
					case "R":
						t.setText(Float.toString(RIGHT_DEFAULT));
						break;		
					case "T":
						t.setText(Float.toString(TOP_DEFAULT));
						break;			
					}
				}
			}
			break;
		case "F": // Far
			model.setFar(checkValue(text, model.getFar(), model.getNear(), true));
			break;
		case "L": // Left
			model.setLeft(checkValue(text, model.getLeft(), model.getRight(), true));
			break;
		case "N": // Near
			model.setNear(checkValue(text, model.getNear(), model.getFar(), false));
			break;
		case "R": // Right
			model.setRight(checkValue(text, model.getRight(), model.getLeft(), false));
			break;
		case "T": // Top
			model.setTop(checkValue(text, model.getTop(), model.getBottom(), false));
			break;
		}
	}

	/**
	 * Repaints the graph when the model is updated.
	 * 
	 * @param e Table model event
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		panel.repaint();
	}

	/**
	 * Create a text field.
	 * 
	 * @param d default value.
	 * @param c Command.
	 * @param l Action listener.
	 * @return Text field.
	 */
	private JTextField getTextField(float d, String c, ActionListener l) {
		JTextField t = new JTextField(Float.toString(d));
		t.setName(c);
		t.setActionCommand(c);
		t.addActionListener(l);
		return t;
	}

	/**
	 * Check the value entered in the text field.
	 *  
	 * @param t Text field.
	 * @param old The old value.
	 * @param limit The limit value.
	 * @param max Limit type (true - max, false - min).
	 * @return New value.
	 */
	private float checkValue(JTextField t, float old, float limit, boolean max) {
		float value = old;
		try {
			value = Float.parseFloat(t.getText());
			if ((max && value > limit) || (!max && value < limit)) {
				value = old;
			}
		}
		catch (NumberFormatException e) {
			value = old;
		}
		t.setText(Float.toString(value));
		return value;
	}
}