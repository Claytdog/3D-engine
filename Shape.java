package newGame;

public abstract class Shape {

	private Color color;

	public Shape(Color c) {
		color = c;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
	public abstract void update();
	public abstract boolean isInFOV();
}