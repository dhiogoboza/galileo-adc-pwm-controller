package br.ufrn.sd.project2.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/*
* Log methods utilities
*/
public class Log {
	
	/*
	* Print a debug message
	*/
	public static void d(String tag, String message) {
		d(tag, message, null);
	}
	
	/*
	* Print a debug message with a throwable
	*/
	public static void d(String tag, String message, Throwable t) {
		write(tag, message, t, false);
	}
	
	/*
	* Print a error message
	*/
	public static void e(String tag, String message) {
		e(tag, message, null);
	}
	
	/*
	* Print a error message with a throwable
	*/
	public static void e(String tag, String message, Throwable t) {
		write(tag, message, t, true);
	}

	private static void write(String tag, String message, Throwable t, boolean error) {
		if (t != null) {
			StringWriter errors = new StringWriter();
			t.printStackTrace(new PrintWriter(errors));
			message += "\n" + errors.toString();
		}
		
		if (error) {
			System.err.println(tag + ":" + message);
		} else {
			System.out.println(tag + ":" + message);
		}
	}

    public static void s(String tag, String string) {
        write(tag, new StringBuilder("<span class='success'>")
                .append(string)
                .append("</span>")
                .toString(), null, false);
    }
	
}
