package br.ufrn.sd.project2.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PathsUtilities {
	
	private static final String TAG = "PathsUtilities";
	
	public static final String FILES_OUTPUT;
	public static final String TEMP_FILES_OUTPUT;
	public static final String IMAGES_ANIMATION_OUTPUT;
	public static final String IMAGES_OUTPUT;
	public static final String VIDEOS_FOLDER;
	public static final String SOUNDS_FOLDER;
	public static final String FFMPEG_PATH;
	public static final String REMOTE_CONTROLS_FILE;
	public static final String WATERMARKS_PATH;
	
	/**
     * A String representing the complete path to the root installation
     * directory
     */
    public static final String INSTALLATION_PATH;
	
	public static final String IRTRANS_LOG;
	
	public static File WORKING_DIRECTORY;
	
	public static final String IRTRANS_INSTALLATION_FOLDER;

	public static final String IRTRANS_PID;
	
	public static final String ERRORS_OUTPUT;
    
    public static final String CONVERT_PATH;
	
	
	static {
		FILES_OUTPUT = System.getProperty("user.home") + "/screentester";
		ifNotExistsCreate(FILES_OUTPUT);
		
		TEMP_FILES_OUTPUT = FILES_OUTPUT + "/temp";
		ifNotExistsCreate(TEMP_FILES_OUTPUT);
		
		VIDEOS_FOLDER = FILES_OUTPUT + "/videos";
		ifNotExistsCreate(VIDEOS_FOLDER);
		
		SOUNDS_FOLDER = FILES_OUTPUT + "/sounds";
		ifNotExistsCreate(SOUNDS_FOLDER);
        
        CONVERT_PATH = FILES_OUTPUT + "/convert";
		ifNotExistsCreate(CONVERT_PATH);
		
		IRTRANS_PID = FILES_OUTPUT + "/irtrans.pid";
	
		IRTRANS_LOG = FILES_OUTPUT + "/irtrans.log";
		ifNotExistsCreate(IRTRANS_LOG, null, false);
		
		REMOTE_CONTROLS_FILE = FILES_OUTPUT + "/remotes.json";
		ifNotExistsCreate(REMOTE_CONTROLS_FILE, "[]", false);
		
		IMAGES_OUTPUT = FILES_OUTPUT + "/images";
		ifNotExistsCreate(IMAGES_OUTPUT);
		
		IMAGES_ANIMATION_OUTPUT = FILES_OUTPUT + "/animationimages";
		ifNotExistsCreate(IMAGES_ANIMATION_OUTPUT);
        
		ERRORS_OUTPUT = FILES_OUTPUT + "/errors";
		ifNotExistsCreate(ERRORS_OUTPUT);
		
		WATERMARKS_PATH = FILES_OUTPUT + "/watermarks";
		ifNotExistsCreate(WATERMARKS_PATH);
		
		IRTRANS_INSTALLATION_FOLDER = "/usr/local/irtrans/irserver";
		
		FFMPEG_PATH = isWindows()?
				"\"C:/Program Files/ffmpeg-20131002-git-64327aa-win64-static/bin/ffmpeg.exe\"":
				"ffmpeg";
		
		
		URL url = PathsUtilities.class.getProtectionDomain().getCodeSource().getLocation();
        File file = new File(url.getFile());
        if (file.isFile()) {
            file = file.getParentFile();
        }

        String aux = file.getAbsolutePath().replaceAll("%c3%a0", "à");

        if (aux.endsWith(".jar")) {
            file = file.getParentFile();
            aux = file.getAbsolutePath().replaceAll("%c3%a0", "à");
        }

		if (isWindows()) {
			aux = aux.replaceAll("%c3%a1", "á");
			aux = aux.replaceAll("%c3%a2", "â");
			aux = aux.replaceAll("%c3%a3", "ã");
			aux = aux.replaceAll("%c3%a8", "è");
			aux = aux.replaceAll("%c3%a9", "é");
			aux = aux.replaceAll("%c3%aa", "ê");
			aux = aux.replaceAll("%c3%ac", "ì");
			aux = aux.replaceAll("%c3%ad", "í");
			aux = aux.replaceAll("%c3%ae", "î");
			aux = aux.replaceAll("%c3%b2", "ò");
			aux = aux.replaceAll("%c3%b3", "ó");
			aux = aux.replaceAll("%c3%b4", "ô");
			aux = aux.replaceAll("%c3%b5", "õ");
			aux = aux.replaceAll("%c3%b9", "ù");
			aux = aux.replaceAll("%c3%ba", "ú");
			aux = aux.replaceAll("%c3%bb", "û");
			aux = aux.replaceAll("%c3%bc", "ü");
			aux = aux.replaceAll("%c3%a7", "ç");
			aux = aux.replaceAll("%20", " ");

			INSTALLATION_PATH = aux + "/";
		} else {
			INSTALLATION_PATH = "";
		}
		
		WORKING_DIRECTORY = new File(FILES_OUTPUT);
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}

	public static void updateTestSuitePath(File currentDirectory) {
		WORKING_DIRECTORY = currentDirectory;
	}

	public static String processPath(String referenceResult) {
		if (referenceResult == null) {
			return "";
		}
		
		if (new File(referenceResult).isAbsolute()) {
			return referenceResult;
		} else {
			return WORKING_DIRECTORY.getAbsolutePath() + "/" + referenceResult;
		}
	}

	private static void writeInFile(File file, String content) {
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(file));
			
			writer.write(content);
		} catch (IOException ex) {
			Logger.getLogger(PathsUtilities.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					
				}
			}
		}
		
	}

	private static void ifNotExistsCreate(String path) {
		ifNotExistsCreate(path, null, true);
	}
	
	private static void ifNotExistsCreate(String path, String content, boolean directory) {
		File newFile = new File(path);
		
		if (!newFile.exists()) {
			try {
				if (directory) {
					newFile.mkdirs();
				} else {
					newFile.createNewFile();
					
					if (content != null) {
						writeInFile(newFile, content);
					}
				}
			} catch (IOException ex) {
				Log.e(TAG, "Creating file/directory", ex);
			}
		}
	}

	public static String getFileExtension(String filePath) {
		String extension = "";

		int i = filePath.lastIndexOf('.');
		int p = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));

		if (i > p) {
			extension = filePath.substring(i+1);
		}
		
		return extension;
	}

	public static boolean copy(File source, File dest) {
		
		InputStream is = null;
		OutputStream os = null;
		
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			
			return dest.exists();
		} catch (IOException ex) {
			Log.e(TAG, "Copying files", ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					
				}
			}
			
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					
				}
			}
		}
		
		return false;
	}
	
}
