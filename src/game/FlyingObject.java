package game;

import game.math.Matrix;
import game.math.Vector3f;

public abstract class FlyingObject {

	protected boolean isAlive;
	protected float speed;
	protected Vector3f position;
	protected Vector3f xAxis;
	protected Vector3f yAxis;
	protected Terrain terrain;

	public FlyingObject(Terrain terrain) {
		isAlive = true;
		speed = 2f;
		position = new Vector3f(500, 50, 500);
		xAxis = new Vector3f(1, 0, 0);
		yAxis = new Vector3f(0, 1, 0);
		this.terrain = terrain;
	}

	public void update() {
		if (getY() <= terrain.getHeight(getX(), getZ())) {
			die();
			return;
		}
		position.setX(position.getX() + speed
				* xAxis.crossProduct(yAxis).normalize().getX());
		position.setY(position.getY() + speed
				* xAxis.crossProduct(yAxis).normalize().getY());
		position.setZ(position.getZ() + speed
				* xAxis.crossProduct(yAxis).normalize().getZ());
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
