import static javax.media.opengl.GL.GL_TEXTURE_2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Ship extends FlyingObject {

	private static final float XY_ROTATION_SPEED = 0.02f;
	private static final float Z_ROTATION_SPEED = 0.02f;
	private static Texture texture;
	private static ObjModel model;

	private List<Laser> lasers;

	public Ship() {
		super();
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
		lasers = new ArrayList<>();
	}

	public void shoot() {
		Laser laser = new Laser();
		laser.setPosition(position.scale(1));
		laser.setXAxis(xAxis.scale(1));
		laser.setYAxis(yAxis.scale(1));
		laser.setSpeed(speed + 2);
		lasers.add(laser);
	}

	public void moveLasers() {
		for (Laser laser : lasers) {
			laser.moveForward();
		}
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

	public void render(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(getX(), getY(), getZ());
		gl.glScalef(0.01f, 0.01f, 0.01f);
		gl.glMultMatrixf(changeOfBasis().toArray(), 0);
		gl.glBindTexture(GL_TEXTURE_2D, texture.getTextureObject());
		model.render(gl);
		gl.glPopMatrix();
	}

	public void renderLasers(GL2 gl) {
		for (Laser laser : lasers) {
			laser.render(gl);
		}
	}
}
