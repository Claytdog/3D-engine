package newGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;

public class PerlinNoise extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;

    boolean once = false;
    Graphics h;
    Image i;

    private boolean random = false;

    private boolean noRun = false;

    private static int width = 512;
    private boolean hasnt = true;

    float[][] pix;
    float[][] pixi;
    float[] pi;

    public static void main(String args[]) {
        PerlinNoise n = new PerlinNoise(1);
        Thread t = new Thread(n);
        t.start();
    }

    public void run() {
        repaint();
        try {
            Thread.sleep(5);
        } catch (Exception e) {
        }
    }

    public PerlinNoise(int w) {
        //width = w;
        setSize(width, width);
        setTitle("Noise");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setFocusable(true);
    }

    public static float[][] smooth(float[][] p, int freq) {
        if (freq > p.length) {
            return null;
        } else {
            float range = 1 / (float) (Math.pow(freq, (1.0 / 64)));
            float plus = (1 - range) / 2;
            float[][] ran = new float[p.length / freq][p.length / freq];

            for (int x = 0; x < ran.length; x++) {
                for (int y = 0; y < ran.length; y++) {
                    ran[x][y] = (float) Math.random() * range + plus;
                }
            }

            float[][] inter = new float[p.length][p.length];
            for (int x = 0; x < inter.length; x++) {
                for (int y = 0; y < inter.length; y++) {
                    inter[x][y] = ran[x / freq][y / freq];
                }
            }
            if (freq < width && freq > 1)
                inter = interpolate(inter, freq);
            if (freq < width)
                return add(inter, smooth(p, freq * 2));
            else
                return inter;
        }
    }

    private static float[][] add(float[][] a, float[][] b) {
        if (b == null)
            return a;
        float[][] c = new float[a.length][a.length];
        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a.length; y++) {
                c[x][y] = a[x][y] + b[x][y];
            }
        }
        return c;
    }

    private static float[][] interpolate(float[][] r, int freq) {
        float[][] returner = new float[r.length][r[0].length];
        int numTimes = 0;
        for (int x = 0; x < r.length; x++) {
            for (int y = 0; y < r[0].length; y++) {
                float tot = 0;
                for (int xo = (-1 * freq) / 2; xo < freq / 2; xo++) {
                    for (int yo = (-1 * freq) / 2; yo < freq / 2; yo++) {
                        if (x + xo >= 0 && x + xo < r.length && y + yo >= 0
                                && y + yo < r.length) {
                            tot += r[x + xo][y + yo];
                            numTimes++;
                        } // else
                            // tot += r[x][y];
                    }
                }
                returner[x][y] = tot / (numTimes);
                numTimes = 0;
            }
        }
        System.out.println(freq);
        return returner;
    }
    
    public static float[][] nearestNeighbor(float[][] a, int times) {
        float[][] m = new float[width][width];
        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a[0].length; y++) {
                for (int xx = 0; xx < times; xx++) {
                    for (int yy = 0; yy < times; yy++) {
                        m[xx + x * times][yy + y * times] = a[x][y];
                    }
                }
            }
        }
        return m;
    }

    public static float[][] bicubicSpline(float[][] a, int times) {
        float[][] n = new float[a.length * times][a[0].length * times];
        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a[0].length - 1; y++) {
                double d1, d2, d3, d4;
                d2 = a[x][y];
                if (y == 0)
                    d1 = d2;
                else
                    d1 = a[x][y - 1];
                d3 = a[x][y + 1];
                if (y == a.length - 2)
                    d4 = d3;
                else
                    d4 = a[x][y + 2];

                double a1, a2, a3, a4;
	            a4 = d2;
	            a3 = d3 / 2 - d1 / 2;
	            a2 = (d2 - d4) / 2 + 3 * d3 - 2 * a3 - 3 * a4;
	            a1 = d3 - a2 - a3 - a4;

                for (double test = 0; test < 1; test += 1d / times) {
                    n[x * times][(int) (y * times + times * test)] = (float) (a1 * Math.pow(test, 3) + a2
                            * Math.pow(test, 2) + a3 * test + a4);
                }
            }
        }
        
        for (int y = 0; y < n[0].length; y++) {
            for (int x = 0; x < a.length - 1; x++) {
                double d1, d2, d3, d4;
                d2 = n[x * times][y];
                if (x == 0)
                    d1 = d2;
                else
                    d1 = n[(x - 1) * times][y];
                d3 = n[(x + 1) * times][y];
                if (x == a.length - 2)
                    d4 = d3;
                else
                    d4 = n[(x + 2) * times][y];

                double a1, a2, a3, a4;
	            a4 = d2;
	            a3 = d3 / 2 - d1 / 2;
	            a2 = (d2 - d4) / 2 + 3 * d3 - 2 * a3 - 3 * a4;
	            a1 = d3 - a2 - a3 - a4;
	            
                for (double test = 0; test < 1; test += 1d / times) {
                    n[(int) (x * times + times * test)][y] = (float) (a1 * Math.pow(test, 3) + a2
                            * Math.pow(test, 2) + a3 * test + a4);
                }
            }
        }

        return n;
    }

    public void paint(Graphics g) {
        pixi = new float[width / 64][width / 64];
        if (random) {
            for (int x = 0; x < pix.length; x++) {
                for (int y = 0; y < pix[0].length; y++) {
                    pix[x][y] = (float) (Math.random() * 255);
                }
            }
        } else {
            if (noRun) {
                //pix = smooth(pix, 2);
                // int tot = 0;
                //for (int x = 1; x <= pix.length / 4; x *= 2) {
                    // tot++;
                    // pix = add(pix, interpolateGradient(x));
                //}
                //for (int x = 0; x < pix.length; x++) {
                    //for (int y = 0; y < pix[0].length; y++) {
                        // pix[x][y] /= tot;
                    //}
                //}
            }
        }
        
        /**if (hasnt) {
	        pi = new float[width / 16];
	        
	        for (int x = 0; x < pi.length; x++) {
	        	pi[x] = (int) (Math.random() * width / 8 + width / 3);
	        }
	        
	        for (int x = 0; x < pi.length - 1; x++) {
	        	g.setColor(Color.black);
	            double d1, d2, d3, d4;
	            d2 = pi[x];
	            if (x == 0)
	                d1 = d2;
	            else
	                d1 = pi[x - 1];
	            d3 = pi[x + 1];
	            if (x == pi.length - 2)
	                d4 = d3;
	            else
	                d4 = pi[x + 2];
	
	            double a1, a2, a3, a4;
	            a4 = d2;
	            a3 = d3 / 2 - d1 / 2;
	            a2 = (d2 - d4) / 2 + 3 * d3 - 2 * a3 - 3 * a4;
	            a1 = d3 - a2 - a3 - a4;
	
	            for (double test = 0; test < 1; test += 1d / 16) {
	            	g.drawLine((int) (x * 16 + test * 16), (int) (a1 * Math.pow(test, 3) + a2
	                        * Math.pow(test, 2) + a3 * test + a4), (int) (x * 16 + (test + 1d / 16) * 16), (int) (a1 * Math.pow(test + 1d / 16, 3) + a2
	                                * Math.pow(test + 1d / 16, 2) + a3 * (test + 1d / 16) + a4));
	            }
	        }
	        hasnt = false;
        }**/
        
        for (int x = 0; x < pixi.length; x++) {
            for (int y = 0; y < pixi[0].length; y++) {
                pixi[x][y] = (float) (Math.random() * 256);
            }
        }
        pix = bicubicSpline(pixi, 64);

        // int fr = 32;
        // pix = interpolateGradient(fr);

        /**
         * int fer = 16;
         * 
         * float[][] rain = new float[pix.length / fer][pix.length / fer]; for
         * (int x = 0; x < rain.length; x++) { for (int y = 0; y < rain.length;
         * y++) { rain[x][y] = Math.random() * 255; } }
         * 
         * float[][] piex = new float[pix.length][pix.length]; for (int x = 0; x
         * < piex.length; x++) { for (int y = 0; y < piex.length; y++) {
         * piex[x][y] = rain[x / fer][y / fer]; } } piex = interpolate(piex,
         * fer);
         * 
         * int f = 8;
         * 
         * float[][] r = new float[pix.length / f][pix.length / f]; for (int x =
         * 0; x < r.length; x++) { for (int y = 0; y < r.length; y++) { r[x][y]
         * = Math.random() * 255; } } float[][] p = new
         * float[pix.length][pix.length]; for (int x = 0; x < p.length; x++) {
         * for (int y = 0; y
         * < p
         * .length; y++) { p[x][y] = r[x / f][y / f]; } } p = interpolate(p, f);
         * 
         * pix = add(piex, pix); pix = add(p, pix);
         **/

        float max = 0;
        float min = 255;
        for (int x = 0; x < pix.length; x++) {
            for (int y = 0; y < pix[0].length; y++) {
                // if (!random)
                // pix[x][y] /= 7;
                if (pix[x][y] > max)
                    max = pix[x][y];
                if (pix[x][y] < min)
                    min = pix[x][y];
            }
        }
        System.out.println("Min: " + min + "\nMax: " + max);

        float avg = 0;
        for (int x = 0; x < pix.length * pix.length; x++) {
            avg += pix[x % pix.length][x / pix.length];
            pix[x % pix.length][x / pix.length] -= min;
            pix[x % pix.length][x / pix.length] *= (255 / (max - min));
        }
        System.out.println("Average: " + (avg / (pix.length * pix.length)));
        for (int x = 0; x < pix.length; x++) {
            for (int y = 0; y < pix[0].length; y++) {
                if (pix[x][y] > max)
                    max = pix[x][y];
                if (pix[x][y] < min)
                    min = pix[x][y];
            }
        }
        System.out.println("min: " + min + "\nmax: " + max);

        for (int x = 0; x < pix.length; x++) {
            for (int y = 0; y < pix[0].length; y++) {
            	float val = pix[x][y];
            	if (val < 32) {
            		g.setColor(new Color(0, 0, 255));
            	} else if (val < 64) {
            		g.setColor(new Color(5, 32, 230));
            	} else if (val < 96) {
            		g.setColor(new Color(10, 64, 205));
            	} else if (val < 128) {
            		g.setColor(new Color(15, 96, 180));
            	} else if (val < 160) {
            		g.setColor(new Color(20, 128, 155));
            	} else if (val < 192) {
            		g.setColor(new Color(25, 160, 130));
            	} else if (val < 224) {
            		g.setColor(new Color(30, 192, 105));
            	} else {
            		g.setColor(new Color(35, 224, 80));
            	}
                //g.setColor(new Color((int) pix[x][y], (int) pix[x][y], (int) pix[x][y]));
                g.drawLine(x, y, x, y);
            }
        }

    }
}
