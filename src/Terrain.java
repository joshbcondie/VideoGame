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
			y += (float) (Math.random() * 5 - 2.5);
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

			textureCoordinates
					.add(new Vector3f(textureCoordinates.get(
							face.getTextureCoordinates().get(0) - 1).getX(),
							(textureCoordinates.get(
									face.getTextureCoordinates().get(0) - 1)
									.getY() + textureCoordinates.get(
									face.getTextureCoordinates().get(1) - 1)
									.getY()) / 2));
			textureCoordinates
					.add(new Vector3f(
							(textureCoordinates.get(
									face.getTextureCoordinates().get(1) - 1)
									.getX() + textureCoordinates.get(
									face.getTextureCoordinates().get(2) - 1)
									.getX()) / 2, textureCoordinates.get(
									face.getTextureCoordinates().get(1) - 1)
									.getY()));
			textureCoordinates
					.add(new Vector3f(textureCoordinates.get(
							face.getTextureCoordinates().get(2) - 1).getX(),
							(textureCoordinates.get(
									face.getTextureCoordinates().get(2) - 1)
									.getY() + textureCoordinates.get(
									face.getTextureCoordinates().get(3) - 1)
									.getY()) / 2));
			textureCoordinates
					.add(new Vector3f(
							(textureCoordinates.get(
									face.getTextureCoordinates().get(3) - 1)
									.getX() + textureCoordinates.get(
									face.getTextureCoordinates().get(0) - 1)
									.getX()) / 2, textureCoordinates.get(
									face.getTextureCoordinates().get(3) - 1)
									.getY()));

			textureCoordinates
					.add(new Vector3f(
							(textureCoordinates.get(
									face.getTextureCoordinates().get(0) - 1)
									.getX() + textureCoordinates.get(
									face.getTextureCoordinates().get(3) - 1)
									.getX()) / 2, (textureCoordinates.get(
									face.getTextureCoordinates().get(0) - 1)
									.getY() + textureCoordinates.get(
									face.getTextureCoordinates().get(1) - 1)
									.getY()) / 2));

			int currentSize = vertices.size();

			Face f1 = new Face();
			f1.addVertex(face.getVertices().get(0));
			f1.addVertex(currentSize - 4);
			f1.addVertex(currentSize);
			f1.addVertex(currentSize - 1);
			f1.addTextureCoordinate(face.getTextureCoordinates().get(0));
			f1.addTextureCoordinate(currentSize - 4);
			f1.addTextureCoordinate(currentSize);
			f1.addTextureCoordinate(currentSize - 1);
			divideFace(f1, recursionLevel - 1);

			Face f2 = new Face();
			f2.addVertex(currentSize - 4);
			f2.addVertex(face.getVertices().get(1));
			f2.addVertex(currentSize - 3);
			f2.addVertex(currentSize);
			f2.addTextureCoordinate(currentSize - 4);
			f2.addTextureCoordinate(face.getTextureCoordinates().get(1));
			f2.addTextureCoordinate(currentSize - 3);
			f2.addTextureCoordinate(currentSize);
			divideFace(f2, recursionLevel - 1);

			Face f3 = new Face();
			f3.addVertex(currentSize);
			f3.addVertex(currentSize - 3);
			f3.addVertex(face.getVertices().get(2));
			f3.addVertex(currentSize - 2);
			f3.addTextureCoordinate(currentSize);
			f3.addTextureCoordinate(currentSize - 3);
			f3.addTextureCoordinate(face.getTextureCoordinates().get(2));
			f3.addTextureCoordinate(currentSize - 2);
			divideFace(f3, recursionLevel - 1);

			Face f4 = new Face();
			f4.addVertex(currentSize - 1);
			f4.addVertex(currentSize);
			f4.addVertex(currentSize - 2);
			f4.addVertex(face.getVertices().get(3));
			f4.addTextureCoordinate(currentSize - 1);
			f4.addTextureCoordinate(currentSize);
			f4.addTextureCoordinate(currentSize - 2);
			f4.addTextureCoordinate(face.getTextureCoordinates().get(3));
			divideFace(f4, recursionLevel - 1);
		} else {
			faces.add(face);
		}
	}
}
