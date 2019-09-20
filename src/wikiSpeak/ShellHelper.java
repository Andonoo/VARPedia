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
		int exitStatus = process.waitFor();
		if (exitStatus == 0) {
			String line;
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
