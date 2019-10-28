package wikiSpeakController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/***
 * A very simple class to simplify the call to bash
 * 
 * @author Xiaobin Lin
 *
 */
public class ShellHelper {
	public static List<String> execute(String command) throws Exception {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
		List<String> output = new ArrayList<String>();
		Process process = pb.start();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String line;
		int exitStatus = process.waitFor();
		if (exitStatus == 0) {
			// Special case for text2wave as error does not return a non-zero status code
			if ((line = stderr.readLine()) != null) {
				if (line.contains("SIOD ERROR")) {
					throw new RuntimeException("Error with message:" + line);					
				}
			}
			
			while ((line = stdout.readLine()) != null) {
				// Add all outputs of cmd to the output of the program
				output.add(line);
			}
		} else {
			if ((line = stderr.readLine()) != null) {
				System.out.println(line);
			}
			throw new RuntimeException("Error with exitStatus " + exitStatus + " (" + command + ")");
		}
		return output;
	}
	
	/**
	 * Create a file name wrapper so argument into bash is always correct
	 * @param fileLocation
	 * @return Wrapped String with quotation marks
	 */
	public static String WrapString(String fileLocation) {
		return ("\"" + fileLocation + "\"");
	}
}
