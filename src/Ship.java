import static javax.media.opengl.GL.GL_TEXTURE_2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Ship {

	private static final float XY_ROTATION_SPEED = 0.02f;
	private static final float Z_ROTATION_SPEED = 0.02f;
	private static Texture texture;
	private static ObjModel model;

	private boolean isAlive;
	private float speed;
	private Vector3f position;
	private Vector3f xAxis;
	private Vector3f yAxis;

	public Ship() {
		isAlive = true;
		try {
			if (texture == null)
				texture = TextureIO.newTexture(new File("ship.jpg"), false);
		} catch (GLException | IOException e) {
			e.printStackTrace();
		}
		try {
			if (model == null)
				model = new ObjModel(new File("ship.obj"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		speed = 2f;
		position = new Vector3f(500, 50, 500);
		xAxis = new Vector3f(1, 0, 0);
		yAxis = new Vector3f(0, 1, 0);
	}

	public void render(GL2 gl) {
		gl.glBindTexture(GL_TEXTURE_2D, texture.getTextureObject());
		model.render(gl);
	}

	public void moveForward() {
		position.setX(position.getX() + speed
				* xAxis.crossProduct(yAxis).normalize().getX());
		position.setY(position.getY() + speed
				* xAxis.crossProduct(yAxis).normalize().getY());
		position.setZ(position.getZ() + speed
				* xAxis.crossProduct(yAxis).normalize().getZ());
	}

	public void rotateX(float amount) {
		Matrix matrix = new Matrix(4, 4);
		matrix.loadIdentity();
		matrix.rotateAbout(yAxis, -XY_ROTATION_SPEED * amount);
		xAxis = matrix.multiply(new Matrix(xAxis)).toVector3f().normalize();
	}

	public void rotateY(float amount) {
		Matrix matrix = new Matrix(4, 4);
		matrix.loadIdentity();
		matrix.rotateAbout(xAxis, XY_ROTATION_SPEED * amount);
		yAxis = matrix.multiply(new Matrix(yAxis)).toVector3f().normalize();
	}

	public void rotateZ(float amount) {
		Matrix matrix = new Matrix(4, 4);
		matrix.loadIdentity();
		matrix.rotateAbout(xAxis.crossProduct(yAxis).normalize(),
				-Z_ROTATION_SPEED * amount);
		xAxis = matrix.multiply(new Matrix(xAxis)).toVector3f().normalize();
		yAxis = matrix.multiply(new Matrix(yAxis)).toVector3f().normalize();
	}

	public Matrix reverseChangeOfBasis() {
		return Matrix.reverseChangeOfBasis(xAxis, yAxis,
				xAxis.crossProduct(yAxis));
	}

	public Matrix changeOfBasis() {
		return Matrix.changeOfBasis(xAxis, yAxis, xAxis.crossProduct(yAxis));
	}

	public float getX() {
		return position.getX();
	}

	public void setX(float x) {
		position.setX(x);
	}

	public float getY() {
		return position.getY();
	}

	public void setY(float y) {
		position.setY(y);
	}

	public float getZ() {
		return position.getZ();
	}

	public void setZ(float z) {
		position.setZ(z);
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void die() {
		isAlive = false;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setXAxis(Vector3f xAxis) {
		this.xAxis = xAxis;
	}

	public void setYAxis(Vector3f yAxis) {
		this.yAxis = yAxis;
	}
}
