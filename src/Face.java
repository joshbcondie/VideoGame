import java.util.ArrayList;
import java.util.List;

public class Face {

	// indices of the vertices
	private List<Integer> vertices;
	// indices of the texture coordinates
	private List<Integer> textureCoordinates;

	public Face() {
		vertices = new ArrayList<>();
		textureCoordinates = new ArrayList<>();
	}

	public List<Integer> getVertices() {
		return vertices;
	}

	public void addVertex(int vertex) {
		vertices.add(vertex);
	}

	public List<Integer> getTextureCoordinates() {
		return textureCoordinates;
	}

	public void addTextureCoordinate(int textureCoordinate) {
		textureCoordinates.add(textureCoordinate);
	}
}
