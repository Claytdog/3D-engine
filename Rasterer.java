package newGame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Rasterer extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	
	protected static Vector camera = new Vector(0, 0, -2000, false);
	protected static float camAngle = 0;
	protected static float camYAngle = 0;
	protected static float camAngleSin = (float) Math.sin(camAngle);
	protected static float camAngleCos = (float) Math.cos(camAngle);
	protected static float camYAngleSin = (float) Math.sin(camYAngle);
	protected static float camYAngleCos = (float) Math.cos(camYAngle);

	protected static final int WIDTH = 800;
	protected static final int HEIGHT = 600;
	
	
	protected static final int viewDistance = 2000;
	protected static final float viewAngle = 70;
	protected static int fog = 200;
	protected static Color bgColor = new Color(2, 4, 10);
	protected static final double DEPTH = 1.0 / Math.tan(Math.PI * viewAngle
			/ 360.0) * WIDTH / 2d;
	
	//slope of the four planes bounding the view spectrum
	protected static final float slope1 = (float) -(2 * DEPTH / WIDTH);
	protected static final float slope2 = (float) (2 * DEPTH / HEIGHT);
	
	// slope3 = (float) -slope1;
	// slope4 = (float) -slope2;
	
	
	protected static float lightAngle = (float) Math.PI;
	protected static Vector lightVector = new Vector(0, 0, 1);
	protected static float ambiantLight = 0.07f;
	
	
	protected static ArrayList<EmissiveLight> lightSources = new ArrayList<EmissiveLight>();
	protected static ArrayList<Shape> staticObjects = new ArrayList<Shape>();

	private static Drawer d;

	private static BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);
	protected static int[] pixels = ((DataBufferInt) image.getRaster()
			.getDataBuffer()).getData();
	protected static float[] zBuffer = new float[WIDTH * HEIGHT];
	protected static boolean[] stencilBuffer = new boolean[WIDTH * HEIGHT];
	
	static InputHandle input;
	private static float[][] hMap = new float[128][128];
	protected static ArrayList<Cube> cubes = new ArrayList<Cube>();
	
	

	public Rasterer() {
		for (int x = 0; x < WIDTH * HEIGHT; x++) {
			pixels[x] = bgColor.getColor();
		}

		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setMaximumSize(new Dimension(WIDTH, HEIGHT));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		d = new Drawer(WIDTH, HEIGHT);

		input = new InputHandle(this);
		System.out.println("Constructor");
		setSize(WIDTH, HEIGHT);
		setTitle("Cube");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setFocusable(true);
	}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;

		int frames = 0;
		int ticks = 0;

		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		while (true) {
			for (Shape s : staticObjects)
				s.update();
			//lightAngle += Math.PI / 32;
			//lightVector = new Vector((float) Math.sin(lightAngle), (float) Math.cos(lightAngle), (float) Math.cos(lightAngle));
			if (input.shift.isPressed()) {
				if (input.up.isPressed()) {
					camera = camera.add(0, 5, 0);
				}
				if (input.down.isPressed()) {
					camera = camera.add(0, -5, 0);
				}
			} else {
				if (input.up.isPressed()) {
					camera = camera
							.add((float) (-5 * Math.sin(camAngle)), 0,
									(float) (5 * Math.cos(camAngle)));
				}
				if (input.down.isPressed()) {
					camera = camera.add(
							(float) (5 * Math.sin(camAngle)),
							0, -(float) (5 * Math.cos(camAngle)));
				}
			}
			if (!input.shift.isPressed()) {
				if (input.left.isPressed()) {
					camAngle += Math.PI / 128;
					camAngleSin = (float) Math.sin(camAngle);
					camAngleCos = (float) Math.cos(camAngle);
				}
				if (input.right.isPressed()) {
					camAngle -= Math.PI / 128;
					camAngleSin = (float) Math.sin(camAngle);
					camAngleCos = (float) Math.cos(camAngle);
				}
			} else {
				if (input.left.isPressed()) {
					camera = camera.subtract((float) (5 * Math.cos(camAngle)),
							0, (float) (5 * Math.sin(camAngle)));
				}
				if (input.right.isPressed()) {
					camera = camera.add((float) (5 * Math.cos(camAngle)), 0,
							(float) (5 * Math.sin(camAngle)));
				}
			}
			if (input.k.isPressed()) {
				camYAngle += Math.PI / 64;
				camYAngleSin = (float) Math.sin(camYAngle);
				camYAngleCos = (float) Math.cos(camYAngle);
			}
			if (input.m.isPressed()) {
				camYAngle -= Math.PI / 64;
				camYAngleSin = (float) Math.sin(camYAngle);
				camYAngleCos = (float) Math.cos(camYAngle);
			}

			if (camAngle < -Math.PI) {
				camAngle += Math.PI * 2;
			}
			if (camAngle >= Math.PI) {
				camAngle -= Math.PI * 2;
			}

			// lightVector.setX((float) Math.sin(lightAngle));
			// lightVector.setY((float) Math.cos(lightAngle));
			// lightVector.setZ((float) Math.cos(lightAngle));

			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;

			boolean shouldRender = true;

			while (delta >= 1) {
				ticks++;
				delta--;
				shouldRender = true;
			}

			if (shouldRender) {
				frames++;
				render();
			}

			if (System.currentTimeMillis() - lastTimer > 1000) {
				lastTimer += 1000;
				System.out.println(ticks + " ticks, " + frames
						+ " frames camPos: " + camera.getX() + " "
						+ camera.getY() + " " + camera.getZ());
				frames = 0;
				ticks = 0;
			}
		}
	}

	public static void main(String args[]) {
		hMap = PerlinNoise.smooth(hMap, 2);
		int acc = 1;
		float[][] nHMap = PerlinNoise.bicubicSpline(hMap, acc);
		System.out.println(nHMap.length * nHMap[0].length * 3);
		float max = 0;
		float min = 255;
		for (int x = 0; x < nHMap.length; x++) {
			for (int y = 0; y < nHMap[0].length; y++) {
				if (nHMap[x][y] > max)
					max = nHMap[x][y];
				if (nHMap[x][y] < min)
					min = nHMap[x][y];
			}
		}
		for (int x = 0; x < nHMap.length; x++) {
			for (int y = 0; y < nHMap[0].length; y++) {
				nHMap[x][y] -= min;
				nHMap[x][y] *= (255f / (max - min));
			}
		}
		//staticObjects.add(new Triangle(0, 0, 0, -100, 150, 10, 100, 100, 0, new Color(0, 255, 0), new Color(0, 100, 100), new Color(0, 0, 255)));
		Raw r = new Raw("Boat");
		Raw.interpretASCIIString(r.s);
		//Triangle t = new Triangle(-50, 0, 100, 0, 50, 100, 50, 0, 100, new Color(0,255,0));
		//staticObjects.add(t);
		//interpretString("0 0 0  0 0 1  1 0 1 ");
		//staticObjects.add(new Triangle(-50, -80, -30, 0, -80, 80, 40, -80, -70,
				//new Color(0, 0, 255)));
		//staticObjects.add(new Triangle(0, 200, 30, 10, 220, 90, 50, 190, 30,
				//new Color(0, 0, 255)));
		/*for (float x = 0; x < nHMap.length - 1; x++) {
			for (float y = 0; y < nHMap.length - 1; y++) {
				Line l1 = new Line(x / acc * 25, nHMap[(int) x][(int) y], y / acc * 25,
						(x * 25 + 25) / acc, nHMap[(int) x + 1][(int) y + 1], (y * 25 + 25) / acc, new Color(0, 210, 49));
				Line l2 = new Line((x * 25 + 25) / acc, nHMap[(int) x + 1][(int) y + 1], (y * 25 + 25) / acc,
						(x * 25 + 25) / acc, nHMap[(int) x + 1][(int) y], y / acc * 25, new Color(0, 210,
								49));
				Line l3 = new Line(x / acc * 25,
						nHMap[(int) x][(int) y + 1], (y * 25 + 25) / acc, (x * 25 + 25) / acc,
						nHMap[(int) x + 1][(int) y + 1], (y * 25 + 25) /acc, new Color(0, 210, 49));
				staticObjects.add(l1);
				staticObjects.add(l2);
				staticObjects.add(l3);
			}
		}*/
		/*for (float x = 0; x < nHMap.length - 1 * acc - 1; x++) {
			for (float y = 0; y < nHMap.length - 1 * acc - 1; y++) {
				Triangle t1 = new Triangle(x * 25 / acc, nHMap[(int) x][(int) y], y * 25 / acc,
						(x * 25 + 25) / acc, nHMap[(int) x + 1][(int) y + 1], (y * 25 + 25) / acc,
						(x * 25 + 25) / acc, nHMap[(int) x + 1][(int) y], y / acc * 25, new Color(0, 210,
								49));
				Triangle t2 = new Triangle(x * 25 / acc, nHMap[(int) x][(int) y], y * 25 / acc, x * 25 / acc,
						nHMap[(int) x][(int) y + 1], (y * 25 + 25) / acc, (x * 25 + 25) / acc,
						nHMap[(int) x + 1][(int) y + 1], (y * 25 + 25) / acc, new Color(0, 210, 49));
				staticObjects.add(t1);
				staticObjects.add(t2);
			}
		}*/
		/*for (float x = 0; x < nHMap.length - 1; x++) {
			for (float y = 0; y < nHMap.length - 1; y++) {
				int height = ((int) (nHMap[(int) x][(int) y] / 25)) * 25;
				Cube c = new Cube(new Vector(x * 25, height - 25, y * 25), new Vector(x * 25 + 25, height, y * 25 + 25), new Color(0, 255, 255));
				staticObjects.add(c);
			}
		}*/
		/*for (float x = 0; x < nHMap.length - 1; x++) {
			for (float y = 0; y < nHMap.length - 1; y++) {
				int height = ((int) (nHMap[(int) x][(int) y] / 25)) * 25;
				for (int h = height; h > 0; h-=25) {
					Cube c = new Cube(new Vector(x * 25, h - 25, y * 25), new Vector(x * 25 + 25, h, y * 25 + 25), new Color(0, 255, 255));
					staticObjects.add(c);
				}
			}
		}*/
		
		/**int[] pix = new int[1];
		//PixelGrabber i = new PixelGrabber((ImageProducer) new Image("/Users/Clayton/Desktop/Black Pixel.bmp"), 0, 0, 1, 1,
				//pix, 0, 1);
		System.out.println("\nStart\n");
		try {
			//i.grabPixels();
		} catch (Exception  InterruptedException) {}
		//int[] pixels = (int[]) i.getPixels();
		for (int f : pixels)
			System.out.println(f);
		System.out.println("\nStop\n");
		
		//System.out.println(Rasterer.slope1 + "\n" + Rasterer.slope2);
		Vector v = new Vector(0, 0, 50);
		Vector p = new Vector(1, 0, 0);
		float[] f = MathEngine.getVolumeBoundIntersections(v, p);
		String s;
		if (f[2] == 0) {
			s = "y-value is ";
		} else {
			s = "x-value is ";
		}**/
		//System.out.println(s + f[0]);
		System.out.println(staticObjects.size() + " triangles drawn per frame");
		Rasterer c = new Rasterer();
		Thread tr = new Thread(c);
		tr.start();
	}
	

	public void render() {
		for (int x = 0; x < WIDTH * HEIGHT; x++) {
			pixels[x] = bgColor.getColor();
			zBuffer[x] = -1;
		}

		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}

		for (int x = 0; x < staticObjects.size(); x++) {
			Shape s = staticObjects.get(x);
			if (s instanceof Line) {
				//if (s.isInFOV())
				try {
					d.drawLine((Line) s);
				} catch(Exception e) {}
			}
			else if (s instanceof Triangle) {
				if (s.isInFOV())
					d.drawTri((Triangle) s);
			} else if (s instanceof Cube) {
				d.drawCube((Cube) s);
			}
		}

		Graphics g = bs.getDrawGraphics();

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		g.dispose();
		bs.show();
	}
}