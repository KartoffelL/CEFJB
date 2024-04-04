package Kartoffel.Licht;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Not finished
 */
public class IcoDecoder {

	static BufferedImage[] decode(byte[] in) throws IOException {
		InputStream is = new ByteArrayInputStream(in);
		readShort(is); //Should be 0
		readShort(is);
		short numImages = readShort(is);
		BufferedImage[] imgs = new BufferedImage[numImages];
		for(int im = 0; im < numImages; im++) {
			byte width = (byte) is.read();
			byte height = (byte) is.read();
			byte colorpl = (byte) is.read();
			is.read(); //Should be 0
			short oth1 = readShort(is);
			short oth2 = readShort(is);
			int size = readInt(is);
			int offset = readInt(is);
			ByteArrayInputStream bais = new ByteArrayInputStream(in, offset, size);
			bais.mark(0);
			imgs[im] = ImageIO.read(bais);
			System.out.println(imgs[im] + " " + width + " " + height + " " + colorpl + " " + oth1 + " " + oth2);
		}
		is.close();
		return imgs;
	}
	
	private static short readShort(InputStream is) throws IOException {
		byte a = (byte) is.read();
		byte b = (byte) is.read();
		return (short) ((b & 0xff) << 8 | (a & 0xff));
	}
	private static int readInt(InputStream is) throws IOException {
		byte a = (byte) is.read();
		byte b = (byte) is.read();
		byte c = (byte) is.read();
		byte d = (byte) is.read();
		int ret = 0;
		ret <<= 8;
	    ret |= d & 0xFF;
	    ret <<= 8;
	    ret |= c & 0xFF;
	    ret <<= 8;
	    ret |= b & 0xFF;
	    ret <<= 8;
	    ret |= a & 0xFF;
	    return ret;
	}

}
