package Lecture_07;
import java.awt.BorderLayout;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;


/**
 * Lecture 7: A simple application that illustrates drawing different primitives using the same vertex array.
 *  
 * @author Denis Gracanin
 * @version 1
 */
public class SelectiveDrawing extends JFrame implements ActionListener, GLEventListener {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "Selective Drawing";
	private JComboBox<String> list = null;
	private String[] listItems = { "All (Black)", "Points (Red)", "Lines (Green)", "Triangle (Blue)" };
	private GLJPanel panel = null;
	private int fragmentShader = 0;
	private int vertexShader = 0;
	private int shaderProgram = 0;
	private int drawType = -1;
	private static final String VERTEX_SHADER =
			"#version 150\n" +
					"in vec4 vPosition;\n" +
					"uniform int drawType;\n" +
					"out vec4 vColor;\n" +
					"\n" +
					"void main(void) {\n" +
					"  gl_Position = vec4(vPosition.x, vPosition.y, 0.0, 1.0);\n" +
					"  gl_PointSize = 5.0;\n" +
					"  switch(drawType) {\n" +
					"  case 0:\n" +
					"    vColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
					"    break;\n" +
					"  case 1:\n" +
					"    vColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
					"  	 break;\n" +
					"  case 2:\n" +
					"    vColor = vec4(0.0, 1.0, 0.0, 1.0);\n" +
					"    break;\n" +
					"  case 3:\n" +
					"    vColor = vec4(0.0, 0.0, 1.0, 1.0);\n" +
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
	 * Creates an instance of <code>SelectiveDrawing</code> with the default title.
	 */
	public SelectiveDrawing() {
		this(TITLE);
	}

	/**
	 * Creates an instance of <code>SelectiveDrawing</code> with the customized title.
	 */
	public SelectiveDrawing(String title) {
		super(title);
		vertexData = new float[]{
				-0.5f, 0.5f,
				-0.5f, -0.5f,
				0.5f, -0.5f,
				0.5f, 0.5f,
				-0.25f, -0.25f,
				0.25f, -0.25f,
				0.0f, 0.25f
		};
		drawType = 0;
		setLayout(new BorderLayout());
		list = new JComboBox<String>(listItems);
		list.setSelectedIndex(drawType);
		list.addActionListener(this);
		add(list, BorderLayout.PAGE_START);
		panel = new GLJPanel(new GLCapabilities(GLProfile.getMaxProgrammableCore(false))); 
		panel.addGLEventListener(this);
		add(panel, BorderLayout.CENTER);
	}

	/**
	 * The main method.
	 * Creates the frame (application window) object, sets the size and shows it.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		SelectiveDrawing frame = new SelectiveDrawing();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 450);
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

		// Enable using gl_PointSize in the vertex shader.
				gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE);
				
		// Sets the line size.
				gl.glEnable(GL3.GL_LINE_SMOOTH);			
				gl.glLineWidth(3.0f);

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
				location = gl.glGetUniformLocation(shaderProgram, "drawType");
				gl.glUniform1i(location, drawType);

		// Draw primitives based on the value of drawType.
				switch (drawType) {
				case 0:
					gl.glDrawArrays(GL3.GL_POINTS, 0, 4);
					gl.glDrawArrays(GL3.GL_LINES, 0, 4);
					gl.glDrawArrays(GL3.GL_TRIANGLES, 4, 3);
					break;
				case 1:
					gl.glDrawArrays(GL3.GL_POINTS, 0, 4);
					break;
				case 2:
					gl.glDrawArrays(GL3.GL_LINES, 0, 4);
					break;
				case 3:
					gl.glDrawArrays(GL3.GL_TRIANGLES, 4, 3);
				}
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

	@Override
	public void actionPerformed(ActionEvent e) {
        JComboBox<String> cb = (JComboBox<String>) e.getSource();
        drawType = cb.getSelectedIndex();
        repaint();
    }

}