package newGame;

public class Drawer {

	private int width;
	private int height;

	public Drawer(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void drawLine(Line l) {
		int x1 = l.screenX1();
		int x2 = l.screenX2();
		int y1 = l.screenY1();
		int y2 = l.screenY2();
		this.drawLine(null, x1, y1, x2, y2, l.getColor());
	}
	
	public void drawLine(Triangle tri, int x1, int y1, int x2, int y2, Color c) {
		double one = x2 - x1;
		double two = y2 - y1;
		double length;
		if (Math.abs(two) > Math.abs(one))
			length = Math.abs(two);
		else
			length = Math.abs(one);
		double dx = one / length;
		double dy = two / length;
		double x = x1;
		double y = y1;
		if (x < 0) {
			y -= dy / dx * x;
			length -= Math.sqrt(Math.pow(y1 - y, 2) + x * x);
			x = 0;
		}
		if (y < 0) {
			x -= dx / dy * y;
			length -= Math.sqrt(Math.pow(x1 - x, 2) + y * y);
			y = 0;
		}
		if (x >= width) {
			y -= dy / dx * (x - width + 1);
			length -= Math.sqrt(Math.pow(y1 - y, 2) + (x - width + 1) * (x - width + 1));
			x = width - 1;
		}
		if (y >= height) {
			x -= dx / dy * (y - height + 1);
			length -= Math.sqrt(Math.pow(x1 - x, 2) + (y - height + 1) * (y - height + 1));
			y = height - 1;
		}
		for (int t = 0; t <= length && x >= 0 && x < width && y >= 0 && y < height; t++) {
			if (tri == null)
				Rasterer.pixels[(int) x + ((int) y) * width] = c.getColor();
			else {
				//Color c1 = MathEngine.getColor(tri, (int) x, (int) y, 0, c);
				Rasterer.pixels[(int) x + ((int) y) * width] = c.getColor();
			}
			x += dx;
			y += dy;
		}
	}

	public void drawTri(Triangle t) {
		float val = MathEngine.getLightValue(t);
		t = t.getNewTri();
		t.update();
		int x1 = t.screenX1();
		int x2 = t.screenX2();
		int x3 = t.screenX3();
		int y1 = t.screenY1();
		int y2 = t.screenY2();
		int y3 = t.screenY3();

		int hx, hy;
		int xa = x1, xb = x2, xc = x3;
		int ya = y1, yb = y2, yc = y3;
		if (ya == yb) {
			if (xa > xb) {
				hx = xb;
				hy = yb;
				yb = ya;
				xb = xa;
				ya = hy;
				xa = hx;
			}
		} else if (ya > yb) {
			hx = xb;
			hy = yb;
			yb = ya;
			xb = xa;
			ya = hy;
			xa = hx;
		}
		if (yb == yc) {
			if (xb > xc) {
				hx = xc;
				hy = yc;
				yc = yb;
				xc = xb;
				yb = hy;
				xb = hx;
			}
		} else if (yb > yc) {
			hx = xc;
			hy = yc;
			yc = yb;
			xc = xb;
			yb = hy;
			xb = hx;
		}
		if (ya == yb) {
			if (xa > xb) {
				hx = xb;
				hy = yb;
				yb = ya;
				xb = xa;
				ya = hy;
				xa = hx;
			}
		} else if (ya > yb) {
			hx = xb;
			hy = yb;
			yb = ya;
			xb = xa;
			ya = hy;
			xa = hx;
		}

		double slope1 = 0, slope2 = 0, slope3 = 0;
		boolean vert1 = false, vert2 = false, vert3 = false;
		if (ya != yb) {
			slope1 = ((double) xb - xa) / (yb - ya);
		} else
			vert1 = true;
		if (yc != ya) {
			slope2 = ((double) xc - xa) / (yc - ya);
		} else
			vert2 = true;
		if (yc != yb) {
			slope3 = ((double) xc - xb) / (yc - yb);
		} else
			vert3 = true;

		nextLine: for (int y = ya; y <= yc; y++) {
			if (y < 0) {
				y = -1;
				continue nextLine;
			}
			if (y > height - 1)
				return;
			if (y <= yb) {
				int min = 0;
				int max = 0;
				if (vert1) {
					if (xc >= xa) {
						min = xa;
						max = xa + MathEngine.round((y - ya) * slope2);
					} else {
						min = xa + MathEngine.round((y - ya) * slope2);
						max = xa;
					}
				} else if (vert2) {
					if (xb >= xa) {
						min = xa;
						max = xa + MathEngine.round((y - ya) * slope1);
					} else {
						min = xa + MathEngine.round((y - ya) * slope1);
						max = xa;
					}
				} else if (slope1 > slope2) {
					min = xa + MathEngine.round((y - ya) * slope2);
					max = xa + MathEngine.round((y - ya) * slope1);
				} else {
					min = xa + MathEngine.round((y - ya) * slope1);
					max = xa + MathEngine.round((y - ya) * slope2);
				}
				nextPixel: for (int x = min; x <= max; x++) {
					if (x < 0) {
						x = -1;
						continue nextPixel;
					}
					else if (x >= width)
						continue nextLine;
					Color cc = MathEngine.getColor(t, x, y, val);
					if (cc != null)
						Rasterer.pixels[x + y * width] = cc.getColor();
				}
			} else {
				int min = 0;
				int max = 0;
				if (vert3) {
					if (xb >= xa) {
						min = xc + MathEngine.round((y - yc) * slope3);
						max = xc;
					} else {
						min = xc;
						max = xc + MathEngine.round((y - yc) * slope3);
					}
				} else if (vert2) {
					if (xb >= xa) {
						min = xc;
						max = xc + MathEngine.round((y - yc) * slope2);
					} else {
						min = xc + MathEngine.round((y - yc) * slope2);
						max = xc;
					}
				} else if (slope3 < slope2) {
					min = xc + MathEngine.round((y - yc) * slope2);
					max = xc + MathEngine.round((y - yc) * slope3);
				} else {
					min = xc + MathEngine.round((y - yc) * slope3);
					max = xc + MathEngine.round((y - yc) * slope2);
				}
				nextPixel: for (int x = min; x <= max; x++) {
					if (x < 0) {
						x = -1;
						continue nextPixel;
					}
					else if (x >= width)
						continue nextLine;
					Color cc = MathEngine.getColor(t, x, y, val);
					if (cc != null)
						Rasterer.pixels[x + y * width] = cc.getColor();
				}
			}
		}
	}
	
	public void drawCube(Cube c) {
		for (int x = 0; x < 12; x++) {
			if (c.getT(x).isInFOV() && c.getT(x) instanceof Triangle)
				drawTri((Triangle) c.getT(x));
		}
	}

	public int getCorr(int x1, int x2, int y1, int y2, int x) {
		if (x == x1)
			return y1;
		if (x == x2)
			return y2;
		return 0;
	}

	public int getCorrX(int x1, int x2, int y1, int y2, int y) {
		if (y == y1)
			return x1;
		if (y == y2)
			return x2;
		return 0;
	}
}
