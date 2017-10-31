package br.ufrn.sd.project2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author dhiogoboza
 */
public class FileUtils {
	private static final String TAG = "FileUtils";
	
	public static boolean saveFile(String content, File file){
        try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(content);
			
			return true;
        } catch (IOException ex) {
            Log.e(TAG, "Error saving file", ex);
        }
		
		return false;
    }

	public static String readFile(File file) {
		try {
			return new Scanner(file).useDelimiter("\\Z").next();
		} catch (FileNotFoundException ex) {
			Log.e(TAG, "Error reading file", ex);
		}
		
		return null;
	}
}
