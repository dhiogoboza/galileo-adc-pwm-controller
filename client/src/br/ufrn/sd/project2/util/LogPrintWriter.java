/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufrn.sd.project2.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import javafx.scene.control.TextArea;


public class LogPrintWriter {
	private final TextArea logTextArea;
	private DebugPrintWriter debugPrintWriter;
	private ErrorPrintWriter errorPrintWriter;
	
	//private final HTMLEditorKit kit = new HTMLEditorKit();
    //private final HTMLDocument doc = new HTMLDocument();
	private boolean initialized = false;
	//private int line_index = 0;
	
	private final static String TAG = "LogPrintWriter";
	
	
	public LogPrintWriter(TextArea logTextArea) {
		this.logTextArea = logTextArea;
		
		//this.logTextArea.setText("");//setHtmlText("");//setText("");
		
		try {
			debugPrintWriter = new DebugPrintWriter();
			errorPrintWriter = new ErrorPrintWriter();
			
			initialized = true;
		} catch (FileNotFoundException ex) {
			Log.e(TAG, "Creating log printer", ex);
			
			initialized = false;
		}
		
		Log.e(TAG, "Creating log printer: " + initialized);
		
		
		
		/*StyleSheet styleSheet = doc.getStyleSheet();
		styleSheet.addRule("a {text-decoration: underline; color: blue;} \n");
		styleSheet.addRule(".error {color: red;}");
        styleSheet.addRule(".success {color: green;}");
		styleSheet.addRule(".tabulation {content: 'A';}");*/
		
		//kit.setStyleSheet(styleSheet);
		
		//this.logTextArea.setEditorKit(kit);
		//this.logTextArea.setDocument(doc);
	}
	
	public void putText(String text) {
		//text = text.replaceAll("\n", "<br />");
		
		//logTextArea.insertText(line_index, text);
		logTextArea.appendText("\n" + text);
		logTextArea.setScrollTop(Double.MAX_VALUE);
		//line_index++;
		
		/*try {
			text = text.replaceAll("\n", "<br />");
			
			kit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
			
			SwingUtilities.invokeLater(() -> {
				JScrollBar scrollBar = ((JScrollPane) logTextArea.getParent().getParent()).getVerticalScrollBar();	
				scrollBar.setValue(scrollBar.getMaximum());
				//kit.insertHTML(doc, doc.getLength(), "vertical scroll bar position: " + scrollBar.getValue() + " - maximum: " + scrollBar.getMaximum(), 0, 0, null);
			});
			
		} catch (BadLocationException | IOException t) {
			System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			
			Log.e("At LogPrintWriter", t);
		}*/
	}

	public boolean isInitialized() {
		return initialized;
	}
	
	public class DebugPrintWriter extends PrintStream {
		public DebugPrintWriter() throws FileNotFoundException {
			super(System.getProperty("java.io.tmpdir") + "/tmptest");
		}

		@Override
		public void println(String text) {
			putText(text);
		}

		@Override
		public void print(String text) {
			putText(text);
		}
	}
	
	public class ErrorPrintWriter extends PrintStream {
		public ErrorPrintWriter() throws FileNotFoundException {
			super(System.getProperty("java.io.tmpdir") + "/tmptest");
		}

		@Override
		public void println(String text) {
			putText("<font color='red'>" + text + "</font>");
		}

		@Override
		public void print(String text) {
			putText("<font class='error'>" + text + "</font>");
		}
	}

	public void clear() {
		logTextArea.setText("");
		//currentText = "";
	}
	
	public DebugPrintWriter getDebugPrintWriter() {
		return debugPrintWriter;
	}

	public ErrorPrintWriter getErrorPrintWriter() {
		return errorPrintWriter;
	}
	
	/*public EditorKit getEditorKit() {
		return kit;
	}

	public Document getDocument() {
		return doc;
	}*/
}
