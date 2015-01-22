package Lecture_09;
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
 * Lecture 8: A simple application that illustrates drawing Letters H and W using OpenGL primitives and a vertex array.
 *  
 * @author Denis Gracanin
 * @version 1
 */
public class HelloWorldJOGLArg extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "Hello World JOGL Args Version";
	private GLJPanel panel = null;
	private float red = 0.0f;
	private float green = 0.0f;
	private float blue = 0.0f;
	private float alpha = 0.0f;
	private int fragmentShader = 0;
	private int vertexShader = 0;
	private int shaderProgram = 0;
	private static final String VERTEX_SHADER =
			"#version 150\n" +
					"in vec4 vPosition;\n" +
					"\n" +
					"void main(void) {\n" +
					"  gl_Position = vec4(vPosition.x, vPosition.y, 0.0, 1.0);\n" +
					"}\n";
	private static final String FRAGMENT_SHADER =
			"#version 150\n" +
					"uniform vec4 color;\n" +
					"out vec4 fColor;\n" +
					"\n" +
					"void main(void) {\n" +
					"  fColor = color;\n" +
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
	 * Creates an instance of <code>SelectiveDrawing</code> with the default title and color.
	 */
	public HelloWorldJOGLArg() {
		this(TITLE);
	}

	/**
	 * Creates an instance of <code>SelectiveDrawing</code> with the default title and custom color.
	 */
	public HelloWorldJOGLArg(String[] args) {
		this(TITLE, args);
	}

	/**
	 * Creates an instance of <code>SelectiveDrawing</code> with the custom title and default color.
	 */
	public HelloWorldJOGLArg(String args) {
		this(TITLE, null);
	}

	/**
	 * Creates an instance of <code>SelectiveDrawing</code> with the customized title and default color.
	 */
	public HelloWorldJOGLArg(String title, String[] args) {
		super(title);
		vertexData = new float[]{
				-0.75f,  0.5f,  // vertex 0
				-0.75f, -0.5f,  // vertex 1
				-0.25f,  0.5f,  // vertex 2
				-0.25f, -0.5f,  // vertex 3
				-0.75f,  0.0f,  // vertex 4
				-0.25f,  0.0f,  // vertex 5

				 0.15f,  0.5f,  // vertex 6
				 0.40f, -0.5f,  // vertex 7
				 0.50f,  0.0f,  // vertex 8
				 0.60f, -0.5f,  // vertex 9
				 0.85f,  0.5f,  // vertex 10
		};
		if (args.length == 4) {
		  red = getColor(args[0]);
		  green = getColor(args[1]);
		  blue = getColor(args[2]);
		  alpha = getColor(args[3]);
		}
		setLayout(new BorderLayout());
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
		if (args.length != 4 && args.length != 0) {
			System.out.println("Arguments, if present, are four color components (0-1): red green blue alpha");
			System.exit(1);
		}
		HelloWorldJOGLArg frame = new HelloWorldJOGLArg(args);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setVisible(true);		
	}

	/**
	 * Overridden to draw the primitives in the drawable area.
	 * 
	 * @param drawable OpenGL drawable.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();

		// Set the background color (white).
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

		// Set the foreground color (white).
		location = gl.glGetUniformLocation(shaderProgram, "color");
		gl.glUniform4f(location, red, green, blue, alpha);

		// Draw letter H (the first six vertices as 3 lines)
		gl.glDrawArrays(GL3.GL_LINES, 0, 6);
		
		// Draw letter W (the next five vertices as a line strip)
		gl.glDrawArrays(GL3.GL_LINE_STRIP, 6, 5);
	}

	/**
	 * Detaches and deletes the created shaders and the shader program.
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
	 * @param x The x coordinate of the lower left corner of the viewport rectangle, in pixels.
	 * @param y The y coordinate of the lower left corner of the viewport rectangle, in pixels.
	 * @param width The width of the viewport.
	 * @param height The height of the viewport.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }

	/**
	 * A utility method to create a shader
	 * 
	 * @param gl The OpenGL context.
	 * @param shaderType The type of the shader.
	 * @param program The string containing the program.
	 * @return The created shader.
	 */
	private int compile(GL3 gl, int shaderType, String program) {
		int shader = gl.glCreateShader(shaderType);
		String[] lines = new String[] { program };
		int[] lengths = new int[] { lines[0].length() };
		gl.glShaderSource(shader, lines.length, lines, lengths, 0);
		gl.glCompileShader(shader);
		int[] compiled = new int[1];
		gl.glGetShaderiv(shader, GL3.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			int[] logLength = new int[1];
			gl.glGetShaderiv(shader, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
			byte[] log = new byte[logLength[0]];
			gl.glGetShaderInfoLog(shader, logLength[0], (int[])null, 0, log, 0);
			System.err.println("Error compiling the shader: " + new String(log));
			System.exit(1);
		}
		return shader;
	}

	private float getColor(String s) {
		float c = 0.0f;
		try {
			c = Float.parseFloat(s);
			if (c < 0.0f) {
				c = 0.0f;
			}
			else if (c > 1.0f) {
				c = 1.0f;
			}
		}
		catch (NumberFormatException e) { }
		return c;
	}
}