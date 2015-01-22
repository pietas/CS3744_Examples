package Lecture_11;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jogamp.common.nio.Buffers;

/**
 * Lecture 12: A graphic view (shader based) of the square data that supports interactive user input.
 *  
 * @author Denis Gracanin
 * @version 2
 */
public class SquareView extends JComponent implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 2L;
	private GLJPanel panel = null;
	private Color backgroundColor = Color.WHITE;
	private int fragmentShader = 0;
	private int vertexShader = 0;
	private int shaderProgram = 0;
	private int red = 0;
	private int green = 0;
	private int blue = 0;
	private float scale = 0.0f;
	private int dark = 0;
	private int oldY = 0;
	private float oldScale = 0.0f;
	private float newScale = 0.0f;
	private boolean reset = false;
	private ArrayList<ChangeListener> listeners = null;
	private static final String VERTEX_SHADER =
			"#version 150\n" +
		    "in vec4 vPosition;\n" +
			"uniform int red;\n" +
			"uniform int green;\n" +
			"uniform int blue;\n" +
			"uniform float scale;\n" +
			"uniform int dark;\n" +
			"out vec4 vColor;\n" +
			"\n" +
			"void main(void) {\n" +
			"  gl_Position = vec4(scale * vPosition.x, scale * vPosition.y, 0.0, 1.0);\n" +
			"  if (int(vPosition.z) == dark) {" +
		    "    vColor = vec4(0.75f * red / 255.0, 0.75f * green / 255.0, 0.75f * blue / 255.0, 1.0);" +
			"  }\n" +
		    "  else {\n" +
		    "    vColor = vec4(red / 255.0, green / 255.0, blue / 255.0, 1.0);" +
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

	private float vertices[]= {
		    -1.0f,  1.0f, 0.0f,
		    -1.0f, -1.0f, 0.0f,
		     1.0f, -1.0f, 0.0f,
		    -1.0f,  1.0f, 1.0f,
		     1.0f, -1.0f, 1.0f,
		     1.0f,  1.0f, 1.0f
	};

	/**
	 * Creates an instance of <code>SquareView</code> with the specified values for scale and color.
	 */
	public SquareView(double s, Color c) {
		super();
		listeners = new ArrayList<ChangeListener>();
		setShaderParameters(s, c);
		panel = new GLJPanel(new GLCapabilities(GLProfile.getMaxProgrammableCore(false))); 
		panel.addGLEventListener(this);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		addKeyListener(this);
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
		
		FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(vertices);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.length * Buffers.SIZEOF_FLOAT, floatBuffer, GL3.GL_STATIC_DRAW);
	    
		int location = gl.glGetAttribLocation(shaderProgram, "vPosition");
		gl.glVertexAttribPointer(location, 3, GL3.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(location);
        location = gl.glGetUniformLocation(shaderProgram, "red");
        gl.glUniform1i(location, red);
        location = gl.glGetUniformLocation(shaderProgram, "green");
        gl.glUniform1i(location, green);
        location = gl.glGetUniformLocation(shaderProgram, "blue");
        gl.glUniform1i(location, blue);
        location = gl.glGetUniformLocation(shaderProgram, "scale");
        gl.glUniform1f(location, scale);
        location = gl.glGetUniformLocation(shaderProgram, "dark");
        gl.glUniform1i(location, dark);
 
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, vertices.length / 3);
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

	/**
	 * Returns new scale value.
	 * 
	 * @return scale value
	 */
	public double getValue() {
		return newScale;
	}

	public boolean getReset() {
		return reset;
	}
	
	/**
	 * Overridden as an empty method.
	 *
	 * @param e the key event
	 */
	@Override
	public void keyPressed(KeyEvent e) { }

	/**
	 * Overridden as an empty method.
	 *
	 * @param e the key event
	 */
	@Override
	public void keyReleased(KeyEvent e) { }

	/**
	 * When <code>r</code> character is type, the views are reset.
	 *
	 * @param e the key event
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 'r') {
			dark = 0;
			reset = true;
			fireStateChanged();
		}		
	}

	/**
	 * Clicking the right mouse button changes the triangle that is drawn darker.
	 *
	 * @param e the mouse event
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			dark = 1 - dark;
			repaint();
		}
	}

	/**
	 * Records the values of y and scale.
	 *
	 * @param e the mouse event
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		requestFocusInWindow();
		if (e.getButton() == MouseEvent.BUTTON1) {
			oldY = e.getY();
			oldScale = scale;
		}
	}

	/**
	 * Overridden as an empty method.
	 *
	 * @param e the mouse event
	 */
	@Override
	public void mouseReleased(MouseEvent e) {}

	/**
	 * Overridden as an empty method.
	 *
	 * @param e the mouse event
	 */
	@Override
	public void mouseEntered(MouseEvent e) { }

	/**
	 * Overridden as an empty method.
	 *
	 * @param e the mouse event
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Calculates the new value for scale based on the vertical mouse movement.
	 *
	 * @param e the mouse event
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		newScale = oldScale + (oldY - e.getY()) / (1.0f * panel.getWidth());
		reset = false;
		fireStateChanged();
	}

	/**
	 * Overridden as an empty method.
	 *
	 * @param e the mouse event
	 */
	@Override
	public void mouseMoved(MouseEvent e) {}

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