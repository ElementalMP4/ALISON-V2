package main.java.de.voidtech.alison.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.de.voidtech.alison.entities.AlisonModel;

public class PackManager {

	public static AlisonModel getPack(String pack) {
		return new AlisonModel(pack);
	}
	
	public static boolean packExists(String pack) {
		return new File("models/" + pack).exists();
	}

	public static void deletePack(String pack) {
		new File("models/" + pack + "/meta.json").delete();
		new File("models/" + pack + "/words.alison").delete();
		new File("models/" + pack).delete();
	}
	
	public static List<AlisonModel> getAllPacks() {
		File[] modelFiles = new File("models/").listFiles();
		List<AlisonModel> models = new ArrayList<AlisonModel>();
		Arrays.asList(modelFiles).stream().forEach(model -> models.add(getPack(model.getName())));
		return models;
	}
	
	public static long getModelCount() {
		return new File("models/").listFiles().length;
	}
	
	public static long getWordCount() {
		List<AlisonModel> models = getAllPacks();
		return models.stream().map(AlisonModel::getWordCount).reduce((long) 0, (a, b) -> a + b);
	}
}