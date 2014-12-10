import static javax.media.opengl.GL.GL_TEXTURE_2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Laser extends Ship {
	private static ObjModel model;

	public Laser() {
		super();
		try {
			if (model == null)
				model = new ObjModel(new File("laser.obj"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(getX(), getY(), getZ());
//		gl.glScalef(10f, 10f, 10f);
		gl.glMultMatrixf(changeOfBasis().toArray(), 0);
		gl.glColor3f(1f, 0f, 0f);
		gl.glDisable(GL_TEXTURE_2D);
		model.render(gl);
		gl.glEnable(GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
}
