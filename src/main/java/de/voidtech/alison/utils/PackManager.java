package main.java.de.voidtech.alison.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.de.voidtech.alison.ephemeral.AlisonModel;

public class PackManager {

	public static AlisonModel GetPack(String pack) {
		return new AlisonModel(pack);
	}
	
	public static boolean PackExists(String pack) {
		return new File("models/" + pack).exists();
	}

	public static void DeletePack(String pack) {
		new File("models/" + pack + "/model.db").delete();
		new File("models/" + pack).delete();
	}
	
	public static List<AlisonModel> GetAllPacks() {
		File[] modelFiles = new File("models/").listFiles();
		List<AlisonModel> models = new ArrayList<AlisonModel>();
		Arrays.asList(modelFiles).stream().forEach(model -> models.add(GetPack(model.getName())));
		return models;
	}
	
	public static long GetWordCount() {
		List<AlisonModel> models = GetAllPacks();
		return models.stream().map(AlisonModel::getWordCount).reduce(0, (a, b) -> a + b);
	}
}