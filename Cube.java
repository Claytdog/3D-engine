package newGame;

public class Cube extends Shape {

	private Vector v1, v2;
	private Vector[] v = new Vector[8];
	private Shape[] t = new Shape[12];

	public Cube(Vector v1, Vector v2, Color c) {
		super(c);
		float x1 = v1.getX();
		float x2 = v2.getX();
		float y1 = v1.getY();
		float y2 = v2.getY();
		float z1 = v1.getZ();
		float z2 = v2.getZ();
		v[0] = v1;
		v[1] = new Vector(x2, y1, z1);
		v[2] = new Vector(x2, y2, z1);
		v[3] = new Vector(x1, y2, z1);
		v[4] = new Vector(x1, y1, z2);
		v[5] = new Vector(x2, y1, z2);
		v[6] = new Vector(x2, y2, z2);
		v[7] = new Vector(x1, y2, z2);
		t[0] = new Triangle(v[0], v[2], v[1], this.getColor(), null, null);
		t[1] = new Triangle(v[0], v[3], v[2], this.getColor(), null, null);
		t[2] = new Triangle(v[0], v[4], v[7], this.getColor(), null, null);
		t[3] = new Triangle(v[0], v[7], v[3], this.getColor(), null, null);
		t[4] = new Triangle(v[4], v[5], v[6], this.getColor(), null, null);
		t[5] = new Triangle(v[4], v[6], v[7], this.getColor(), null, null);
		t[6] = new Triangle(v[1], v[6], v[5], this.getColor(), null, null);
		t[7] = new Triangle(v[1], v[2], v[6], this.getColor(), null, null);
		t[8] = new Triangle(v[3], v[6], v[2], this.getColor(), null, null);
		t[9] = new Triangle(v[3], v[7], v[6], this.getColor(), null, null);
		t[10] = new Triangle(v[0], v[1], v[4], this.getColor(), null, null);
		t[11] = new Triangle(v[1], v[5], v[4], this.getColor(), null, null);
		// t[10] = new Line(v[2], v[1], this.getColor());
		// t[11] = new Line(v[2], v[3], this.getColor());
		// t[12] = new Line(v[0], v[3], this.getColor());
		// t[13] = new Line(v[7], v[4], this.getColor());
		// t[14] = new Line(v[3], v[7], this.getColor());
		// t[15] = new Line(v[6], v[5], this.getColor());
		// t[16] = new Line(v[6], v[7], this.getColor());
		// t[17] = new Line(v[6], v[2], this.getColor());
		this.v1 = v1;
		this.v2 = v2;
	}

	public Vector getV1() {
		return v1;
	}

	public Vector getV2() {
		return v2;
	}

	public Shape getT(int pos) {
		return t[pos];
	}
	
	public int tLength() {
		return t.length;
	}

	public void update() {
		for (Shape v : t)
			v.update();
	}

	public boolean isInFOV() {
		return v1.isInFOV()
				|| v2.isInFOV()
				|| new Vector(v1.getX(), v2.getY(), v2
						.getZ()).isInFOV()
				|| new Vector(v2.getX(), v2.getY(), v1
						.getZ()).isInFOV();
	}
}
