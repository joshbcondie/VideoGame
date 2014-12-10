import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL2ES1.GL_DECAL;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL2ES1.GL_TEXTURE_ENV;
import static javax.media.opengl.GL2ES1.GL_TEXTURE_ENV_MODE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * JOGL 2.0 Program Template (GLCanvas) This is a "Component" which can be added
 * into a top-level "Container". It also handles the OpenGL events to render
 * graphics.
 */
@SuppressWarnings("serial")
public class VideoGame extends GLCanvas implements GLEventListener,
		KeyListener, MouseListener, MouseMotionListener {

	// Define constants for the top-level container
	private static String TITLE = "Video Game"; // window's title
	private static final int CANVAS_WIDTH = 640; // width of the drawable
	private static final int CANVAS_HEIGHT = 480; // height of the drawable

	private static int mouseMovementX = 0;
	private static int mouseMovementY = 0;
	private static boolean rotatePositiveZ = false;
	private static boolean rotateNegativeZ = false;

	private static Ship ship;
	private static List<Ship> enemies = new ArrayList<Ship>();

	private static Terrain terrain;

	/** The entry main() method to setup the top-level container and animator */
	public static void main(String[] args) {
		// Run the GUI codes in the event-dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Create the OpenGL rendering canvas
				GLCanvas canvas = new VideoGame();
				canvas.setPreferredSize(new Dimension(CANVAS_WIDTH,
						CANVAS_HEIGHT));
				canvas.setCursor(Toolkit.getDefaultToolkit()
						.createCustomCursor(
								new BufferedImage(1, 1,
										BufferedImage.TYPE_INT_ARGB),
								new Point(0, 0), "none"));

				// Create the top-level container
				final JFrame frame = new JFrame(); // Swing's JFrame or AWT's
													// Frame
				frame.getContentPane().add(canvas);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						// Use a dedicate thread to run the stop() to ensure
						// that the
						// animator stops before program exits.
						new Thread() {
							@Override
							public void run() {
								System.exit(0);
							}
						}.start();
					}
				});
				frame.setTitle(TITLE);
				frame.pack();
				frame.setVisible(true);

				new FPSAnimator(canvas, 60);
				canvas.getAnimator().start();
			}
		});
	}

	// Setup OpenGL Graphics Renderer

	private GLU glu; // for the GL Utility

	/** Constructor to setup the GUI for this Component */
	public VideoGame() {
		this.addGLEventListener(this);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	// ------ Implement methods declared in GLEventListener ------

	/**
	 * Called back immediately after the OpenGL context is initialized. Can be
	 * used to perform one-time initialization. Run only once.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context
		glu = new GLU(); // get GL Utilities
		gl.glClearColor(0.53f, 0.81f, 0.92f, 0.0f); // set background (clear)
													// color
		gl.glClearDepth(1.0f); // set clear depth value to farthest
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best
																// perspective
																// correction
		gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out
									// lighting

		ship = new Ship();
		ship.setSpeed(0.1f);
		Ship enemy = new Ship();
		enemy.setSpeed(0);
		enemy.setPosition(new Vector3f(500, 100, 600));
		enemy.setXAxis(new Vector3f(1, 0, 0));
		enemy.setYAxis(new Vector3f(0, 0, 1));
		enemies.add(enemy);
		enemy = new Ship();
		enemy.setSpeed(0);
		enemy.setPosition(new Vector3f(500, 50, 700));
		enemy.setXAxis(new Vector3f(-1, 0, -1).normalize());
		enemy.setYAxis(new Vector3f(0, 1, 0));
		enemies.add(enemy);
		terrain = new Terrain(1000, 4, 30);

		gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glEnable(GL_TEXTURE_2D);
	}

	/**
	 * Call-back handler for window re-size event. Also called when the drawable
	 * is first set to visible.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context

		if (height == 0)
			height = 1; // prevent divide by zero
		float aspect = (float) width / height;

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matches viewport
		gl.glMatrixMode(GL_PROJECTION); // choose projection matrix
		gl.glLoadIdentity(); // reset projection matrix
		glu.gluPerspective(45.0, aspect, 0.1, 2000.0); // fovy, aspect, zNear,
														// zFar

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity(); // reset
	}

	/**
	 * Called back by the animator to perform rendering.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {

		update();

		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color
																// and depth
																// buffers
		gl.glLoadIdentity(); // reset the model-view matrix

		gl.glRotatef(180, 0, 1, 0);
		gl.glTranslatef(0, -5, 20);
		gl.glMultMatrixf(ship.reverseChangeOfBasis().toArray(), 0);
		gl.glTranslatef(-ship.getX(), -ship.getY(), -ship.getZ());

		terrain.render(gl);
		if (ship.isAlive()) {
			ship.render(gl);
		}
		for (Ship enemy : enemies) {
			if (enemy.isAlive()) {
				enemy.render(gl);
			}
		}
	}

	private void update() {

		if (ship.isAlive()) {

			if (ship.getY() <= terrain.getHeight(ship.getX(), ship.getZ())) {
				ship.die();
				return;
			}

			ship.moveForward();

			if (ship.getX() < 0)
				ship.setX(terrain.getLength() - 0.1f);
			else if (ship.getX() >= terrain.getLength())
				ship.setX(0);
			if (ship.getZ() < 0)
				ship.setZ(terrain.getLength() - 0.1f);
			else if (ship.getZ() >= terrain.getLength())
				ship.setZ(0);

			ship.rotateX(mouseMovementX);
			ship.rotateY(mouseMovementY);

			if (rotatePositiveZ && !rotateNegativeZ) {
				ship.rotateZ(1);
			} else if (rotateNegativeZ && !rotatePositiveZ) {
				ship.rotateZ(-1);
			}
		}
		for (Ship enemy : enemies) {
			if (enemy.isAlive()) {
				enemy.moveForward();
			}

			if (enemy.getX() < 0)
				enemy.setX(terrain.getLength() - 0.1f);
			else if (enemy.getX() >= terrain.getLength())
				enemy.setX(0);
			if (enemy.getZ() < 0)
				enemy.setZ(terrain.getLength() - 0.1f);
			else if (enemy.getZ() >= terrain.getLength())
				enemy.setZ(0);
		}
	}

	/**
	 * Called back before the OpenGL context is destroyed. Release resource such
	 * as buffers.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			rotatePositiveZ = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rotateNegativeZ = true;
		}
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			rotatePositiveZ = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rotateNegativeZ = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		try {
			Window window = SwingUtilities.getWindowAncestor(this);
			new Robot().mouseMove(window.getX() + window.getWidth() / 2,
					window.getY() + window.getHeight() / 2);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		try {
			Window window = SwingUtilities.getWindowAncestor(this);
			mouseMovementX = (int) (MouseInfo.getPointerInfo().getLocation()
					.getX() - (window.getX() + window.getWidth() / 2));
			mouseMovementY = (int) (MouseInfo.getPointerInfo().getLocation()
					.getY() - (window.getY() + window.getHeight() / 2));
			new Robot().mouseMove(window.getX() + window.getWidth() / 2,
					window.getY() + window.getHeight() / 2);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}
}