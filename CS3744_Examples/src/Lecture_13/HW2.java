package Lecture_13;


import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.jogamp.common.nio.Buffers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;
import java.util.Vector;


/**
 * Homework 2: .
 * 
 * @author Denis Gracanin
 * @version 2
 */
@SuppressWarnings("serial")
public class HW2 extends JFrame implements ActionListener, GLEventListener, TableModelListener, ChangeListener, ListSelectionListener { 
	private final static String TITLE = "HW2: gracanin";
	private final static String HELP = "Homework 2 version 2.";
	private JMenuBar menuBar = null;
	private JMenu fileMenu = null;
	private JMenu editMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem openMenuItem = null;
	private JMenuItem closeMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JMenuItem quitMenuItem = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem pasteMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JScrollPane scrollPane = null;
	private JComboBox<String> comboBox =  null;
	private JButton backgroundButton = null;
	private JButton foregroundButton = null;
	private JButton selectionButton = null;
	private Color backgroundColor = Color.WHITE;
	private Color foregroundColor = Color.BLUE;
	private Color selectionColor = Color.RED;
	private int currentColor = -1;
	private String clipboard = null;

	private File file = null;
	/*
	 * The model.
	 */
	private HW2Model model = null;
	/*
	 * The table view.
	 */
	private JTable table = null;
	/*
	 * The graphic view.
	 */
	private GLJPanel graph = null;
	private int fragmentShader = 0;
	private int vertexShader = 0;
	private int shaderProgram = 0;
	private int drawType = -1;

	private static final String VERTEX_SHADER =
			"#version 150\n" +
					"in vec4 vPosition;\n" +
					"uniform int vertexType;\n" +
					"uniform vec4 color;\n" +
					"out vec4 vColor;\n" +
					"\n" +
					"void main(void) {\n" +
					"  gl_Position = vec4(vPosition.x, vPosition.y, 0.0, 1.0);\n" +
					"  gl_PointSize = 3.0;\n" +
					"  vColor = color;\n" +
					"}\n";
	private static final String FRAGMENT_SHADER =
			"#version 150\n" +
					"in vec4 vColor;\n" +
					"out vec4 fColor;\n" +
					"\n" +
					"void main(void) {\n" +
					"  fColor = vColor;\n" +
					"}\n";
	/*
	 * Stores the vertex data, 2 values per vertex:
	 * 1st value: x coordinate
	 * 2nd value: y coordinate
	 */
	private float vertexData[] = null;
	private float selectedData[] = null;

	/*
	 * Used for Vertex Buffer Object (VBO).
	 */
	private IntBuffer intBuffer = null;

	/*
	 * Used for Vertex Array Object (VAO).
	 */
	private FloatBuffer floatBuffer = null;
	private int location;


	/**
	 * Creates an instance of <code>HW2</code> class.
	 * The default title is used.
	 */
	public HW2() {
		this(TITLE);
	}

	/**
	 * Creates an instance of <code>SimpleEditor</code> class.
	 * 
	 * @param title The title of the application window.
	 */
	public HW2(String title) {
		super(title);
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");

		openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(this);
		openMenuItem.setActionCommand("O");
		fileMenu.add(openMenuItem);

		closeMenuItem = new JMenuItem("Close");
		closeMenuItem.addActionListener(this);
		closeMenuItem.setActionCommand("W");
		fileMenu.add(closeMenuItem);

		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(this);
		saveMenuItem.setActionCommand("S");
		fileMenu.add(saveMenuItem);

		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(this);
		quitMenuItem.setActionCommand("Q");
		fileMenu.add(quitMenuItem);

		menuBar.add(fileMenu);

		editMenu = new JMenu("Edit");

		copyMenuItem = new JMenuItem("Copy");
		copyMenuItem.addActionListener(this);
		copyMenuItem.setActionCommand("C");
		editMenu.add(copyMenuItem);

		pasteMenuItem = new JMenuItem("Paste");
		pasteMenuItem.addActionListener(this);
		pasteMenuItem.setActionCommand("V");
		editMenu.add(pasteMenuItem);

		menuBar.add(editMenu);

		helpMenu = new JMenu("Help");
		aboutMenuItem = new JMenuItem("About Homework 2");
		aboutMenuItem.addActionListener(this);
		aboutMenuItem.setActionCommand("A");
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		model = new HW2Model();
		model.addTableModelListener(this);
		table = new JTable(model);
		scrollPane = new JScrollPane(table);

		// Configure the color scheme.
		table.setGridColor(Color.BLUE);
		table.getTableHeader().setBackground(Color.YELLOW);
		table.setSelectionBackground(Color.RED);

		// Configure the selection model.
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);	
		table.getSelectionModel().addListSelectionListener(this);


		graph = new GLJPanel(new GLCapabilities(GLProfile.getMaxProgrammableCore(false)));
		graph.addGLEventListener(this);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, graph);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(200);

		Dimension minimumSize = new Dimension(100, 50);
		scrollPane.setMinimumSize(minimumSize);
		graph.setMinimumSize(minimumSize);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);

		JToolBar toolBar = new JToolBar("HW2 Tools");
		String[] names = { "GL_POINTS", "GL_LINES", "GL_LINE_STRIP", "GL_LINE_LOOP", "GL_TRIANGLES", "GL_TRIANGLE_STRIP", "GL_TRIANGLE_FAN" };
		comboBox = new JComboBox<String>(names);
		comboBox.setPreferredSize(new Dimension(200,30));
		comboBox.setMaximumSize(new Dimension(200,30));
		comboBox.addActionListener(this);
		comboBox.setActionCommand("CB");
		comboBox.setSelectedIndex(0);
		toolBar.add(comboBox);
		toolBar.addSeparator();
		toolBar.add(backgroundButton = new JButton("Background"));
		backgroundButton.addActionListener(this);
		backgroundButton.setActionCommand("BB");
		backgroundButton.setBackground(backgroundColor);
		toolBar.add(foregroundButton = new JButton("Foreground"));
		foregroundButton.addActionListener(this);
		foregroundButton.setActionCommand("FB");
		foregroundButton.setBackground(foregroundColor);
		toolBar.add(selectionButton = new JButton("Selection"));
		selectionButton.setBackground(selectionColor);
		selectionButton.addActionListener(this);
		selectionButton.setActionCommand("SB");
		add(toolBar, BorderLayout.PAGE_START);
	}

	/**
	 * The main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		final HW2 app = new HW2();
		app.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				app.dispose();
				System.exit(0);
			}
		});
		app.setSize(600, 400);
		app.setVisible(true);
	}

	/**
	 * The action event handler that determines the source of the event
	 * and processes the event accordingly.
	 * 
	 * @param e The generated event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		ColorDialog c = null;
		String command = null;
		switch (source.getClass().getName()) {
		case "javax.swing.JMenuItem":
			command = ((JMenuItem) source).getActionCommand();
			break;
		case "javax.swing.JComboBox":
		command = ((JComboBox<String>) source).getActionCommand();
		break;
		case "javax.swing.JButton":
		command = ((JButton) source).getActionCommand();
		break;
		default:
			command = "";
		}
		switch (command) {

		case "O":
			JFileChooser fileChooser = new JFileChooser();
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
				model.setColumnCount(0);
				try {

					//Create input stream (a BufferdReader object) from the file.
					BufferedReader inputStream = new BufferedReader(new FileReader(file));

					//Read the first line to get the column names.
					String line = null;
					if ((line = inputStream.readLine()) != null) {
						Scanner scanner = new Scanner(line);
						scanner.useDelimiter(",");
						while (scanner.hasNext()) {
							model.addColumn(scanner.next());
						}
						scanner.close();
					}

					//Read the remaining lines to get the data.
					while ((line = inputStream.readLine()) != null) {
						Vector<String> tmpVector = new Vector<String>();
						Scanner scanner = new Scanner(line);
						scanner.useDelimiter(",");
						while (scanner.hasNext()) {
							tmpVector.add(scanner.next());
						}
						model.addRow(tmpVector);
						scanner.close();
					}

					//Close the input stream.
					inputStream.close();

					setTitle(TITLE + ": " + file.getName());
				}
				catch (IOException ex) {
					System.err.println(ex);
				}	
			}
			break;
		case "W":
			file = null;

			//Clear the model (erase the model data).
			model.setRowCount(0);
			model.setColumnCount(0);
			setTitle(TITLE);
			break;
		case "S":
			try {
				// Create output stream (a BufferdReader object) from the file.
				BufferedWriter outputStream = new BufferedWriter(new FileWriter(file));

				// Write the first line to store the column names.
				int columnCount = model.getColumnCount();
				int rowCount = model.getRowCount();
				if (columnCount > 0) {
					for (int i = 0; i < columnCount -1; i++) {
						outputStream.write(model.getColumnName(i) + ",");
					}
					outputStream.write(model.getColumnName(columnCount - 1) + "\n");
				}
				for (int i = 0; i < rowCount; i++) {
					for (int j = 0; j < columnCount; j++) {
						outputStream.write((j != 0 ? ", " : "") + model.getValueAt(i, j));			
					}
					outputStream.write("\n");
				}
				outputStream.close();
			}
			catch (IOException ex) {
				System.err.println(ex);
			}
			break;
		case "Q":
			System.exit(0);
			break;
		case "C":
			int row = table.getEditingRow();
			int col = table.getEditingColumn();
			if (row >= 0 && col >= 0) {
				clipboard = (String) model.getValueAt(row, col);
			}
			else {
				clipboard = null;
			}
			break;
		case "V":
			row = table.getEditingRow();
			col = table.getEditingColumn();
			if (row >= 0 && col >= 0) {
				model.setValueAt(clipboard, row, col);
			}
			break;
		case "A":
			JOptionPane.showMessageDialog(this, HELP);
			break;
		case "CB":
			drawType = comboBox.getSelectedIndex();
			repaint();
			break;
		case "BB":
			currentColor = 0;
			c =  new ColorDialog(this, backgroundColor, this);
			c.setVisible(true);			
			break;
		case "FB":
			currentColor = 1;
			c =  new ColorDialog(this, foregroundColor, this);
			c.setVisible(true);			
			break;
		case "SB":
			currentColor = 2;
			c =  new ColorDialog(this, selectionColor, this);
			c.setVisible(true);			
		}

	}

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

	@Override
	public void dispose(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();
		gl.glDetachShader(shaderProgram, vertexShader);
		gl.glDeleteShader(vertexShader);
		gl.glDetachShader(shaderProgram, fragmentShader);
		gl.glDeleteShader(fragmentShader);
		gl.glDeleteProgram(shaderProgram);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();
		gl.glClearColor(backgroundColor.getRed() / 255.0f, backgroundColor.getGreen() / 255.0f, backgroundColor.getBlue() / 255.0f, 0.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
		if (vertexData == null) return;
		// Enable using gl_PointSize in the vertex shader.
		gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE);

		// Sets the line size.
		gl.glEnable(GL3.GL_LINE_SMOOTH);			
		gl.glLineWidth(3.0f);

		// Use the shader		
		gl.glUseProgram(shaderProgram);

		// Set the VBO.
		intBuffer = Buffers.newDirectIntBuffer(2);
		gl.glGenBuffers(2, intBuffer);
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, intBuffer.get(0));

		// Set the VAO.
		floatBuffer = Buffers.newDirectFloatBuffer(vertexData);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertexData.length * Buffers.SIZEOF_FLOAT, floatBuffer, GL3.GL_STATIC_DRAW);

		// Use vertex data in the vertexData array as vPosition vertex shader variable.
		location = gl.glGetAttribLocation(shaderProgram, "vPosition");
		gl.glVertexAttribPointer(location, 2, GL3.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(location);

		// Use the current value of drawType variable as drawType vertex shader variable.
		location = gl.glGetUniformLocation(shaderProgram, "color");
		gl.glUniform4f(location, foregroundColor.getRed() / 255.0f, foregroundColor.getGreen() / 255.0f, foregroundColor.getBlue() / 255.0f, 0.0f);

		// Draw primitives based on the value of drawType.
		switch (drawType) {
		case 0:
			gl.glDrawArrays(GL3.GL_POINTS, 0, vertexData.length / 2);
			break;
		case 1:
			gl.glDrawArrays(GL3.GL_LINES, 0, vertexData.length / 2);
			break;
		case 2:
			gl.glDrawArrays(GL3.GL_LINE_STRIP, 0, vertexData.length / 2);
			break;
		case 3:
			gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, vertexData.length / 2);
			break;
		case 4:
			gl.glDrawArrays(GL3.GL_TRIANGLES, 0, vertexData.length / 2);
			break;
		case 5:
			gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, vertexData.length / 2);
			break;
		case 6:
			gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, vertexData.length / 2);
			break;
		}

		if (selectedData == null) return;

		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, intBuffer.get(1));

		// Set the VAO.
		floatBuffer = Buffers.newDirectFloatBuffer(selectedData);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, selectedData.length * Buffers.SIZEOF_FLOAT, floatBuffer, GL3.GL_STATIC_DRAW);

		// Use vertex data in the vertexData array as vPosition vertex shader variable.
		location = gl.glGetAttribLocation(shaderProgram, "vPosition");
		gl.glVertexAttribPointer(location, 2, GL3.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(location);

		// Use the current value of drawType variable as drawType vertex shader variable.
		location = gl.glGetUniformLocation(shaderProgram, "color");
		gl.glUniform4f(location, selectionColor.getRed() / 255.0f, selectionColor.getGreen() / 255.0f, selectionColor.getBlue() / 255.0f, 0.0f);

		// Draw primitives based on the value of drawType.
		switch (drawType) {
		case 0:
			gl.glDrawArrays(GL3.GL_POINTS, 0, selectedData.length / 2);
			break;
		case 1:
			gl.glDrawArrays(GL3.GL_LINES, 0, selectedData.length / 2);
			break;
		case 2:
			gl.glDrawArrays(GL3.GL_LINE_STRIP, 0, selectedData.length / 2);
			break;
		case 3:
			gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, selectedData.length / 2);
			break;
		case 4:
			gl.glDrawArrays(GL3.GL_TRIANGLES, 0, selectedData.length / 2);
			break;
		case 5:
			gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, selectedData.length / 2);
			break;
		case 6:
			gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, selectedData.length / 2);
			break;
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {	}

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
	public void tableChanged(TableModelEvent e) {
		vertexData = model.getAllData();
		selectedData = model.getSelectedData(table.getSelectedRows());
		repaint();
	}

	/**
	 * Processes a state changed event by setting the scale value based on the slider value.
	 *
	 * @param e The state changed event.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source instanceof ColorSelectionModel) {
			Color c = ((ColorSelectionModel) source).getSelectedColor();
			switch (currentColor) {
			case 0:
				backgroundColor = c;
				backgroundButton.setBackground(c);
				break;
			case 1:
				foregroundColor = c;
				foregroundButton.setBackground(c);
				break;
			case 2:
				selectionColor = c;
				selectionButton.setBackground(c);
				break;
			}
			repaint();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		selectedData = model.getSelectedData(table.getSelectedRows());
		repaint();
	}

}
