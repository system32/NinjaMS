package net.sf.odinms.provider.xmlwz;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.FileImageInputStream;

import net.sf.odinms.provider.MapleCanvas;

import com.sun.imageio.plugins.png.PNGImageReaderSpi;

public class FileStoredPngMapleCanvas implements MapleCanvas {
	private File file;
	private int width;
	private int height;
	private BufferedImage image;
	
	public FileStoredPngMapleCanvas(int width, int height, File fileIn) {
		this.width = width;
		this.height = height;
		this.file = fileIn;
	}
	
	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public BufferedImage getImage() {
		loadImageIfNescessary();
		return image;
	}
	
	private void loadImageIfNescessary() {
		if (image == null) {
			IIORegistry iioRegistry = IIORegistry.getDefaultInstance();
			ImageReaderSpi readerSpi = iioRegistry.getServiceProviderByClass(PNGImageReaderSpi.class);
			FileImageInputStream fiis = null;
			try {
				ImageReader reader = readerSpi.createReaderInstance();
				fiis = new FileImageInputStream(file);
				reader.setInput(fiis);
				image = reader.read(0);
				// replace the dimensions loaded from the wz by the REAL dimensions from the image - should be equal tho
				width = image.getWidth();
				height = image.getHeight();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (fiis != null) {
					try {
						fiis.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
}
