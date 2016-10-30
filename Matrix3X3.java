package newGame;

public class Matrix3X3 {

	private double[] a;

	public Matrix3X3(double[] n) {
		a = n;
	}

	public static double determinant(double[] m) {
		if (m.length == 4) {
			return m[0] * m[3] - m[1] * m[2];
		} else {
			int rt = (int) Math.sqrt(m.length);
			double total = 0;
			for (int x = 0; x < rt; x++) {
				if (x % 2 == 0)
					total += m[x] * determinant(getSub(m, x + 1));
				else
					total -= m[x] * determinant(getSub(m, x + 1));
			}
			return total;
		}
	}
	
	public static Vector solve(double[] m) {
		double x = solveDX(m);
		m = new double[]{m[1], m[2], m[3] - x * m[0], m[5], m[6], m[7] - x * m[4]};
		double y = solveDY(m);
		m = new double[]{m[1], m[2] - y * m[0]};
		return new Vector((float) x, (float) y, (float) solveDZ(m));
	}
	
	public static double solveDX(double[] m) {
		double d = determinant(new double[] {m[0], m[1], m[2], m[4], m[5],
				m[6], m[8], m[9], m[10] });
		return (determinant(new double[] {m[3], m[1], m[2],
				m[7], m[5], m[6], m[11], m[9], m[10] }) / d);
	}
	
	public static double solveDY(double[] m) {
		if (m.length == 6) {
			double d = determinant(new double[]{m[0], m[1], m[3], m[4]});
			return (determinant(new double[]{m[2], m[1], m[5], m[4]}) / d);
		}
		double d = determinant(new double[] {m[0], m[1], m[2], m[4], m[5],
				m[6], m[8], m[9], m[10] });
		return (determinant(new double[] {m[0], m[3], m[2], m[4],
						m[7], m[6], m[8], m[11], m[10] }) / d);
	}
	
	public static double solveDZ(double[] m) {
		if (m.length == 2) {
			return (m[1] / m[0]);
		}
		double d = determinant(new double[] {m[0], m[1], m[2], m[4], m[5],
				m[6], m[8], m[9], m[10] });
		return (determinant(new double[] {m[0], m[1], m[3], m[4],
						m[5], m[7], m[8], m[9], m[11] }) / d);
	}

	public static double[] getSub(double[] d, int i) {
		int rt = (int) Math.sqrt(d.length);
		double[] ret = new double[d.length - 2 * (rt) + 1];
		int it = 0;
		for (int y = 1; y < rt; y++) {
			for (int x = 0; x < rt - 1; x++) {
				if (x >= i - 1)
					ret[it] = d[x + 1 + y * rt];
				else
					ret[it] = d[x + y * rt];
				it++;
			}
		}
		return ret;
	}

	public Vector solve() {
		double x = 0;
		double y = 0;
		double z = 0;
		if (a[0] == 0) {
			swap(0, 1);
			if (a[0] == 0)
				swap(0, 2);
		}
		div(0, a[0]);
		if (a[4] != 0) {
			div(1, a[4]);
			subtract(1, 0);
		}
		if (a[8] != 0) {
			div(2, a[8]);
			subtract(2, 0);
		}
		if (a[5] != 0)
			div(1, a[5]);
		if (a[9] != 0) {
			div(2, a[9]);
			subtract(2, 1);
		}
		if (a[10] != 0)
			z = a[11] / a[10];
		a[7] -= a[6] * z;
		if (a[5] != 0) {
			y = a[7] / a[5];
		}
		a[3] -= a[2] * z + a[1] * y;
		x = a[3];
		return new Vector((float) x, (float) y, (float) z);
	}

	private void div(int r, double n) {
		for (int x = r * 4; x < r * 4 + 4; x++) {
			a[x] /= n;
		}
	}

	private void subtract(int r, int s) {
		a[r * 4] -= a[s * 4];
		a[r * 4 + 1] -= a[s * 4 + 1];
		a[r * 4 + 2] -= a[s * 4 + 2];
		a[r * 4 + 3] -= a[s * 4 + 3];
	}

	private void swap(int r, int s) {
		double o = a[r * 4];
		double t = a[r * 4 + 1];
		double f = a[r * 4 + 2];
		double q = a[r * 4 + 3];
		a[r * 4] = a[s * 4];
		a[r * 4 + 1] = a[s * 4 + 1];
		a[r * 4 + 2] = a[s * 4 + 2];
		a[r * 4 + 3] = a[s * 4 + 3];
		a[s * 4] = o;
		a[s + 4 + 1] = t;
		a[s * 4 + 2] = f;
		a[s * 4 + 3] = q;
	}
}