import static javax.media.opengl.GL.GL_TEXTURE_2D;

import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL2;

public class Laser extends FlyingObject {
	private static ObjModel model;

	public Laser(Terrain terrain) {
		super(terrain);
		try {
			if (model == null)
				model = new ObjModel(new File("laser.obj"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		if (getX() < 0 || getX() >= terrain.getLength() || getZ() < 0
				|| getZ() >= terrain.getLength())
			die();
		super.update();
	}

	public void render(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(getX(), getY(), getZ());
		gl.glScalef(0.2f, 0.2f, 0.2f);
		gl.glMultMatrixf(changeOfBasis().toArray(), 0);
		gl.glColor3f(1f, 0f, 0f);
		gl.glDisable(GL_TEXTURE_2D);
		model.render(gl);
		gl.glEnable(GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
}
