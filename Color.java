package newGame;

public class Color {

	private int color;

	public Color(int r, int g, int b) {
		color = r << 16 | g << 8 | b;
	}

	public Color(int c) {
		color = c;
	}

	public int getColor() {
		return color;
	}

	public int getR() {
		return (int) color >> 16;
	}

	public int getG() {
		return (int) (color >> 8) & 255;
	}

	public int getB() {
		return (int) color & 255;
	}
	
	public void setRGB(Color c) {
	    color = c.getColor();
	}
	
	public void setRGB(int c) {
	    color = c;
	}
	
	public String toString() {
		return "" + getR() + " " + getG() + " " + getB() + "\n";
	}

	public int add(Color c) {
		return (this.getR() + c.getR()) << 16 | (this.getG() + c.getG()) << 8
				| (this.getB() + c.getB());
	}

	public int subtract(Color c) {
		return (this.getR() - c.getR()) << 16 | (this.getG() - c.getG()) << 8
				| (this.getB() - c.getB());
	}

	public int multiply(float s) {
		return (int) (this.getR() * s) << 16 | (int) (this.getG() * s) << 8
				| (int) (this.getB() * s);
	}

	public int divide(float s) {
		return (int) (this.getR() / s) << 16 | (int) (this.getG() / s) << 8
				| (int) (this.getB() / s);
	}
}
