package webpifier;

public class FileUtil {
	public static String getFileNameWithoutExtension(String name) {
		int lastIndexOfDot = name.lastIndexOf('.');
		return lastIndexOfDot == -1 ? name : name.substring(0, lastIndexOfDot);
	}

	public static String getExtension(String name) {
		int lastIndexOfDot = name.lastIndexOf('.');
		return lastIndexOfDot == -1 ? null : name.substring(lastIndexOfDot + 1);
	}

	/**
	 * formats the bytes to a human readable format
	 *
	 * @param si
	 *            true if each kilo==1000, false if kilo==1024
	 */
	public static String humanReadableByteCount(final long bytes, final boolean si) {
		final int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		double result = bytes;
		final String unitsToUse = (si ? "k" : "K") + "MGTPE";
		int i = 0;
		final int unitsCount = unitsToUse.length();
		while (true) {
			result /= unit;
			if (result < unit)
				break;
			// check if we can go further:
			if (i == unitsCount - 1)
				break;
			++i;
		}
		final StringBuilder sb = new StringBuilder(9);
		sb.append(String.format("%.1f ", result));
		sb.append(unitsToUse.charAt(i));
		if (si)
			sb.append('B');
		else
			sb.append('i').append('B');
		final String resultStr = sb.toString();
		return resultStr;
	}
}
