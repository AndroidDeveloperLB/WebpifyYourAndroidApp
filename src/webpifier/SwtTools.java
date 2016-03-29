package webpifier;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public class SwtTools {
	public static void centerShell(Shell shell) {
		final Rectangle bounds = shell.getDisplay().getPrimaryMonitor().getBounds();
		// final Rectangle bounds = shell.getParent() == null ?
		// shell.getDisplay().getPrimaryMonitor().getBounds()
		// : shell.getParent().getBounds();
		final Rectangle rect = shell.getBounds();
		// int x = bounds.x + (bounds.width - rect.width) / 2;
		// int y = bounds.y + (bounds.height - rect.height) / 2;
		// Point size = shell.getSize();
		// shell.setLocation(bounds.x + (bounds.width - size.x) / 2, bounds.y +
		// (bounds.height - size.y) / 2);
		shell.setLocation(bounds.x + (bounds.width - rect.width) / 2, bounds.y + (bounds.height - rect.height) / 2);

		// shell.setLocation(bounds.x, bounds.y);

	}
}
