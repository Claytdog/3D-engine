package newGame;

public class Vector extends Shape {

	private float x, y, z, newX, newY, newZ, magnitude;
	
	private int screenX, screenY;

	public Vector(float x, float y, float z) {
		super(null);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(float x, float y, float z, boolean huh) {
		super(null);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setMag() {
		this.magnitude = (float) Math.sqrt(MathEngine.square(x)
				+ MathEngine.square(y) + MathEngine.square(z));
	}
	
	public static float magnitude(Vector v) {
		return (float) Math.sqrt(MathEngine.square(v.getX())
				+ MathEngine.square(v.getY()) + MathEngine.square(v.getZ()));
	}

	public Vector(float x, float y) {
		this(x, y, 0);
	}

	public Vector(Vector v) {
		this(v.x, v.y, v.z);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public float newX() {
		return newX;
	}

	public float newY() {
		return newY;
	}

	public float newZ() {
		return newZ;
	}

	/**
	 * 
	 * @return screen x-value of this Vector, assuming it has already been transformed
	 */
	public int screenX() {
		return screenX;
	}

	/**
	 * 
	 * @return screen y-value of this Vector, assuming it has already been transformed
	 */
	public int screenY() {
		return screenY;
	}

	public float magnitude() {
		return magnitude;
	}

	public Vector add(Vector v) {
		return add(v.getX(), v.getY(), v.getZ());
	}

	public Vector add(float x, float y, float z) {
		return new Vector(this.x + x, this.y + y, this.z + z);
	}

	public Vector subtract(Vector v) {
		return subtract(v.getX(), v.getY(), v.getZ());
	}

	public Vector subtract(float x, float y, float z) {
		return new Vector(this.x - x, this.y - y, this.z - z);
	}

	public Vector multiply(double scalar) {
		return new Vector((float) (this.x * scalar), (float) (this.y * scalar),
				(float) (this.z * scalar));
	}

	public float angle(Vector v) {
		return (float) Math.acos(this.dotProduct(v)
				/ (this.magnitude() * v.magnitude()));
	}

	public float dotProduct(Vector v) {
		v.update();
		v = v.normalize();
		return dotProduct(v.x, v.y, v.z);
	}

	public float dotProduct(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public Vector crossProduct(Vector v) {
		return crossProduct(v.getX(), v.getY(), v.getZ());
	}

	public Vector crossProduct(float x, float y, float z) {
		return new Vector(this.y * z - this.z * y, this.z
				* x - this.x * z, this.x * y - this.y * x);
	}

	public Vector getNewVector() {
		return new Vector(newX, newY, newZ);
	}

	public Vector getOldVector() {
		float x = (float) (this.x * Rasterer.camAngleCos + this.y * Rasterer.camAngleSin
				* Rasterer.camYAngleSin - this.z * Rasterer.camAngleSin * Rasterer.camYAngleCos);
		float y = (float) (this.y * Rasterer.camYAngleCos + this.z * Rasterer.camYAngleSin);
		float z = (float) (this.x * Rasterer.camAngleSin - this.y * Rasterer.camAngleCos
				* Rasterer.camYAngleSin + this.z * Rasterer.camAngleCos * Rasterer.camYAngleCos);
		return Rasterer.camera.add(x, y, z);
	}

	public Vector normalize() {
		return new Vector(x / magnitude, y / magnitude, z / magnitude);
	}
	
	public String toString() {
		return "" + getX() + "\n" + getY() + "\n" + getZ() + "\n";
	}
	
	public void update() {
		setMag();
		screenX = MathEngine.screenX(this.x, this.z);
		screenY = MathEngine.screenY(this.y, this.z);
		newX = MathEngine.newX(this);
		newY = MathEngine.newY(this);
		newZ = MathEngine.newZ(this);
	}

	public boolean isInFOV() {
		float sdistance = (float) (MathEngine.square(newX)
				+ MathEngine.square(newY) + MathEngine.square(newZ));
		return !(newZ < 0 || sdistance > MathEngine.square(Rasterer.viewDistance))
				&& (Math.abs(newX / newZ) < Rasterer.WIDTH / 2d / Rasterer.DEPTH) && (Math
				.abs(newY / newZ) < Rasterer.HEIGHT / 2d / Rasterer.DEPTH) && newZ > 0;
	}
}
