package newGame;

public class Line extends Shape {

	private Vector one, two;

	public Line(float x1, float y1, float z1, float x2, float y2, float z2,
			Color c) {
		super(c);
		this.one = new Vector(x1, y1, z1);
		this.two = new Vector(x2, y2, z2);
		update();
	}

	public Line(Vector v1, Vector v2, Color c) {
		super(c);
		this.one = v1;
		this.two = v2;
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
	
	public void update() {
		one.update();
		two.update();
		
	}

	public boolean isInFOV() {
		if (one.isInFOV() || two.isInFOV()) {
			if (one.newZ() <= 0 && two.newZ() <= 0) {
				return true;
			} else if (one.newZ() >= 0 && two.newZ() >= 0)
				return true;
			else
				return false;
		} else
			return false;
	}
}