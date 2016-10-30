package newGame;

public class Matrix {
	
	private float[][] m;
	
	public Matrix(float[][] m) {
		this.m = m;
	}
	
	public Matrix(Vector[] v) {
		float[][] f = new float[v.length][3];
		for (int x = 0; x < v.length; x++) {
			f[x][0] = v[x].getX();
			f[x][1] = v[x].getY();
			f[x][2] = v[x].getZ();
		}
		this.m = f;
	}
	
	public float get(int row, int column) {
		return m[row][column];
	}
	
	public float[][] getArray() {
		return m;
	}
	
	public void set(int row, int column, float value) {
		m[row][column] = value;
	}
	
	public Matrix multiply(Matrix n) {
		if (m[0].length != n.getArray().length)
			return null;
		int mr = m.length, mc = m[0].length;
		int nc = n.getArray()[0].length;
		float[][] ans = new float[mr][nc];
		for (int x = 0; x < mr; x++) {
			for (int y = 0; y < nc; y++) {
				for (int z = 0; z < mc; z++) {
					ans[x][y] += m[x][z] * n.getArray()[z][y];
				}
			}
		}
		return new Matrix(ans);
	}
	
	public Matrix inverse() {
		return new Matrix(inverse(this.m));
	}
	
	public static float[][] inverse(float[][] n) {
		float[][] inv = new float[n.length][n[0].length];
		if (n.length <= 1) {
			inv[0][0] = 1 / n[0][0];
			return inv;
		}
		else if (n.length == 2) {
			float det = determinant(n);
			inv[0][0] = n[1][1] / det;
			inv[0][1] = -n[1][0] / det;
			inv[1][0] = -n[0][1] / det;
			inv[1][1] = n[0][0] / det;
			return inv;
		}
		else {
			float det = determinant(n);
			byte a = -1, b = 1;
			for (int x = 0; x < n.length; x++) {
				a *= -1;
				b *= -1;
				for (int y = 0; y < n.length; y++) {
					b *= -1;
					inv[x][y] = a * b * determinant(sub(n, x, y)) / det;
				}
			}
			return inv;
		}
	}
	
	
	public float determinant() {
		return determinant(m);
	}
	
	public static float determinant(float[][] n) {
		if (n.length <= 1)
			return n[0][0];
		else if (n.length == 2) {
			return (n[0][0] * n[1][1]) - (n[0][1] * n[1][0]);
		}
		float ans = 0;
		for (int x = 0; x < n[0].length; x++) {
			if (x % 2 == 0)
				ans += n[0][x] * determinant(sub(n, x));
			else
				ans -= n[0][x] * determinant(sub(n, x));
		}
		return ans;
	}
	
	public static float[][] sub(float[][] n, int x) {
		return sub(n, x, 0);
	}
	
	public static float[][] sub(float[][] n, int x, int y) {
		float[][] ans = new float[n.length - 1][n[0].length - 1];
		byte s = 0, t = 0;
		for (int b = 0; b < ans[0].length; b++) {
			t=0;
			if (b == x)
				s = 1;
			for (int a = 0; a < ans.length; a++) {
				if (a == y)
					t = 1;
				ans[a][b] = n[a + t][b + s];
			}
		}
		return ans;
	}
}