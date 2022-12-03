package main.java.de.voidtech.alison.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceLoader {

	private final InputStream resourceStream;

	public ResourceLoader(String filename) {
		resourceStream = getClass().getClassLoader().getResourceAsStream(filename);
	}

	public List<String> getResource() {
		List<String> lines = new ArrayList<>();
		try {
            assert resourceStream != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(resourceStream));
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public InputStream getStream() {
		return this.resourceStream;
	}
}