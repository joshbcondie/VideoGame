package game;

import static javax.media.opengl.GL.GL_TEXTURE_2D;
import game.obj.ObjModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.media.opengl.GL2;

public class Laser extends FlyingObject {
	private static ObjModel model;

	private List<Ship> ships;
	private int shipIndex;

	public Laser(Terrain terrain, List<Ship> ships, int shipIndex) {
		super(terrain);
		try {
			if (model == null)
				model = new ObjModel(new File("laser.obj"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.ships = ships;
		this.shipIndex = shipIndex;
	}

	private Ship hitsAnyShip() {
		float dx = 0;
		float dy = 0;
		float dz = 0;
		for (int i = 0; i < ships.size(); i++) {
			if (i == shipIndex)
				continue;
			dx = ships.get(i).getX() - getX();
			dy = ships.get(i).getY() - getY();
			dz = ships.get(i).getZ() - getZ();
			if (dx * dx + dy * dy + dz * dz <= 10) {
				return ships.get(i);
			}
		}
		return null;
	}

	@Override
	public void update() {
		Ship hitShip = hitsAnyShip();
		if (hitShip != null) {
			die();
			hitShip.die();
		}
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
