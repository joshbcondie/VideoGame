import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2ES1.*;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
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
	private static String TITLE = "Transformations"; // window's title
	private static final int CANVAS_WIDTH = 640; // width of the drawable
	private static final int CANVAS_HEIGHT = 480; // height of the drawable
	private static final float CAMERA_ROTATION_AMOUNT = 0.02f;
	private static final float CAMERA_SPEED = 0.1f;
	private static final float TIRE_ROTATION_AMOUNT = 0.05f;
	private static final float CAR_SPEED = 0.05f;
	private static final float BACKWARD_SPEED = 0.005f;

	private static ObjModel carModel = null;
	private static ObjModel tireModel = null;
	private static ObjModel parkingLotModel = null;
	private static ObjModel penguinModel = null;

	private static Texture carTexture = null;
	private static Texture tireTexture = null;
	private static Texture parkingLotTexture = null;

	private static Vector3f cameraPosition = new Vector3f(0, 1, 5);
	private static Vector3f cameraRotation = new Vector3f(0, 0, 0);
	private static float tireOrientation = 0.001f;
	private static float tireRotation = 0;

	private static Vector3f carPosition = new Vector3f(-2.5f, 0.1f, -7.5f);
	private static float carRotation = (float) (55 * Math.PI / 180);
	// private static Vector3f carPosition = new Vector3f(0, 0, 0);
	// private static float carRotation = 0;

	private static boolean cameraLeft = false;
	private static boolean cameraRight = false;
	private static boolean cameraUp = false;
	private static boolean cameraDown = false;

	private static boolean rotateLeft = false;
	private static boolean rotateRight = false;
	private static boolean rotateUp = false;
	private static boolean rotateDown = false;

	private static boolean tireLeft = false;
	private static boolean tireRight = false;

	private static boolean carForward = false;
	private static boolean carBackward = false;

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
			carTexture = TextureIO.newTexture(new File("car.jpg"), false);
			tireTexture = TextureIO.newTexture(new File("tire.jpg"), false);
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
			carModel = new ObjModel(new File("car.obj"));
			tireModel = new ObjModel(new File("tire.obj"));
			parkingLotModel = new ObjModel(new File("ParkingLot.obj"));
			penguinModel = new ObjModel(new File("Penguin.obj"));
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
		Matrix viewMatrix = new Matrix(4, 4);
		viewMatrix.loadIdentity(); // reset the model-view matrix

		viewMatrix.rotateX(-cameraRotation.getX());
		viewMatrix.rotateY(-cameraRotation.getY());
		viewMatrix.translate(-cameraPosition.getX(), -cameraPosition.getY(),
				-cameraPosition.getZ());

		gl.glBindTexture(GL_TEXTURE_2D, parkingLotTexture.getTextureObject());
		parkingLotModel.render(gl, viewMatrix);

		viewMatrix.translate(carPosition.getX(), carPosition.getY(),
				carPosition.getZ());
		viewMatrix.rotateY(carRotation);
		gl.glBindTexture(GL_TEXTURE_2D, carTexture.getTextureObject());
		carModel.render(gl, viewMatrix);

		viewMatrix.translate(0, 0.9f, 0);
		viewMatrix.scale(0.1f, 0.1f, 0.1f);
		gl.glBindTexture(GL_TEXTURE_2D, tireTexture.getTextureObject());
		penguinModel.render(gl, viewMatrix);
		viewMatrix.scale(10f, 10f, 10f);
		viewMatrix.translate(0, -0.9f, 0);

		viewMatrix.translate(0.36f, 0.12f, -0.54f);
		viewMatrix.scale(0.25f, 0.25f, 0.25f);
		viewMatrix.rotateY(tireOrientation);
		viewMatrix.rotateX(tireRotation);
		gl.glBindTexture(GL_TEXTURE_2D, tireTexture.getTextureObject());
		tireModel.render(gl, viewMatrix);
		viewMatrix.rotateX(-tireRotation);
		viewMatrix.rotateY(-tireOrientation);

		viewMatrix.translate(0, 0, 4.1f);
		viewMatrix.rotateX(tireRotation);
		tireModel.render(gl, viewMatrix);
		viewMatrix.rotateX(-tireRotation);

		viewMatrix.translate(-2.9f, 0, 0);
		viewMatrix.rotateY((float) Math.PI);
		viewMatrix.rotateX(-tireRotation);
		tireModel.render(gl, viewMatrix);
		viewMatrix.rotateX(tireRotation);

		viewMatrix.translate(0, 0, 4.1f);
		viewMatrix.rotateY(tireOrientation);
		viewMatrix.rotateX(-tireRotation);
		tireModel.render(gl, viewMatrix);
	}

	private void update() {

		if (cameraLeft) {
			cameraPosition.setX(cameraPosition.getX() - CAMERA_SPEED
					* (float) Math.cos(cameraRotation.getY()));
			cameraPosition.setZ(cameraPosition.getZ() + CAMERA_SPEED
					* (float) Math.sin(cameraRotation.getY()));
		}
		if (cameraRight) {
			cameraPosition.setX(cameraPosition.getX() + CAMERA_SPEED
					* (float) Math.cos(cameraRotation.getY()));
			cameraPosition.setZ(cameraPosition.getZ() - CAMERA_SPEED
					* (float) Math.sin(cameraRotation.getY()));
		}
		if (cameraDown) {
			cameraPosition.setX(cameraPosition.getX() + CAMERA_SPEED
					* (float) Math.sin(cameraRotation.getY()));
			cameraPosition.setZ(cameraPosition.getZ() + CAMERA_SPEED
					* (float) Math.cos(cameraRotation.getY()));
		}
		if (cameraUp) {
			cameraPosition.setX(cameraPosition.getX() - CAMERA_SPEED
					* (float) Math.sin(cameraRotation.getY()));
			cameraPosition.setZ(cameraPosition.getZ() - CAMERA_SPEED
					* (float) Math.cos(cameraRotation.getY()));
		}

		if (rotateLeft) {
			cameraRotation.setY(cameraRotation.getY() + CAMERA_ROTATION_AMOUNT);
		}
		if (rotateRight) {
			cameraRotation.setY(cameraRotation.getY() - CAMERA_ROTATION_AMOUNT);
		}
		if (rotateDown) {
			cameraRotation.setX(cameraRotation.getX() - CAMERA_ROTATION_AMOUNT);
		}
		if (rotateUp) {
			cameraRotation.setX(cameraRotation.getX() + CAMERA_ROTATION_AMOUNT);
		}

		if (tireLeft && tireOrientation < Math.PI / 4) {
			tireOrientation += TIRE_ROTATION_AMOUNT;
		}
		if (tireRight && tireOrientation > -Math.PI / 4) {
			tireOrientation -= TIRE_ROTATION_AMOUNT;
		}

		if (carForward) {

			// Form a triangle to find out which point to rotate about
			float z = 3.56f;
			float x = 1000;
			x = (float) -(4.64 / Math.tan(tireOrientation));

			Matrix carTransformation = new Matrix(4, 4);
			carTransformation.loadIdentity();

			// Put car in the origin
			carTransformation.translate(carPosition.getX(), carPosition.getY(),
					carPosition.getZ());
			carTransformation.rotateY(carRotation);

			// Rotate around the point
			carTransformation.translate(x, 0, z);
			carTransformation.rotateY(tireOrientation * CAR_SPEED);
			carTransformation.translate(-x, 0, -z);

			// Undo transformations
			carTransformation.rotateY(-carRotation);
			carTransformation.translate(-carPosition.getX(),
					-carPosition.getY(), -carPosition.getZ());

			// Move car
			carPosition = carTransformation.multiply(new Matrix(carPosition))
					.toVector3f();
			carRotation += tireOrientation * CAR_SPEED;
			tireOrientation -= tireOrientation * CAR_SPEED;

			// Rotate tire
			tireRotation -= CAR_SPEED;
		}

		if (carBackward) {

			// Form a triangle to find out which point to rotate about
			float z = 3.56f;
			float x = 1000;
			x = (float) -(4.64 / Math.tan(tireOrientation));

			Matrix carTransformation = new Matrix(4, 4);
			carTransformation.loadIdentity();

			// Put car in the origin
			carTransformation.translate(carPosition.getX(), carPosition.getY(),
					carPosition.getZ());
			carTransformation.rotateY(carRotation);

			// Rotate around the point
			carTransformation.translate(x, 0, z);
			carTransformation.rotateY(-tireOrientation * BACKWARD_SPEED);
			carTransformation.translate(-x, 0, -z);

			// Undo transformations
			carTransformation.rotateY(-carRotation);
			carTransformation.translate(-carPosition.getX(),
					-carPosition.getY(), -carPosition.getZ());

			// Move car
			carPosition = carTransformation.multiply(new Matrix(carPosition))
					.toVector3f();
			carRotation -= tireOrientation * BACKWARD_SPEED;
			if (tireOrientation + tireOrientation * BACKWARD_SPEED < Math.PI / 4
					&& tireOrientation + tireOrientation * BACKWARD_SPEED > -Math.PI / 4)
				tireOrientation += tireOrientation * BACKWARD_SPEED;

			// Rotate tire
			tireRotation += CAR_SPEED;
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
			cameraLeft = true;
			break;
		case java.awt.event.KeyEvent.VK_RIGHT:
			cameraRight = true;
			break;
		case java.awt.event.KeyEvent.VK_DOWN:
			cameraDown = true;
			break;
		case java.awt.event.KeyEvent.VK_UP:
			cameraUp = true;
			break;
		case java.awt.event.KeyEvent.VK_A:
			rotateLeft = true;
			break;
		case java.awt.event.KeyEvent.VK_D:
			rotateRight = true;
			break;
		case java.awt.event.KeyEvent.VK_S:
			rotateUp = true;
			break;
		case java.awt.event.KeyEvent.VK_W:
			rotateDown = true;
			break;
		case java.awt.event.KeyEvent.VK_T:
			tireLeft = true;
			break;
		case java.awt.event.KeyEvent.VK_Y:
			tireRight = true;
			break;
		case java.awt.event.KeyEvent.VK_SPACE:
			carForward = true;
			break;
		case java.awt.event.KeyEvent.VK_B:
			carBackward = true;
			break;
		}
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		switch (e.getKeyCode()) {
		case java.awt.event.KeyEvent.VK_LEFT:
			cameraLeft = false;
			break;
		case java.awt.event.KeyEvent.VK_RIGHT:
			cameraRight = false;
			break;
		case java.awt.event.KeyEvent.VK_DOWN:
			cameraDown = false;
			break;
		case java.awt.event.KeyEvent.VK_UP:
			cameraUp = false;
			break;
		case java.awt.event.KeyEvent.VK_A:
			rotateLeft = false;
			break;
		case java.awt.event.KeyEvent.VK_D:
			rotateRight = false;
			break;
		case java.awt.event.KeyEvent.VK_S:
			rotateUp = false;
			break;
		case java.awt.event.KeyEvent.VK_W:
			rotateDown = false;
			break;
		case java.awt.event.KeyEvent.VK_T:
			tireLeft = false;
			break;
		case java.awt.event.KeyEvent.VK_Y:
			tireRight = false;
			break;
		case java.awt.event.KeyEvent.VK_SPACE:
			carForward = false;
			break;
		case java.awt.event.KeyEvent.VK_B:
			carBackward = false;
			break;
		}
	}
}