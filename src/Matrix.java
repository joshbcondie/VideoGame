public class Matrix {

	private float[][] values;

	public Matrix multiply(Matrix b) {
		Matrix result = new Matrix(b.values.length, values[0].length);
		for (int i = 0; i < b.values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				float sum = 0;
				for (int k = 0; k < values.length; k++) {
					sum += values[k][j] * b.values[i][k];
				}
				result.values[i][j] = sum;
			}
		}
		return result;
	}

	public Matrix(int columns, int rows) {
		values = new float[columns][rows];
	}

	public Matrix(Vector3f vector) {
		values = new float[][] { { vector.getX(), vector.getY(), vector.getZ(),
				1 } };
	}

	public void loadIdentity() {
		for (int i = 0; i < values.length; i++) {
			values[i][i] = 1;
		}
	}

	public void translate(float x, float y, float z) {
		Matrix translation = new Matrix(4, 4);
		translation.loadIdentity();
		translation.values[3][0] = x;
		translation.values[3][1] = y;
		translation.values[3][2] = z;
		values = multiply(translation).values;
	}

	public void scale(float x, float y, float z) {
		Matrix scale = new Matrix(4, 4);
		scale.loadIdentity();
		scale.values[0][0] = x;
		scale.values[1][1] = y;
		scale.values[2][2] = z;
		values = multiply(scale).values;
	}

	public void rotateX(float radians) {
		Matrix rotation = new Matrix(4, 4);
		rotation.loadIdentity();
		rotation.values[1][1] = (float) Math.cos(radians);
		rotation.values[2][1] = -(float) Math.sin(radians);
		rotation.values[1][2] = (float) Math.sin(radians);
		rotation.values[2][2] = (float) Math.cos(radians);
		values = multiply(rotation).values;
	}

	public void rotateY(float radians) {
		Matrix rotation = new Matrix(4, 4);
		rotation.loadIdentity();
		rotation.values[0][0] = (float) Math.cos(radians);
		rotation.values[2][0] = (float) Math.sin(radians);
		rotation.values[0][2] = -(float) Math.sin(radians);
		rotation.values[2][2] = (float) Math.cos(radians);
		values = multiply(rotation).values;
	}

	public Vector3f toVector3f() {
		return new Vector3f(values[0][0] / values[0][3], values[0][1]
				/ values[0][3], values[0][2] / values[0][3]);
	}
}
