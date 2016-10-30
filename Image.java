package newGame;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	
	private Color[] c;
	private byte[] bite;
	
	public Image(String address) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(address));
		} catch (Exception e) {
			System.err.println("Found it");
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "png", baos);
			baos.flush();
			bite = baos.toByteArray();
			baos.close();
		} catch (Exception e) {
			System.err.println("Person is wrong");
		}
	}
	
	public void print() {
		for (byte b : bite) {
			System.out.println(b);
		}
	}
	
}
