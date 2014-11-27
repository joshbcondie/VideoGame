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

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * JOGL 2.0 Program Template (GLCanvas) This is a "Component" which can be added
 * into a top-level "Container". It also handles the OpenGL events to render
 * graphics.
 */
@SuppressWarnings("serial")
public class SimpleScene extends GLCanvas implements GLEventListener,
		java.awt.event.KeyListener {

	// Define constants for the top-level container
	private static String TITLE = "Video Game"; // window's title
	private static final int CANVAS_WIDTH = 640; // width of the drawable
	private static final int CANVAS_HEIGHT = 480; // height of the drawable

	private static final float SHIP_ROTATION = 0.02f;
	private static final float SHIP_SPEED = 0.1f;

	private static ObjModel parkingLotModel = null;
	private static ObjModel arwingModel = null;

	private static Texture arwingTexture = null;
	private static Texture parkingLotTexture = null;

	private static Vector3f arwingPosition = new Vector3f(0, 3, 5);
	private static Vector3f arwingRotation = new Vector3f(0, 0, 0);

	private static boolean arwingLeft = false;
	private static boolean arwingRight = false;
	private static boolean arwingUp = false;
	private static boolean arwingDown = false;

	/** The entry main() method to setup the top-level container and animator */
	public static void main(String[] args) {
		// Run the GUI codes in the event-dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Create the OpenGL rendering canvas
				GLCanvas canvas = new SimpleScene();
				canvas.setPreferredSize(new Dimension(CANVAS_WIDTH,
						CANVAS_HEIGHT));

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
	public SimpleScene() {
		this.addGLEventListener(this);
		this.addKeyListener(this);
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
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
		gl.glClearDepth(1.0f); // set clear depth value to farthest
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best
																// perspective
																// correction
		gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out
									// lighting
		try {
			arwingTexture = TextureIO.newTexture(new File("arwing.jpg"), false);
			parkingLotTexture = TextureIO.newTexture(
					new File("ParkingLot.jpg"), false);
		} catch (GLException | IOException e) {
			e.printStackTrace();
		}
		gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		try {
			parkingLotModel = new ObjModel(new File("ParkingLot.obj"));
			arwingModel = new ObjModel(new File("arwing.obj"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
		glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear,
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

		gl.glTranslatef(0, -1, -10);
		gl.glRotatef((float) (-arwingRotation.getX() * 180 / Math.PI), 1, 0, 0);
		gl.glRotatef((float) (-arwingRotation.getY() * 180 / Math.PI), 0, 1, 0);
		gl.glTranslatef(-arwingPosition.getX(), -arwingPosition.getY(),
				-arwingPosition.getZ());
		gl.glBindTexture(GL_TEXTURE_2D, parkingLotTexture.getTextureObject());
		parkingLotModel.render(gl);

		gl.glTranslatef(arwingPosition.getX(), arwingPosition.getY(),
				arwingPosition.getZ());
		gl.glRotatef((float) (arwingRotation.getX() * 180 / Math.PI), 1, 0, 0);
		gl.glRotatef((float) (arwingRotation.getY() * 180 / Math.PI), 0, 1, 0);
		gl.glScalef(0.01f, 0.01f, -0.01f);
		gl.glBindTexture(GL_TEXTURE_2D, arwingTexture.getTextureObject());
		arwingModel.render(gl);
	}

	private void update() {

		arwingPosition.setX(arwingPosition.getX() - SHIP_SPEED
				* (float) Math.sin(arwingRotation.getY()));
		arwingPosition.setZ(arwingPosition.getZ() - SHIP_SPEED
				* (float) Math.cos(arwingRotation.getY()));

		if (arwingLeft) {
			arwingRotation.setY(arwingRotation.getY() + SHIP_ROTATION);
		}
		if (arwingRight) {
			arwingRotation.setY(arwingRotation.getY() - SHIP_ROTATION);
		}
		if (arwingDown) {
			arwingRotation.setX(arwingRotation.getX() - SHIP_ROTATION);
		}
		if (arwingUp) {
			arwingRotation.setX(arwingRotation.getX() + SHIP_ROTATION);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		switch (e.getKeyCode()) {
		case java.awt.event.KeyEvent.VK_LEFT:
			arwingLeft = true;
			break;
		case java.awt.event.KeyEvent.VK_RIGHT:
			arwingRight = true;
			break;
		case java.awt.event.KeyEvent.VK_DOWN:
			arwingDown = true;
			break;
		case java.awt.event.KeyEvent.VK_UP:
			arwingUp = true;
			break;
		}
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		switch (e.getKeyCode()) {
		case java.awt.event.KeyEvent.VK_LEFT:
			arwingLeft = false;
			break;
		case java.awt.event.KeyEvent.VK_RIGHT:
			arwingRight = false;
			break;
		case java.awt.event.KeyEvent.VK_DOWN:
			arwingDown = false;
			break;
		case java.awt.event.KeyEvent.VK_UP:
			arwingUp = false;
			break;
		}
	}
}