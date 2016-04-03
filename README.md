[<img src="https://raw.githubusercontent.com/AndroidDeveloperLB/WebpifyYourAndroidApp/master/art/Webp_logo_Webp.png">](https://en.wikipedia.org/wiki/WebP)



# WebpifyYourAndroidApp
A small tool to convert your app's png&amp;jpg image files into WebP when possible

Sample video:

[<img src="https://raw.githubusercontent.com/AndroidDeveloperLB/WebpifyYourAndroidApp/master/art/play-1073616_640.png">](https://rawgit.com/AndroidDeveloperLB/WebpifyYourAndroidApp/master/art/player.html)



How to use
----------
If you use Windows OS, just run the executable jar file that's inside the repo, inside "Release\WindowsOs". Then, drag the file/s&folder/s into the app, or put the paths of them into the textBox, choose the settings you wish, and press the conversion button ("do it").

If you use MacOs or Linux (which I haven't tried), here's what you should probably do:

 1. Install latest JDK:
 http://www.oracle.com/technetwork/java/javase/downloads/index.html
 2. Install Eclipse with Java development support: 
 https://eclipse.org/downloads/
 2. Install Window-Builder:
 https://eclipse.org/windowbuilder/
 3. Download SWT (the "swt.jar" file is enough), and put it inside "external" folder:
 https://www.eclipse.org/swt/
 4. Download the converter executable file ("cwebp")  and put it inside "external" folder:
https://storage.googleapis.com/downloads.webmproject.org/releases/webp/index.html
 5. Open the project via Eclipse, make sure the "swt.jar" that you've downloaded is used. Run it via Eclipse to see that it works. If it does, you can export it as a runnable jar file and run it from outside Eclipse.
 6. You are good to go!

Advantages and features
----------

 1. Converts png&jpg into WebP format when possible.
 2. Can also convert png to jpg files, if they are non-transparent (meaning not even a single pixel has a non-100%-opaque color)
 3. Should work on all OSs (yet tested on Windows only)
 4. Can handle all supported image files of all folders that are given, or only those on res/drawable* and res/mipmap* (which works this way by default)
 5. Supports drag&drop of files&folders, or entering full paths to them, separated by ";". Will handle all sub-directories, recursively.
 6. Supports level of encoding quality for both Jpeg and WebP
 7. Shows progress and summary of how many files were handled and how much space was saved.
 8. Native look&feel of the OS (because it uses SWT).
 9. Skips 9-patch images, as they aren't supported in WebP format anyway.

Disadvantages, issues, and To-do's
-------------

 1. Ugly UI, but still functional 
 I wanted to make this tool for developers anyway. I'm not as familiar with SWT as I am with Android, and I didn't want to spend too much time on it.
 2. Supports only png&jpg as input image files. Should be possible to handle gif files too
 3. Uses executable converter files instead of JNI. 
 4. Since I'm not sure how to make the same project work well on multiple OSs (needed SWT and converter file for each), and since I don't have other OSs currently, it was only developed&tested on Windows OS.
 5. The tool doesn't remember which files were already handled from last time. It will go over all of them each time.
 6. Chosen settings aren't saved for next sessions. It also doesn't remember which files were already handled for next sessions
 7. Doesn't parse the gradle files. Instea, it assumes the folders structures of "res/drawable*" and "res/mipmap*" for the image files to handle. You can disable this check, and let it convert all supported files in all sub-directories of the given path.
 8. A bit slow, but this is in order to compress the files using the best space-saving algorithm. 
 9. There should be a console/command-line tool, and not just UI-based tool.
