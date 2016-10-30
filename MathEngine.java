package newGame;

public class MathEngine {

	protected static float newX(Vector v) {
		v = v.subtract(Rasterer.camera);
		return v.getX() * Rasterer.camAngleCos + v.getZ()
				* Rasterer.camAngleSin;
	}

	protected static float newY(Vector v) {
		v = v.subtract(Rasterer.camera);
		return v.getX() * Rasterer.camAngleSin * Rasterer.camYAngleSin
				+ v.getY() * Rasterer.camYAngleCos - v.getZ()
				* Rasterer.camAngleCos * Rasterer.camYAngleSin;
	}

	protected static float newZ(Vector v) {
		v = v.subtract(Rasterer.camera);
		return -v.getX() * Rasterer.camAngleSin * Rasterer.camYAngleCos
				+ v.getY() * Rasterer.camYAngleSin + v.getZ()
				* Rasterer.camAngleCos * Rasterer.camYAngleCos;
	}

	protected static int screenX(Vector x) {
		return screenX(newX(x), newZ(x));
	}

	/**
	 * 
	 * @param x
	 * @param z
	 * @return Screen x-value
	 */
	protected static int screenX(float x, float z) {
		return Rasterer.WIDTH / 2 + (int) (x * Rasterer.DEPTH / z);
	}

	protected static int screenY(Vector y) {
		return screenY(newY(y), newZ(y));
	}

	/**
	 * 
	 * @param y
	 * @param z
	 * @return Screen y-value
	 */
	protected static int screenY(float y, float z) {
		return Rasterer.HEIGHT / 2 - (int) (y * Rasterer.DEPTH / z);
	}

	protected static double square(double d) {
		return d * d;
	}

	protected static int square(int d) {
		return d * d;
	}

	protected static float getLightValue(Triangle t) {
		float val = -t.getNormal().dotProduct(Rasterer.lightVector);
		if (val < Rasterer.ambiantLight)
			val = Rasterer.ambiantLight;
		return val;
	}

	protected static Color getColor(Triangle t, int x, int y, float val) {
		Vector v = getVector(t, x, y);
		//Color cc = linearInterpolation(t, v);
		Color cc = new Color(t.getColor().getColor());
		/*
		 * for (EmissiveLight i : Rasterer.lightSources) { float va =
		 * i.getLightValue(v, t.getNormal()); // if (va > val) val = va; }
		 */
		cc.setRGB(cc.multiply(val));
		float dis = (float) (MathEngine.square(v.getX()) // fog
				+ MathEngine.square(v.getY()) + MathEngine.square(v.getZ()));
		if (dis <= MathEngine.square(Rasterer.viewDistance)
				&& (dis < Rasterer.zBuffer[x + y * Rasterer.WIDTH] || Rasterer.zBuffer[x
						+ y * Rasterer.WIDTH] == -1)) {
			Rasterer.zBuffer[x + y * Rasterer.WIDTH] = dis;
			int dist = Rasterer.viewDistance - Rasterer.fog;
			if (dis <= MathEngine.square(dist)) {
				return cc;
			} else {
				float perc = (float) ((Math.sqrt(dis) - dist) / (Rasterer.fog));
				if (perc != 0)
					return new Color(Rasterer.bgColor.multiply(perc)
							+ cc.multiply(1 - perc));
			}
		}
		return null;
	}

	protected static Vector getVector(Triangle t, int x, int y) {
		double[] vars = {t.getV1().getX(), t.getV1().getY(), t.getV1().getZ(),
				t.getV2().getX() - t.getV1().getX(), t.getV2().getY() - t.getV1().getY(), t.getV2().getZ() - t.getV1().getZ(), 
				t.getV3().getX() - t.getV1().getX(), t.getV3().getY() - t.getV1().getY(), t.getV3().getZ() - t.getV1().getZ()};
		double z = (vars[3] * (vars[7] * vars[2] - vars[8] * vars[1]) - vars[6] * (vars[4] * vars[2] - vars[1] * vars[5]) + vars[0] * (vars[4] * vars[8] - vars[7] * vars[5]))
				/ (vars[3] * (vars[7] - vars[8] * (-y + .5 + Rasterer.HEIGHT / 2) / Rasterer.DEPTH) - vars[6] * (vars[4] - vars[5] * (-y + .5 + Rasterer.HEIGHT / 2) / Rasterer.DEPTH) + (x + .5 - Rasterer.WIDTH / 2) / Rasterer.DEPTH * (vars[4] * vars[8] - vars[7] * vars[5]));
		float min = min(t.getV1().getZ(), t.getV2().getZ(), t.getV3().getZ());
		if (z < min)
			z = min;
		return new Vector(
				(float) (z * (x - Rasterer.WIDTH / 2) / Rasterer.DEPTH),
				(float) (z * (-y + Rasterer.HEIGHT / 2) / Rasterer.DEPTH),
				(float) z);
	}
	
	private static float min(float x, float y, float z) {
		if (x < y && x < z) {
			return x;
		}
		if (y < x && y < z)
			return y;
		return z;
	}
	
	private static Color linearInterpolation(Triangle t, Vector v) {
		Vector ab = t.getAB();
		Vector ac = t.getAC();
		v = v.subtract(t.getV1());
		float c2 = (ac.getY() * v.getX() - ac.getX() * v.getY()) / (ab.getX()*ac.getY()-ab.getY()*ac.getX());
		float c3 = -(ab.getY() * v.getX() - ab.getX() * v.getY()) / (ab.getX()*ac.getY()-ab.getY()*ac.getX());
		float c1 = 1 - c2 - c3;
		if (c1 < 0) {
			c1 = 0;
		}
		return new Color((t.getColor().multiply(c1)) + (t.getColor2().multiply(c2)) + (t.getColor3().multiply(c3)));
	}

	/**
	 * 
	 * @param v
	 *            = position Vector of light source in relation to camera
	 * @param p
	 *            = direction from v of the line
	 * 
	 *            y-value of intersection of line of shadow volume that passes
	 *            through plane 1 of the view frustrum (plane on left)
	 * 
	 * @return ret[0] = y-value of intersection in plane 1 or x-value of
	 *         intersection in plane 2 ret[1] = y-value of intersetcion in plane
	 *         3 or x-value of intersection in plane 2 ret[2] = if 0, y-value is
	 *         stored in ret[0], if 1, x-value is stored ret[3] = if 0, y value
	 *         is stored in ret[1], if 1, x-value is stored
	 */
	protected static float[] getVolumeBoundIntersections(Vector v, Vector p) {
		float[] ret = new float[4];
		float x = Rasterer.slope2 * p.getX() + p.getZ();
		float y = p.getY() - Rasterer.slope1 * p.getZ();
		System.out.println("x: " + x + "\ny: " + y);
		if (x != 0) {
			float tx = (float) -((Rasterer.slope2 * v.getX() + v.getZ()) / x);
			float txn = (float) -((-Rasterer.slope2 * v.getX() + v.getZ()) / (-Rasterer.slope2
					* p.getX() + p.getZ()));
			float x2 = MathEngine.screenX(v.getX() + p.getX() * tx,
					v.getZ() + p.getZ() * tx);
			float x4 = MathEngine.screenX(v.getX() + p.getX() * txn,
					v.getZ() + p.getZ() * txn);
			System.out.println("tx: " + tx + "\ntxn: " + txn + "\nx2: " + x2
					+ "\nx4: " + x4);
			if (Math.abs(x2) <= Rasterer.WIDTH / 2f) {
				ret[0] = x2;
				ret[2] = 1;
			}
			if (Math.abs(x4) <= Rasterer.WIDTH / 2f) {
				ret[1] = x4;
				ret[3] = 1;
			}

		}
		if (y != 0) {
			float ty = (float) (Rasterer.slope1 * v.getZ() - v.getY()) / y;
			float tyn = (float) (-Rasterer.slope1 * v.getZ() - v.getY())
					/ (Rasterer.slope1 * p.getZ() + p.getY());
			float y1 = MathEngine.screenY(v.getY() + p.getY() * ty,
					v.getZ() + p.getZ() * ty);
			float y3 = MathEngine.screenY(v.getY() + p.getY() * tyn,
					v.getZ() + p.getZ() * tyn);
			System.out.println("ty: " + ty + "\ntyn: " + tyn + "\ny1: " + y1
					+ "\ny3: " + y3);
			if (Math.abs(y1) <= Rasterer.HEIGHT) {
				ret[0] = y1;
				ret[2] = 0;
			}
			if (Math.abs(y3) <= Rasterer.HEIGHT) {
				ret[1] = y3;
				ret[3] = 0;
			}
		}
		return ret;
	}

	protected static int round(float d) {
		return round((double) d);
	}

	protected static int round(double d) {
		if (d % 1 >= 0.5)
			return (int) (d + .5);
		else
			return (int) d;
	}
}
