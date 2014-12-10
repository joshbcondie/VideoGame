package game;

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
import game.math.Vector3f;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		KeyListener, MouseListener {

	// Define constants for the top-level container
	private static String TITLE = "Video Game"; // window's title
	private static final int CANVAS_WIDTH = 640; // width of the drawable
	private static final int CANVAS_HEIGHT = 480; // height of the drawable

	private static double lastMouseX;
	private static double lastMouseY;
	private static boolean rotatePositiveZ = false;
	private static boolean rotateNegativeZ = false;

	private static Ship ship;
	private static List<Ship> ships = new ArrayList<Ship>();

	private static Terrain terrain;

	private static final Random random = new Random();

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
				frame.setExtendedState(frame.getExtendedState()
						| JFrame.MAXIMIZED_BOTH);
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

		terrain = new Terrain(1000, 4, 30);

		ship = new Ship(terrain, ships, 0);
		ship.setSpeed(2);
		ships.add(ship);
		for (int i = 0; i < 50; i++) {
			Ship enemy = new Ship(terrain, ships, i + 1);
			enemy.setSpeed(random.nextFloat() * 4);
			enemy.setPosition(new Vector3f(random.nextFloat() * 1000, random
					.nextFloat() * 100, random.nextFloat() * 1000));
			enemy.setXAxis(new Vector3f(random.nextFloat() - 0.5f, random
					.nextFloat() - 0.5f, random.nextFloat() - 0.5f).normalize());
			enemy.setYAxis(new Vector3f(random.nextFloat() - 0.5f, random
					.nextFloat() - 0.5f, random.nextFloat() - 0.5f).normalize());
			ships.add(enemy);
		}

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

		for (Ship ship : ships) {
			if (ship.isAlive()) {
				ship.render(gl);
			}
			ship.renderLasers(gl);
		}
	}

	private void update() {

		if (ship.isAlive()) {
			ship.update();

			double currentMouseX = MouseInfo.getPointerInfo().getLocation()
					.getX();
			double currentMouseY = MouseInfo.getPointerInfo().getLocation()
					.getY();
			ship.rotateX((float) (currentMouseX - lastMouseX));
			ship.rotateY((float) (currentMouseY - lastMouseY));

			Window window = SwingUtilities.getWindowAncestor(this);
			if (currentMouseX < 20 || window.getWidth() - currentMouseX < 20
					|| currentMouseY < 20
					|| window.getHeight() - currentMouseY < 20) {
				try {
					new Robot().mouseMove(
							window.getX() + window.getWidth() / 2,
							window.getY() + window.getHeight() / 2);
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
			lastMouseX = MouseInfo.getPointerInfo().getLocation().getX();
			lastMouseY = MouseInfo.getPointerInfo().getLocation().getY();

			if (rotatePositiveZ && !rotateNegativeZ) {
				ship.rotateZ(1);
			} else if (rotateNegativeZ && !rotatePositiveZ) {
				ship.rotateZ(-1);
			}
		}
		ship.updateLasers();
		for (int i = 1; i < ships.size(); i++) {
			Ship enemy = ships.get(i);
			if (enemy.isAlive()) {
				enemy.update();
				if (random.nextDouble() < 0.03)
					enemy.shoot();
			}
			enemy.updateLasers();
		}
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
	public void mousePressed(MouseEvent e) {
		if (ship.isAlive())
			ship.shoot();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}