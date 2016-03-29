package webpifier;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class ImageUtils {
	// http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fgraphics%2FImageData.html
	public static boolean hasAlpha(ImageData imageData) {
		if (imageData.type == SWT.IMAGE_JPEG)
			return false;
		if (imageData.alpha != -1 || imageData.transparentPixel != -1)
			return true;
		if (imageData.alphaData == null && imageData.maskData == null)
			return false;
		if (imageData.alphaData != null)
			for (int i = 0; i < imageData.alphaData.length; ++i) {
				byte alpha = imageData.alphaData[i];
				if (alpha != 0xFF)
					return true;
			}
		if (imageData.maskData != null)
			for (int i = 0; i < imageData.maskData.length; ++i)
				if (imageData.maskData[i] != 0xFF)
					return true;
		return false;
	}

	public static void saveToJpg(ImageData image, int compression, File f) {
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image };
		loader.compression = compression;
		loader.save(f.getAbsolutePath(), SWT.IMAGE_JPEG);
	}
}
