package webpifier;
public class Configuration {

	protected boolean convertNonTransparentImagestoJpg;
	protected boolean convertNonTransparentImagesToWebp;
	protected boolean convertTransparentImagesToWebp;
	protected boolean png2WebpShouldStayLossless;
	protected int jpegEncoderQuality;
	protected int webpEncoderQuality;
	protected boolean jpgToWebpShouldBecomeLossless;
	protected boolean deleteOriginalFile;
	protected boolean handleOnlyFilesFromResFolder;

}
