package webpifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class Main extends Shell {
	private Text pathOfAndroidProject;
	private Button btnUpToApi13;
	private Button btnApi14To17;
	private Button btnConvertNonTransparentImagestoJpg;
	private Button btnConvertNonTransparentImagesToWebp;
	private Button btnConvertTransparentImagesToWebp;
	private Scale scaleJpegEncoderQuality;
	private Button btnPng2WebpShouldStayLossless;
	private Label lblWebpEncoderQuality;
	private Scale scaleWebpEncoderQuality;
	private Button btnJpgToWebpShouldBecomeLossless;
	private Label lblJpgEncoderQuality;
	private Button btnDeleteOriginalFile;
	private Button btnHandleOnlyFilesFromResFolder;
	private Button btnDoIt;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Label lbStatus;
	private Menu menu;
	private MenuItem mntmAbout;
	private Button btnApi18OrAbove;

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	public static void main(String args[]) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			@Override
			public void run() {
				try {
					Display display = Display.getDefault();
					Main shell = new Main(display);
					// shell.pack();
					SwtTools.centerShell(shell);
					shell.open();
					shell.layout();
					while (!shell.isDisposed()) {
						if (!display.readAndDispatch()) {
							display.sleep();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void getImagesFilesToHandle(webpifier.Configuration configuration,
			ConcurrentLinkedDeque<File> filesToScan, ArrayList<String> result) {
		// ArrayList<File> drawableFolders = new ArrayList<>();
		while (!filesToScan.isEmpty()) {
			File file = filesToScan.removeFirst();
			String fileName = file.getName();
			// handle images
			if (!file.isDirectory()) {
				if ((fileName.endsWith(".jpg") || (fileName.endsWith(".png") && !fileName.endsWith(".9.png")))) {
					if (!configuration.handleOnlyFilesFromResFolder) {
						result.add(file.getAbsolutePath());
						continue;
					}
					File parent = file.getParentFile();
					final String name = parent.getName();
					if (!parent.isDirectory() || (!name.startsWith("mipmap") && !name.startsWith("drawable")))
						continue;
					parent = parent.getParentFile();
					if (!parent.isDirectory() || !"res".equals(parent.getName()))
						continue;
					result.add(file.getAbsolutePath());
				}
				continue;
			}
			// handle folder
			if (configuration.handleOnlyFilesFromResFolder
					&& (fileName.startsWith(".") || "build".equals(fileName) || "gradle".equals(fileName)))
				continue;
			File[] children = file.listFiles();
			for (File child : children)
				filesToScan.add(child);
		}
	}

	/**
	 * Create the shell.
	 *
	 * @param display
	 */
	public Main(Display display) {
		super(display, SWT.SHELL_TRIM);
		createContents();
		setLayout(new FormLayout());
		DropTarget dt = new DropTarget(this, DND.DROP_DEFAULT | DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { FileTransfer.getInstance() });

		pathOfAndroidProject = new Text(this, SWT.BORDER);
		FormData fd_pathOfAndroidProject = new FormData();
		fd_pathOfAndroidProject.right = new FormAttachment(0, 594);
		fd_pathOfAndroidProject.top = new FormAttachment(0, 18);
		fd_pathOfAndroidProject.left = new FormAttachment(0, 152);
		pathOfAndroidProject.setLayoutData(fd_pathOfAndroidProject);
		// pathOfAndroidProject.setText("");

		Label lblPathOfAndroid = new Label(this, SWT.NONE);
		FormData fd_lblPathOfAndroid = new FormData();
		fd_lblPathOfAndroid.right = new FormAttachment(0, 146);
		fd_lblPathOfAndroid.top = new FormAttachment(0, 21);
		fd_lblPathOfAndroid.left = new FormAttachment(0, 10);
		lblPathOfAndroid.setLayoutData(fd_lblPathOfAndroid);
		lblPathOfAndroid.setText("paths to handle");

		btnDoIt = new Button(this, SWT.NONE);
		FormData fd_btnDoIt = new FormData();
		fd_btnDoIt.left = new FormAttachment(0, 285);
		btnDoIt.setLayoutData(fd_btnDoIt);
		btnDoIt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String pathsStr = pathOfAndroidProject.getText();
				if (pathsStr == null || pathsStr.isEmpty())
					return;
				btnDoIt.setEnabled(false);
				lbStatus.setText("processing...");
				final webpifier.Configuration configuration = new webpifier.Configuration();
				configuration.convertNonTransparentImagestoJpg = Main.this.btnConvertNonTransparentImagestoJpg
						.getSelection() && btnConvertNonTransparentImagestoJpg.isEnabled();
				configuration.convertNonTransparentImagesToWebp = Main.this.btnConvertNonTransparentImagesToWebp
						.getSelection() && btnConvertNonTransparentImagesToWebp.isEnabled();
				configuration.convertTransparentImagesToWebp = Main.this.btnConvertTransparentImagesToWebp
						.getSelection() && btnConvertTransparentImagesToWebp.isEnabled();
				configuration.png2WebpShouldStayLossless = Main.this.btnPng2WebpShouldStayLossless.getSelection()
						&& btnPng2WebpShouldStayLossless.isEnabled();
				configuration.jpegEncoderQuality = Main.this.scaleJpegEncoderQuality.getSelection();
				configuration.webpEncoderQuality = Main.this.scaleWebpEncoderQuality.getSelection();
				configuration.jpgToWebpShouldBecomeLossless = btnJpgToWebpShouldBecomeLossless.getSelection()
						&& btnJpgToWebpShouldBecomeLossless.isEnabled();
				configuration.handleOnlyFilesFromResFolder = btnHandleOnlyFilesFromResFolder.getSelection();
				configuration.deleteOriginalFile = btnDeleteOriginalFile.getSelection();
				new Thread() {
					@Override
					public void run() {
						String[] splitPaths = pathsStr.split(";");
						ConcurrentLinkedDeque<File> foldersToScan = new ConcurrentLinkedDeque<>();
						for (String path : splitPaths)
							foldersToScan.add(new File(path));

						ArrayList<String> result = new ArrayList<>();
						getImagesFilesToHandle(configuration, foldersToScan, result);
						// System.out.println("result paths:" +
						// CollectionUtil.toString(result));
						convertFiles(result, configuration);
					};
				}.start();
			}
		});
		btnDoIt.setText("Do it");

		Label lblMinsdk = new Label(this, SWT.NONE);
		FormData fd_lblMinsdk = new FormData();
		fd_lblMinsdk.right = new FormAttachment(0, 65);
		fd_lblMinsdk.top = new FormAttachment(0, 64);
		fd_lblMinsdk.left = new FormAttachment(0, 10);
		lblMinsdk.setLayoutData(fd_lblMinsdk);
		lblMinsdk.setText("minSdk");

		Group group = new Group(this, SWT.NONE);
		fd_btnDoIt.right = new FormAttachment(group, -10, SWT.RIGHT);
		FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(0, 90);
		fd_group.right = new FormAttachment(0, 374);
		fd_group.top = new FormAttachment(0, 45);
		fd_group.left = new FormAttachment(0, 65);
		group.setLayoutData(fd_group);
		btnUpToApi13 = new Button(group, SWT.RADIO);
		btnUpToApi13.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtonsStateAccordingToSelectedMinSdk();
			}
		});
		btnUpToApi13.setBounds(3, 16, 90, 16);
		btnUpToApi13.setText("up to API 13");

		btnApi14To17 = new Button(group, SWT.RADIO);
		btnApi14To17.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtonsStateAccordingToSelectedMinSdk();
			}
		});
		btnApi14To17.setBounds(99, 16, 71, 16);
		btnApi14To17.setText("API 14-17");
		btnApi14To17.setSelection(true);

		btnApi18OrAbove = new Button(group, SWT.RADIO);
		btnApi18OrAbove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtonsStateAccordingToSelectedMinSdk();
			}
		});
		btnApi18OrAbove.setBounds(193, 16, 106, 16);
		btnApi18OrAbove.setText("API 18 or above");

		btnConvertNonTransparentImagestoJpg = new Button(this, SWT.CHECK);
		FormData fd_btnConvertNonTransparentImagestoJpg = new FormData();
		fd_btnConvertNonTransparentImagestoJpg.right = new FormAttachment(0, 594);
		fd_btnConvertNonTransparentImagestoJpg.top = new FormAttachment(0, 96);
		fd_btnConvertNonTransparentImagestoJpg.left = new FormAttachment(0, 10);
		btnConvertNonTransparentImagestoJpg.setLayoutData(fd_btnConvertNonTransparentImagestoJpg);
		btnConvertNonTransparentImagestoJpg.setSelection(true);
		btnConvertNonTransparentImagestoJpg
				.setText("convert non transparent images to jpg (and delete created ones if webp takes less)");

		btnConvertNonTransparentImagesToWebp = new Button(this, SWT.CHECK);
		FormData fd_btnConvertNonTransparentImagesToWebp = new FormData();
		fd_btnConvertNonTransparentImagesToWebp.right = new FormAttachment(0, 269);
		fd_btnConvertNonTransparentImagesToWebp.top = new FormAttachment(0, 118);
		fd_btnConvertNonTransparentImagesToWebp.left = new FormAttachment(0, 10);
		btnConvertNonTransparentImagesToWebp.setLayoutData(fd_btnConvertNonTransparentImagesToWebp);
		btnConvertNonTransparentImagesToWebp.setSelection(true);
		btnConvertNonTransparentImagesToWebp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnConvertNonTransparentImagesToWebp.setText("convert non transparent images to webp");

		btnConvertTransparentImagesToWebp = new Button(this, SWT.CHECK);
		FormData fd_btnConvertTransparentImagesToWebp = new FormData();
		fd_btnConvertTransparentImagesToWebp.right = new FormAttachment(0, 300);
		fd_btnConvertTransparentImagesToWebp.top = new FormAttachment(0, 140);
		fd_btnConvertTransparentImagesToWebp.left = new FormAttachment(0, 10);
		btnConvertTransparentImagesToWebp.setLayoutData(fd_btnConvertTransparentImagesToWebp);
		btnConvertTransparentImagesToWebp.setSelection(true);
		btnConvertTransparentImagesToWebp.setText("convert transparent images to webp");

		lblJpgEncoderQuality = new Label(this, SWT.NONE);
		FormData fd_lblJpgEncoderQuality = new FormData();
		fd_lblJpgEncoderQuality.left = new FormAttachment(0, 74);
		lblJpgEncoderQuality.setLayoutData(fd_lblJpgEncoderQuality);
		lblJpgEncoderQuality.setAlignment(SWT.CENTER);
		scaleJpegEncoderQuality = new Scale(this, SWT.NONE);
		fd_lblJpgEncoderQuality.bottom = new FormAttachment(scaleJpegEncoderQuality, -6);
		fd_lblJpgEncoderQuality.right = new FormAttachment(scaleJpegEncoderQuality, 0, SWT.RIGHT);
		FormData fd_scaleJpegEncoderQuality = new FormData();
		scaleJpegEncoderQuality.setLayoutData(fd_scaleJpegEncoderQuality);
		scaleJpegEncoderQuality.setSelection(80);
		lblJpgEncoderQuality.setText("jpg encoder quality:" + scaleJpegEncoderQuality.getSelection());
		scaleJpegEncoderQuality.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblJpgEncoderQuality.setText("jpg encoder quality:" + scaleJpegEncoderQuality.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnPng2WebpShouldStayLossless = new Button(this, SWT.CHECK);
		FormData fd_btnPng2WebpShouldStayLossless = new FormData();
		fd_btnPng2WebpShouldStayLossless.top = new FormAttachment(btnConvertNonTransparentImagestoJpg, 6);
		fd_btnPng2WebpShouldStayLossless.right = new FormAttachment(0, 643);
		btnPng2WebpShouldStayLossless.setLayoutData(fd_btnPng2WebpShouldStayLossless);
		btnPng2WebpShouldStayLossless.setText("non-transparent png->webp should stay lossless");

		lblWebpEncoderQuality = new Label(this, SWT.NONE);
		FormData fd_lblWebpEncoderQuality = new FormData();
		fd_lblWebpEncoderQuality.left = new FormAttachment(100, -288);
		lblWebpEncoderQuality.setLayoutData(fd_lblWebpEncoderQuality);
		lblWebpEncoderQuality.setAlignment(SWT.CENTER);

		scaleWebpEncoderQuality = new Scale(this, SWT.NONE);
		fd_scaleJpegEncoderQuality.right = new FormAttachment(scaleWebpEncoderQuality, -82);
		fd_scaleJpegEncoderQuality.bottom = new FormAttachment(scaleWebpEncoderQuality, 0, SWT.BOTTOM);
		fd_lblWebpEncoderQuality.bottom = new FormAttachment(scaleWebpEncoderQuality, -6);
		fd_lblWebpEncoderQuality.right = new FormAttachment(scaleWebpEncoderQuality, 0, SWT.RIGHT);
		FormData fd_scaleWebpEncoderQuality = new FormData();
		fd_scaleWebpEncoderQuality.bottom = new FormAttachment(100, -94);
		fd_scaleWebpEncoderQuality.right = new FormAttachment(100, -108);
		scaleWebpEncoderQuality.setLayoutData(fd_scaleWebpEncoderQuality);
		scaleWebpEncoderQuality.setSelection(80);
		lblWebpEncoderQuality.setText("webp encoder quality:" + scaleWebpEncoderQuality.getSelection());

		btnJpgToWebpShouldBecomeLossless = new Button(this, SWT.CHECK);
		fd_btnPng2WebpShouldStayLossless.left = new FormAttachment(btnJpgToWebpShouldBecomeLossless, 0, SWT.LEFT);
		FormData fd_btnJpgToWebpShouldBecomeLossless = new FormData();
		fd_btnJpgToWebpShouldBecomeLossless.top = new FormAttachment(btnConvertTransparentImagesToWebp, 0, SWT.TOP);
		fd_btnJpgToWebpShouldBecomeLossless.right = new FormAttachment(100, -10);
		fd_btnJpgToWebpShouldBecomeLossless.left = new FormAttachment(0, 355);
		btnJpgToWebpShouldBecomeLossless.setLayoutData(fd_btnJpgToWebpShouldBecomeLossless);
		btnJpgToWebpShouldBecomeLossless.setText("jpg->webp should become lossless");

		btnDeleteOriginalFile = new Button(this, SWT.CHECK);
		FormData fd_btnDeleteOriginalFile = new FormData();
		fd_btnDeleteOriginalFile.top = new FormAttachment(btnConvertTransparentImagesToWebp, 6);
		fd_btnDeleteOriginalFile.left = new FormAttachment(lblPathOfAndroid, 0, SWT.LEFT);
		btnDeleteOriginalFile.setLayoutData(fd_btnDeleteOriginalFile);
		btnDeleteOriginalFile.setText("delete original file if converted to smaller sized image file");
		btnDeleteOriginalFile.setSelection(true);

		btnHandleOnlyFilesFromResFolder = new Button(this, SWT.CHECK);
		btnHandleOnlyFilesFromResFolder.setSelection(true);
		FormData fd_btnHandleOnlyFilesFromResFolder = new FormData();
		fd_btnHandleOnlyFilesFromResFolder.bottom = new FormAttachment(btnDeleteOriginalFile, 0, SWT.BOTTOM);
		fd_btnHandleOnlyFilesFromResFolder.left = new FormAttachment(btnPng2WebpShouldStayLossless, 0, SWT.LEFT);
		btnHandleOnlyFilesFromResFolder.setLayoutData(fd_btnHandleOnlyFilesFromResFolder);
		btnHandleOnlyFilesFromResFolder.setText("Handle only files from res/drawable and res/mipmap");

		lbStatus = new Label(this, SWT.NONE);
		fd_btnDoIt.bottom = new FormAttachment(lbStatus, -17);
		FormData fd_lbStatus = new FormData();
		fd_lbStatus.right = new FormAttachment(btnPng2WebpShouldStayLossless, 0, SWT.RIGHT);
		fd_lbStatus.left = new FormAttachment(lblPathOfAndroid, 0, SWT.LEFT);
		fd_lbStatus.bottom = new FormAttachment(100, -10);
		lbStatus.setLayoutData(fd_lbStatus);
		formToolkit.adapt(lbStatus, true, true);
		lbStatus.setText("Awaiting commands...");

		menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);

		mntmAbout = new MenuItem(menu, SWT.NONE);
		mntmAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogWithUrls.openInformation(getShell(), "Info",
						"<a href=\"https://github.com/AndroidDeveloperLB/WebpifyYourAndroidApp\">Repository website</a>\n"
								+ "<a href=\"https://github.com/AndroidDeveloperLB?tab=repositories\">All my repositories</a>\n"
								+ "<a href=\"https://play.google.com/store/apps/developer?id=AndroidDeveloperLB\">All my apps</a>\n");
			}
		});
		mntmAbout.setText("About");
		scaleWebpEncoderQuality.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblWebpEncoderQuality.setText("webp encoder quality:" + scaleWebpEncoderQuality.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		dt.addDropListener(new DropTargetAdapter() {
			@Override
			public void drop(DropTargetEvent event) {
				String fileList[] = null;
				FileTransfer ft = FileTransfer.getInstance();
				if (ft.isSupportedType(event.currentDataType) && event.data != null) {
					fileList = (String[]) event.data;
					StringBuilder sb = new StringBuilder();
					for (String string : fileList) {
						sb.append(string).append(";");
					}
					pathOfAndroidProject.setText(sb.toString());
					// for (String string : fileList) {
					// System.out.println(string);
					// }
				}
			}
		});
		updateButtonsStateAccordingToSelectedMinSdk();

	}

	private void updateButtonsStateAccordingToSelectedMinSdk() {
		if (btnApi18OrAbove.getSelection()) {
			btnConvertNonTransparentImagesToWebp.setEnabled(true);
			btnPng2WebpShouldStayLossless.setEnabled(true);
			btnConvertTransparentImagesToWebp.setEnabled(true);
			scaleWebpEncoderQuality.setEnabled(true);
			btnJpgToWebpShouldBecomeLossless.setEnabled(true);
		} else if (btnApi14To17.getSelection()) {
			btnConvertNonTransparentImagesToWebp.setEnabled(true);
			btnPng2WebpShouldStayLossless.setEnabled(true);
			btnConvertTransparentImagesToWebp.setEnabled(false);
			scaleWebpEncoderQuality.setEnabled(true);
			btnJpgToWebpShouldBecomeLossless.setEnabled(true);
		} else if (btnUpToApi13.getSelection()) {
			btnConvertNonTransparentImagesToWebp.setEnabled(false);
			btnPng2WebpShouldStayLossless.setEnabled(false);
			btnConvertTransparentImagesToWebp.setEnabled(false);
			scaleWebpEncoderQuality.setEnabled(false);
			btnJpgToWebpShouldBecomeLossless.setEnabled(false);
		}
	}

	private File getPngAndJpgToWebpConversionFile(String mainPathOfCurrentApp) {
		File convertersFolder = new File(mainPathOfCurrentApp, "external");
		if (!convertersFolder.exists() || !convertersFolder.isDirectory())
			return null;
		File[] list = convertersFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				String fileNameWithoutExtension = FileUtil.getFileNameWithoutExtension(name);
				File file = new File(dir, name);
				return "cwebp".equals(fileNameWithoutExtension) && !file.isDirectory() && file.canExecute();
			}
		});
		return CollectionUtil.isEmpty(list) ? null : list[0];
	}

	private void convertFiles(ArrayList<String> filesToCheck, webpifier.Configuration configuration) {
		final String mainPathOfCurrentApp = Paths.get(".").toAbsolutePath().normalize().toString();
		Display display = new Display();
		// search for conversion files
		final File pngAndJpgToWebpConverter = getPngAndJpgToWebpConversionFile(mainPathOfCurrentApp);
		if (pngAndJpgToWebpConverter == null)
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					DialogWithUrls.openError(getShell(), "Error", "error: cannot find converter file \"cwebp\"\n"
							+ "This file is needed for convertion into webp files.\n"
							+ "you can find it <a href=\"https://storage.googleapis.com/downloads.webmproject.org/releases/webp/index.html\">here</a>");
				}
			});
		//
		int filesHandledSoFar = 0;
		final int numberOfFilesToHandle = filesToCheck.size();
		long totalSpaceSaved = 0;
		for (String filePath : filesToCheck) {
			Image image;
			File jpgFile = null, webpFile = null;
			final File file = new File(filePath);
			try {
				FileInputStream fileInputStream = new FileInputStream(filePath);
				image = new Image(display, fileInputStream);
				String name = file.getName();
				String nameWithoutExtension = FileUtil.getFileNameWithoutExtension(name);
				String extension = FileUtil.getExtension(name);
				ImageData imageData = image.getImageData();
				image.dispose();
				fileInputStream.close();
				boolean hasAlpha = ImageUtils.hasAlpha(imageData);
				boolean allowConversionToWebp = false;
				allowConversionToWebp |= configuration.convertNonTransparentImagesToWebp && !hasAlpha;
				allowConversionToWebp |= configuration.convertTransparentImagesToWebp && hasAlpha;
				allowConversionToWebp &= pngAndJpgToWebpConverter != null;
				boolean allowConversionToJpeg = configuration.convertNonTransparentImagestoJpg && !hasAlpha;
				if (!allowConversionToJpeg && !allowConversionToWebp)
					// no conversion allowed, so skip
					continue;
				boolean isPngFile = "png".equalsIgnoreCase(extension) || imageData.type == SWT.IMAGE_PNG;
				boolean isJpgFile = "jpeg".equalsIgnoreCase(extension) || "jpg".equalsIgnoreCase(extension)
						|| imageData.type == SWT.IMAGE_JPEG;
				if (!isJpgFile && allowConversionToJpeg) {
					// convert to jpg, if image has no transparency
					jpgFile = new File(file.getParent(), nameWithoutExtension + ".jpg");
					ImageUtils.saveToJpg(imageData, configuration.jpegEncoderQuality, jpgFile);
				}
				if (allowConversionToWebp) {
					webpFile = new File(file.getParent(), nameWithoutExtension + ".webp");
					String command;
					boolean useLossless = hasAlpha;
					if (isPngFile && (hasAlpha || configuration.png2WebpShouldStayLossless)) {
						useLossless = true;
					}
					if (isJpgFile)
						useLossless = configuration.jpgToWebpShouldBecomeLossless;
					// convert to webp
					if (useLossless)
						command = pngAndJpgToWebpConverter.getAbsolutePath()
								+ " -q 100 -m 6 -mt -lossless -alpha_cleanup -quiet -alpha_q 100" + " \"" + filePath
								+ "\" -o \"" + webpFile.getAbsolutePath() + "\"";
					else
						command = pngAndJpgToWebpConverter.getAbsolutePath() + " -q " + configuration.webpEncoderQuality
								+ " -m 6 -mt -quiet -noalpha \"" + filePath + "\" -o \"" + webpFile.getAbsolutePath()
								+ "\"";
					try {
						Process p = Runtime.getRuntime().exec(command);
						p.waitFor();
					} catch (Exception e) {
						// TODO show error if needed
						e.printStackTrace();
					}
				}
				final List<File> filesHandled = new ArrayList<File>();
				filesHandled.add(file);
				if (jpgFile != null && jpgFile.exists())
					filesHandled.add(jpgFile);
				if (webpFile != null && webpFile.exists())
					filesHandled.add(webpFile);
				// sort files. First is largest. Last is smallest
				Collections.sort(filesHandled, new Comparator<File>() {

					@Override
					public int compare(File arg0, File arg1) {
						return arg0.length() > arg1.length() ? -1 : arg0.length() < arg1.length() ? 1 : 0;
					}
				});
				final long originalFileSize = file.length(),
						smallestFileSize = filesHandled.get(filesHandled.size() - 1).length();
				final long spaceSaved = originalFileSize - smallestFileSize;
				for (int i = 0; i < filesHandled.size() - 1; ++i) {
					File f = filesHandled.get(i);
					if (!f.equals(file) || configuration.deleteOriginalFile) {
						try {
							Files.delete(FileSystems.getDefault().getPath(f.getAbsolutePath()));
						} catch (Exception e) {
							System.out.println("failed deleting:" + e);
						}
					}
				}
				++filesHandledSoFar;
				final int progress = filesHandledSoFar;
				totalSpaceSaved += spaceSaved;
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						// show progress
						lbStatus.setText(progress + "/" + numberOfFilesToHandle + " saved:"
								+ FileUtil.humanReadableByteCount(spaceSaved, false));
					}
				});
			} catch (IOException e) {
				System.out.println("error with file:" + filePath);
				e.printStackTrace();
			}
		}
		display.dispose();
		final long finalTotalSpaceSaved = totalSpaceSaved;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				btnDoIt.setEnabled(true);
				lbStatus.setText("processed " + numberOfFilesToHandle + " files. Total saved:"
						+ FileUtil.humanReadableByteCount(finalTotalSpaceSaved, false));
			}
		});

	}

	protected void createContents() {
		setText("Webpify your Android app");
		setSize(669, 393);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
