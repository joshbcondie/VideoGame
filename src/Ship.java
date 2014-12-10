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

	public Ship(Terrain terrain) {
		super(terrain);
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
		Laser laser = new Laser(terrain);
		laser.setPosition(position.add(xAxis.crossProduct(yAxis).normalize()
				.scale(3)));
		laser.setXAxis(xAxis.scale(1));
		laser.setYAxis(yAxis.scale(1));
		laser.setSpeed(speed + 2);
		lasers.add(laser);
	}

	@Override
	public void update() {
		super.update();
		if (getX() < 0)
			setX(terrain.getLength() - 0.1f);
		else if (getX() >= terrain.getLength())
			setX(0);
		if (getZ() < 0)
			setZ(terrain.getLength() - 0.1f);
		else if (getZ() >= terrain.getLength())
			setZ(0);
	}

	public void updateLasers() {
		for (Laser laser : lasers) {
			if (laser.isAlive())
				laser.update();
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
			if (laser.isAlive())
				laser.render(gl);
		}
	}
}
