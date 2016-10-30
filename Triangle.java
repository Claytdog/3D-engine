package newGame;

public class Triangle extends Shape {

	private Vector one, two, three, ab, ac, norm1, norm2, norm3, norm;
	private Color c2, c3;

	public Triangle(float x1, float y1, float z1, float x2, float y2, float z2,
			float x3, float y3, float z3, Color c) {
		this(x1, y1, z1, x2, y2, z2, x3, y3, z3, c, null, null);
	}

	public Triangle(float x1, float y1, float z1, float x2, float y2, float z2,
			float x3, float y3, float z3, Color c, Color c2, Color c3) {
		super(c);
		this.one = new Vector(x1, y1, z1);
		this.two = new Vector(x2, y2, z2);
		this.three = new Vector(x3, y3, z3);
		this.c2 = c2;
		this.c3 = c3;
	}

	public Triangle(Vector one, Vector two, Vector three, Color c1, Color c2, Color c3) {
		this(one.getX(), one.getY(), one.getZ(), two.getX(), two.getY(), two
				.getZ(), three.getX(), three.getY(), three.getZ(), c1, c2, c3);
	}

	public Triangle(Triangle t) {
		this(t.getV1(), t.getV2(), t.getV3(), t.getColor(), t.getColor2(), t.getColor3());
	}

	public Vector getV1() {
		return one;
	}

	public Vector getV2() {
		return two;
	}

	public Vector getV3() {
		return three;
	}

	public Vector getAB() {
		return ab;
	}

	public Vector getAC() {
		return ac;
	}
	
	public Vector getNorm1() {
		return norm1;
	}
	
	public Vector getNorm2() {
		return norm2;
	}
	
	public Vector getNorm3() {
		return norm3;
	}

	public int screenX1() {
		return one.screenX();
	}

	public int screenY1() {
		return one.screenY();
	}

	public int screenX2() {
		return two.screenX();
	}

	public int screenY2() {
		return two.screenY();
	}

	public int screenX3() {
		return three.screenX();
	}

	public int screenY3() {
		return three.screenY();
	}

	public Color getColor2() {
		return c2;
	}

	public Color getColor3() {
		return c3;
	}

	public Triangle subtract(Vector v) {
		return subtract(v.getX(), v.getY(), v.getZ());
	}
	
	public Triangle subtract(float x, float y, float z) {
		return new Triangle(one.subtract(x, y, z), two.subtract(x, y, z),
				three.subtract(x, y, z), getColor(), getColor2(), getColor3());
	}

	public Triangle getNewTri() {
		return new Triangle(one.getNewVector(), two.getNewVector(),
				three.getNewVector(), this.getColor(), this.getColor2(), this.getColor3());
	}

	public Triangle getOldTri() {
		return new Triangle(one.getOldVector(), two.getOldVector(),
				three.getOldVector(), this.getColor(), getColor2(), getColor3());
	}

	public boolean isInside(Vector v) {
		if (norm1.dotProduct(v) < 0)
			return false;
		if (norm2.dotProduct(v) < 0)
			return false;
		if (norm3.dotProduct(v) < 0)
			return false;
		return true;
	}

	public boolean isCloser(Vector v) {
		return norm.dotProduct(v.subtract(one)) < 0;
	}

	public Vector getNormal() {
		return norm;
	}

	public double getArea() {
		int x1 = screenX1();
		int x2 = screenX2();
		int x3 = screenX3();
		int y1 = screenY1();
		int y2 = screenY2();
		int y3 = screenY3();
		return getArea(x1, y1, x2, y2, x3, y3);
	}

	public static double getArea(int x1, int y1, int x2, int y2, int x3, int y3) {
		double a = Math.sqrt(MathEngine.square(x2 - x1)
				+ MathEngine.square(y2 - y1));
		double b = Math.sqrt(MathEngine.square(x3 - x2)
				+ MathEngine.square(y3 - y2));
		double c = Math.sqrt(MathEngine.square(x3 - x1)
				+ MathEngine.square(y3 - y1));
		double s = (a + b + c) / 2;
		return Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}
	
	public void update() {
		one.update();
		two.update();
		three.update();
		this.ab = two.subtract(one);
		this.ac = three.subtract(one);
		this.norm1 = one.crossProduct(two);
		if (norm1.dotProduct(three) < 0)
			norm1 = norm1.multiply(-1.0);
		this.norm2 = one.crossProduct(three);
		if (norm2.dotProduct(two) < 0)
			norm2 = norm2.multiply(-1.0);
		this.norm3 = two.crossProduct(three);
		if (norm3.dotProduct(one) < 0)
			norm3 = norm3.multiply(-1.0);
		this.norm = ab.crossProduct(ac);
		this.norm = norm.multiply(1/Vector.magnitude(norm));
	}

	public boolean isInFOV() {
		float newZ1 = one.newZ();
		float newZ2 = two.newZ();
		float newZ3 = three.newZ();
		if (norm.dotProduct(one.subtract(Rasterer.camera)) >= 0) // backface
																	// culling
			return false;
		return (one.isInFOV() || two.isInFOV() || three
				.isInFOV())
				&& (newZ1 >= 0
						&& newZ2 >= 0 && newZ3 >= 0);
	}
}
