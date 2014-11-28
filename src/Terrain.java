import java.util.ArrayList;

public class Terrain extends ObjModel {

	public Terrain(float length, int recursionLevel) {

		vertices = new ArrayList<>();
		vertices.add(new Vector3f(0, 0, 0));
		vertices.add(new Vector3f(0, 0, length));
		vertices.add(new Vector3f(length, 0, length));
		vertices.add(new Vector3f(length, 0, 0));

		textureCoordinates = new ArrayList<>();
		textureCoordinates.add(new Vector3f(0, 0));
		textureCoordinates.add(new Vector3f(0, 1));
		textureCoordinates.add(new Vector3f(1, 1));
		textureCoordinates.add(new Vector3f(1, 0));

		faces = new ArrayList<>();
		Face face = new Face();
		face.addVertex(1);
		face.addVertex(2);
		face.addVertex(3);
		face.addVertex(4);
		face.addTextureCoordinate(1);
		face.addTextureCoordinate(2);
		face.addTextureCoordinate(3);
		face.addTextureCoordinate(4);

		divideFace(face, recursionLevel);
	}

	private void divideFace(Face face, int recursionLevel) {
		if (recursionLevel > 0) {
			float x = 0;
			float y = 0;
			float z = 0;
			for (int j = 0; j < face.getVertices().size(); j++) {
				x += vertices.get(face.getVertices().get(j) - 1).getX();
				y += vertices.get(face.getVertices().get(j) - 1).getY();
				z += vertices.get(face.getVertices().get(j) - 1).getZ();
			}
			x /= 4f;
			// x += (float) (Math.random() * 10 - 5);
			y /= 4f;
//			y += (float) (Math.random() * 5 - 2.5);
			z /= 4f;
			// z += (float) (Math.random() * 10 - 5);
			vertices.add(new Vector3f(vertices.get(
					face.getVertices().get(0) - 1).getX(), (vertices.get(
					face.getVertices().get(0) - 1).getY() + vertices.get(
					face.getVertices().get(1) - 1).getY()) / 2, z));
			vertices.add(new Vector3f(x, (vertices.get(
					face.getVertices().get(1) - 1).getY() + vertices.get(
					face.getVertices().get(2) - 1).getY()) / 2, vertices.get(
					face.getVertices().get(1) - 1).getZ()));
			vertices.add(new Vector3f(vertices.get(
					face.getVertices().get(2) - 1).getX(), (vertices.get(
					face.getVertices().get(2) - 1).getY() + vertices.get(
					face.getVertices().get(3) - 1).getY()) / 2, z));
			vertices.add(new Vector3f(x, (vertices.get(
					face.getVertices().get(3) - 1).getY() + vertices.get(
					face.getVertices().get(0) - 1).getY()) / 2, vertices.get(
					face.getVertices().get(3) - 1).getZ()));
			vertices.add(new Vector3f(x, y, z));

			Face f1 = new Face();
			f1.addVertex(face.getVertices().get(0));
			f1.addVertex(vertices.size() - 4);
			f1.addVertex(vertices.size());
			f1.addVertex(vertices.size() - 1);
			for (int i = 0; i < 4; i++) {
				f1.addTextureCoordinate(i + 1);
			}
			divideFace(f1, recursionLevel - 1);

			Face f2 = new Face();
			f2.addVertex(vertices.size() - 4);
			f2.addVertex(face.getVertices().get(1));
			f2.addVertex(vertices.size() - 3);
			f2.addVertex(vertices.size());
			for (int i = 0; i < 4; i++) {
				f2.addTextureCoordinate(i + 1);
			}
			divideFace(f2, recursionLevel - 1);

			Face f3 = new Face();
			f3.addVertex(vertices.size());
			f3.addVertex(vertices.size() - 3);
			f3.addVertex(face.getVertices().get(2));
			f3.addVertex(vertices.size() - 2);
			for (int i = 0; i < 4; i++) {
				f3.addTextureCoordinate(i + 1);
			}
			divideFace(f3, recursionLevel - 1);

			Face f4 = new Face();
			f4.addVertex(vertices.size() - 1);
			f4.addVertex(vertices.size());
			f4.addVertex(vertices.size() - 2);
			f4.addVertex(face.getVertices().get(3));
			for (int i = 0; i < 4; i++) {
				f4.addTextureCoordinate(i + 1);
			}
			divideFace(f4, recursionLevel - 1);
		} else {
			faces.add(face);
		}
	}
}
