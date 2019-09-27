package wikiSpeak;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/***
 * A very simple class to simplify the call to bash
 * @author student
 *
 */
public class ShellHelper {
	public static List<String> execute(String command) throws Exception {
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command);
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
			throw new RuntimeException("Error with exitStatus" + exitStatus);
		}
		return output;
	}
}
