package webpifier;

import java.awt.Desktop;
import java.io.IOException;

/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

//http://stackoverflow.com/a/21676351/878126
/**
 * A dialog for showing messages to the user.
 * <p>
 * This concrete dialog class can be instantiated as is, or further subclassed
 * as required.
 * </p>
 * <p>
 * <strong>Note:</strong> This class does not use button IDs from
 * IDialogConstants. Instead, the ID is the index of the button in the supplied
 * array.
 * </p>
 */
public class DialogWithUrls extends IconAndMessageDialog {

	Link linkLabel;

	/**
	 * Constant for no image (value 0).
	 *
	 * @see #MessageDialog(Shell, String, Image, String, int, String[], int)
	 */
	public final static int NONE = 0;

	/**
	 * Constant for the error image, or a simple dialog with the error image and
	 * a single OK button (value 1).
	 *
	 * @see #MessageDialog(Shell, String, Image, String, int, String[], int)
	 * @see #open(int, Shell, String, String, int)
	 */
	public final static int ERROR = 1;

	/**
	 * Constant for the info image, or a simple dialog with the info image and a
	 * single OK button (value 2).
	 *
	 * @see #MessageDialog(Shell, String, Image, String, int, String[], int)
	 * @see #open(int, Shell, String, String, int)
	 */
	public final static int INFORMATION = 2;

	/**
	 * Constant for the question image, or a simple dialog with the question
	 * image and Yes/No buttons (value 3).
	 *
	 * @see #MessageDialog(Shell, String, Image, String, int, String[], int)
	 * @see #open(int, Shell, String, String, int)
	 */
	public final static int QUESTION = 3;

	/**
	 * Constant for the warning image, or a simple dialog with the warning image
	 * and a single OK button (value 4).
	 *
	 * @see #MessageDialog(Shell, String, Image, String, int, String[], int)
	 * @see #open(int, Shell, String, String, int)
	 */
	public final static int WARNING = 4;

	/**
	 * Constant for a simple dialog with the question image and OK/Cancel
	 * buttons (value 5).
	 *
	 * @see #open(int, Shell, String, String, int)
	 * @since 3.5
	 */
	public final static int CONFIRM = 5;

	/**
	 * Constant for a simple dialog with the question image and Yes/No/Cancel
	 * buttons (value 6).
	 *
	 * @see #open(int, Shell, String, String, int)
	 * @since 3.5
	 */
	public final static int QUESTION_WITH_CANCEL = 6;

	/**
	 * Labels for buttons in the button bar (localized strings).
	 */
	private String[] buttonLabels;

	/**
	 * The buttons. Parallels <code>buttonLabels</code>.
	 */
	private Button[] buttons;

	/**
	 * Index into <code>buttonLabels</code> of the default button.
	 */
	private int defaultButtonIndex;

	/**
	 * Dialog title (a localized string).
	 */
	private String title;

	/**
	 * Dialog title image.
	 */
	private Image titleImage;

	/**
	 * Image, or <code>null</code> if none.
	 */
	private Image image = null;

	/**
	 * The custom dialog area.
	 */
	private Control customArea;

	/**
	 * Create a message dialog. Note that the dialog will have no visual
	 * representation (no widgets) until it is told to open.
	 * <p>
	 * The labels of the buttons to appear in the button bar are supplied in
	 * this constructor as an array. The <code>open</code> method will return
	 * the index of the label in this array corresponding to the button that was
	 * pressed to close the dialog.
	 * </p>
	 * <p>
	 * <strong>Note:</strong> If the dialog was dismissed without pressing a
	 * button (ESC key, close box, etc.) then {@link SWT#DEFAULT} is returned.
	 * Note that the <code>open</code> method blocks.
	 * </p>
	 *
	 * @param parentShell
	 *            the parent shell
	 * @param dialogTitle
	 *            the dialog title, or <code>null</code> if none
	 * @param dialogTitleImage
	 *            the dialog title image, or <code>null</code> if none
	 * @param dialogMessage
	 *            the dialog message
	 * @param dialogImageType
	 *            one of the following values:
	 *            <ul>
	 *            <li><code>MessageDialog.NONE</code> for a dialog with no image
	 *            </li>
	 *            <li><code>MessageDialog.ERROR</code> for a dialog with an
	 *            error image</li>
	 *            <li><code>MessageDialog.INFORMATION</code> for a dialog with
	 *            an information image</li>
	 *            <li><code>MessageDialog.QUESTION </code> for a dialog with a
	 *            question image</li>
	 *            <li><code>MessageDialog.WARNING</code> for a dialog with a
	 *            warning image</li>
	 *            </ul>
	 * @param dialogButtonLabels
	 *            an array of labels for the buttons in the button bar
	 * @param defaultIndex
	 *            the index in the button label array of the default button
	 */
	public DialogWithUrls(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
			int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell);
		this.title = dialogTitle;
		this.titleImage = dialogTitleImage;
		this.message = dialogMessage;

		switch (dialogImageType) {
		case ERROR: {
			this.image = getErrorImage();
			break;
		}
		case INFORMATION: {
			this.image = getInfoImage();
			break;
		}
		case QUESTION:
		case QUESTION_WITH_CANCEL:
		case CONFIRM: {
			this.image = getQuestionImage();
			break;
		}
		case WARNING: {
			this.image = getWarningImage();
			break;
		}
		}
		this.buttonLabels = dialogButtonLabels;
		this.defaultButtonIndex = defaultIndex;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		close();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.
	 * Shell)
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
		if (titleImage != null) {
			shell.setImage(titleImage);
		}
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		buttons = new Button[buttonLabels.length];
		for (int i = 0; i < buttonLabels.length; i++) {
			String label = buttonLabels[i];
			Button button = createButton(parent, i, label, defaultButtonIndex == i);
			buttons[i] = button;
		}
	}

	/**
	 * Creates and returns the contents of an area of the dialog which appears
	 * below the message and above the button bar.
	 * <p>
	 * The default implementation of this framework method returns
	 * <code>null</code>. Subclasses may override.
	 * </p>
	 *
	 * @param parent
	 *            parent composite to contain the custom area
	 * @return the custom area control, or <code>null</code>
	 */
	protected Control createCustomArea(Composite parent) {
		return null;
	}

	/**
	 * This implementation of the <code>Dialog</code> framework method creates
	 * and lays out a composite and calls <code>createMessageArea</code> and
	 * <code>createCustomArea</code> to populate it. Subclasses should override
	 * <code>createCustomArea</code> to add contents below the message.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		// create message area
		createMessageArea(parent);
		// create the top level composite for the dialog area
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		composite.setLayoutData(data);
		// allow subclasses to add custom controls
		customArea = createCustomArea(composite);
		// If it is null create a dummy label for spacing purposes
		if (customArea == null) {
			customArea = new Label(composite, SWT.NULL);
		}
		return composite;
	}

	/**
	 * Gets a button in this dialog's button bar.
	 *
	 * @param index
	 *            the index of the button in the dialog's button bar
	 * @return a button in the dialog's button bar, or <code>null</code> if
	 *         there's no button with that index
	 */
	@Override
	protected Button getButton(int index) {
		if (buttons == null || index < 0 || index >= buttons.length)
			return null;
		return buttons[index];
	}

	/**
	 * Returns the minimum message area width in pixels This determines the
	 * minimum width of the dialog.
	 * <p>
	 * Subclasses may override.
	 * </p>
	 *
	 * @return the minimum message area width (in pixels)
	 */
	protected int getMinimumMessageWidth() {
		return convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
	}

	/**
	 * Handle the shell close. Set the return code to <code>SWT.DEFAULT</code>
	 * as there has been no explicit close by the user.
	 *
	 * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
	 */
	@Override
	protected void handleShellCloseEvent() {
		// Sets a return code of SWT.DEFAULT since none of the dialog buttons
		// were pressed to close the dialog.
		super.handleShellCloseEvent();
		setReturnCode(SWT.DEFAULT);
	}

	/**
	 * Opens this message dialog, creating it first if it has not yet been
	 * created.
	 * <p>
	 * This method waits until the dialog is closed by the end user, and then it
	 * returns the dialog's return code. The dialog's return code is either the
	 * index of the button the user pressed, or {@link SWT#DEFAULT} if the
	 * dialog has been closed by other means.
	 * </p>
	 *
	 * @return the return code
	 *
	 * @see org.eclipse.jface.window.Window#open()
	 */
	@Override
	public int open() {
		return super.open();
	}

	/**
	 * Convenience method to open a simple dialog as specified by the
	 * <code>kind</code> flag.
	 *
	 * @param kind
	 *            the kind of dialog to open, one of {@link #ERROR},
	 *            {@link #INFORMATION}, {@link #QUESTION}, {@link #WARNING},
	 *            {@link #CONFIRM}, or {@link #QUESTION_WITH_CANCEL}.
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 * @param style
	 *            {@link SWT#NONE} for a default dialog, or {@link SWT#SHEET}
	 *            for a dialog with sheet behavior
	 * @return <code>true</code> if the user presses the OK or Yes button,
	 *         <code>false</code> otherwise
	 * @since 3.5
	 */
	public static boolean open(int kind, Shell parent, String title, String message, int style) {
		DialogWithUrls dialog = new DialogWithUrls(parent, title, null, message, kind, getButtonLabels(kind), 0);
		style &= SWT.SHEET;
		dialog.setShellStyle(dialog.getShellStyle() | style);
		return dialog.open() == 0;
	}

	static String[] getButtonLabels(int kind) {
		String[] dialogButtonLabels;
		switch (kind) {
		case ERROR:
		case INFORMATION:
		case WARNING: {
			dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL };
			break;
		}
		case CONFIRM: {
			dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
			break;
		}
		case QUESTION: {
			dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL };
			break;
		}
		case QUESTION_WITH_CANCEL: {
			dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
					IDialogConstants.CANCEL_LABEL };
			break;
		}
		default: {
			throw new IllegalArgumentException("Illegal value for kind in MessageDialog.open()"); //$NON-NLS-1$
		}
		}
		return dialogButtonLabels;
	}

	/**
	 * Convenience method to open a simple confirm (OK/Cancel) dialog.
	 *
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 * @return <code>true</code> if the user presses the OK button,
	 *         <code>false</code> otherwise
	 */
	public static boolean openConfirm(Shell parent, String title, String message) {
		return open(CONFIRM, parent, title, message, SWT.NONE);
	}

	/**
	 * Convenience method to open a standard error dialog.
	 *
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 */
	public static void openError(Shell parent, String title, String message) {
		open(ERROR, parent, title, message, SWT.NONE);
	}

	/**
	 * Convenience method to open a standard information dialog.
	 *
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 */
	public static void openInformation(Shell parent, String title, String message) {
		open(INFORMATION, parent, title, message, SWT.NONE);
	}

	/**
	 * Convenience method to open a simple Yes/No question dialog.
	 *
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 * @return <code>true</code> if the user presses the Yes button,
	 *         <code>false</code> otherwise
	 */
	public static boolean openQuestion(Shell parent, String title, String message) {
		return open(QUESTION, parent, title, message, SWT.NONE);
	}

	/**
	 * Convenience method to open a standard warning dialog.
	 *
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 */
	public static void openWarning(Shell parent, String title, String message) {
		open(WARNING, parent, title, message, SWT.NONE);
	}

	/*
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.
	 * Composite, int, java.lang.String, boolean)
	 */
	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		Button button = super.createButton(parent, id, label, defaultButton);
		// Be sure to set the focus if the custom area cannot so as not
		// to lose the defaultButton.
		if (defaultButton && !customShouldTakeFocus()) {
			button.setFocus();
		}
		return button;
	}

	/**
	 * Create the area the message will be shown in.
	 * <p>
	 * The parent composite is assumed to use GridLayout as its layout manager,
	 * since the parent is typically the composite created in
	 * {@link Dialog#createDialogArea}.
	 * </p>
	 * <p>
	 * <strong>Note:</strong> Clients are expected to call this method,
	 * otherwise neither the icon nor the message will appear.
	 * </p>
	 *
	 * @param composite
	 *            The composite to parent from.
	 * @return Control
	 */
	@Override
	protected Control createMessageArea(Composite composite) {
		// create composite
		// create image
		Image image = getImage();
		if (image != null) {
			imageLabel = new Label(composite, SWT.NULL);
			image.setBackground(imageLabel.getBackground());
			imageLabel.setImage(image);
			// addAccessibleListeners(imageLabel, image);
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(imageLabel);
		}
		// create message
		if (message != null) {
			linkLabel = new Link(composite, getMessageLabelStyle());
			linkLabel.setText(message);

			linkLabel.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// System.out.println("You have selected: " + e.text);
					try {
						// Open default external browser
						Desktop d = Desktop.getDesktop();
						// Browse a URL, say google.com
						d.browse(new URI(e.text));
						// PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new
						// URL(e.text));
						// } catch (PartInitException ex) {
						// TODO Auto-generated catch block
						// ex.printStackTrace();
					} catch (MalformedURLException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
					.hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
					.applyTo(linkLabel);
		}
		return composite;
	}

	/**
	 * Return whether or not we should apply the workaround where we take focus
	 * for the default button or if that should be determined by the dialog. By
	 * default only return true if the custom area is a label or CLabel that
	 * cannot take focus.
	 *
	 * @return boolean
	 */
	protected boolean customShouldTakeFocus() {
		if (customArea instanceof Label) {
			return false;
		}
		if (customArea instanceof CLabel) {
			return (customArea.getStyle() & SWT.NO_FOCUS) > 0;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.dialogs.IconAndMessageDialog#getImage()
	 */
	@Override
	public Image getImage() {
		return image;
	}

	/**
	 * An accessor for the labels to use on the buttons.
	 *
	 * @return The button labels to used; never <code>null</code>.
	 */
	protected String[] getButtonLabels() {
		return buttonLabels;
	}

	/**
	 * An accessor for the index of the default button in the button array.
	 *
	 * @return The default button index.
	 */
	protected int getDefaultButtonIndex() {
		return defaultButtonIndex;
	}

	/**
	 * A mutator for the array of buttons in the button bar.
	 *
	 * @param buttons
	 *            The buttons in the button bar; must not be <code>null</code>.
	 */
	protected void setButtons(Button[] buttons) {
		if (buttons == null) {
			throw new NullPointerException("The array of buttons cannot be null."); //$NON-NLS-1$
		}
		this.buttons = buttons;
	}

	/**
	 * A mutator for the button labels.
	 *
	 * @param buttonLabels
	 *            The button labels to use; must not be <code>null</code>.
	 */
	protected void setButtonLabels(String[] buttonLabels) {
		if (buttonLabels == null) {
			throw new NullPointerException("The array of button labels cannot be null."); //$NON-NLS-1$
		}
		this.buttonLabels = buttonLabels;
	}
}