package Lecture_09;
import java.awt.BorderLayout;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JComponent;

import com.jogamp.common.nio.Buffers;

/**
 * Lecture 8: A graphic view (shader based) of the square data.
 *  
 * @author Denis Gracanin
 * @version 1
 */
public class SquareView extends JComponent implements GLEventListener {
	private static final long serialVersionUID = 1L;
	private GLJPanel panel = null;
	private Color backgroundColor = Color.WHITE;
	private int fragmentShader = 0;
	private int vertexShader = 0;
	private int shaderProgram = 0;
	private int red = 0;
	private int green = 0;
	private int blue = 0;
	private float scale = 0.0f;
	private static final String VERTEX_SHADER =
			"#version 150\n" +
		    "in vec4 vPosition;\n" +
			"uniform int red;\n" +
			"uniform int green;\n" +
			"uniform int blue;\n" +
			"uniform float scale;\n" +
			"out vec4 vColor;\n" +
			"\n" +
			"void main(void) {\n" +
			"  gl_Position = vec4(scale * vPosition.x, scale * vPosition.y, 0.0, 1.0);\n" +
		    "  vColor = vec4(red / 255.0, green / 255.0, blue / 255.0, 1.0);" +
			"}\n";
	private static final String FRAGMENT_SHADER =
			"#version 150\n" +
	        "in vec4 vColor;\n" +
			"out vec4 fColor;\n" +
	    	"\n" +
			"void main(void) {\n" +
			"  fColor = vColor;\n" +
			"}\n";

	private float vertexData[]= {
		    -1.0f,  1.0f,
		    -1.0f, -1.0f,
		     1.0f, -1.0f,
		    -1.0f,  1.0f,
		     1.0f, -1.0f,
		     1.0f,  1.0f
	};

	/**
	 * Creates an instance of <code>SquareView</code> with the specified values for scale and color.
	 */
	public SquareView(double s, Color c) {
		super();
		setShaderParameters(s, c);
		panel = new GLJPanel(new GLCapabilities(GLProfile.getMaxProgrammableCore(false))); 
		panel.addGLEventListener(this);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
	}

	/**
	 * Overridden as an empty method.
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
	 * Overridden to draw the square centered in the drawable area.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();

		gl.glClearColor(backgroundColor.getRed() / 255.0f, backgroundColor.getGreen() / 255.0f, backgroundColor.getBlue() / 255.0f, backgroundColor.getAlpha() / 255.0f);
	    gl.glClear(GL3.GL_COLOR_BUFFER_BIT);

		gl.glUseProgram(shaderProgram);

		IntBuffer intBuffer = Buffers.newDirectIntBuffer(1);
		gl.glGenBuffers(1, intBuffer);
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, intBuffer.get(0));
		
		FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(vertexData);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertexData.length * Buffers.SIZEOF_FLOAT, floatBuffer, GL3.GL_STATIC_DRAW);
	    
		int location = gl.glGetAttribLocation(shaderProgram, "vPosition");
		gl.glVertexAttribPointer(location, 2, GL3.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(location);
        location = gl.glGetUniformLocation(shaderProgram, "red");
        gl.glUniform1i(location, red);
        location = gl.glGetUniformLocation(shaderProgram, "green");
        gl.glUniform1i(location, green);
        location = gl.glGetUniformLocation(shaderProgram, "blue");
        gl.glUniform1i(location, blue);
        location = gl.glGetUniformLocation(shaderProgram, "scale");
        gl.glUniform1f(location, scale);

        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, vertexData.length / 2);
		gl.glFlush();
	}

	/**
	 * Overridden as an empty method.
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
	 * Overridden as an empty method.
	 *
	 * @param drawable OpenGL drawable.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

	/**
	 * Draws the square with the specified scale and color.
	 * 
	 * @param s the scale to set
	 * @param c the color to set
	 */
	public void draw(double s, Color c) {
		setShaderParameters(s, c);
		repaint();
	}

	/**
	 * Sets the background color.
	 *
	 * @param bc the background color to set
	 */
	@Override
	public void setBackground(Color bc) {
		backgroundColor = bc;
	}

	/**
	 * A utility method to create a shader 
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
	 * A utility method to set the shader parameters.
	 * 
	 * @param s the scale to set
	 * @param c the color to set
	 */
	private void setShaderParameters(double s, Color c) {
		scale = (float) s;
		red = c.getRed();
		green = c.getGreen();
		blue = c.getBlue();
	}

}