public class Vector3f {

	private float x;
	private float y;
	private float z;

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector3f normalize() {
		double length = Math.sqrt(x * x + y * y + z * z);
		return new Vector3f((float) (x / length), (float) (y / length),
				(float) (z / length));
	}

	public Vector3f crossProduct(Vector3f vector) {
		return new Vector3f(y * vector.z - z * vector.y, z * vector.x - x
				* vector.z, x * vector.y - y * vector.x);
	}

	public Vector3f scale(float scale) {
		return new Vector3f(x * scale, y * scale, z * scale);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
}
