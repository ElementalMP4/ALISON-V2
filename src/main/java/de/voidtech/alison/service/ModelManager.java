package main.java.de.voidtech.alison.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.de.voidtech.alison.entities.AlisonModel;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ModelManager {
	
	public static AlisonModel getModel(String model) {
		return new AlisonModel(model);
	}

	public static boolean modelExists(String model) {
		return new File("models/" + model).exists();
	}

	public static void deleteModel(String model) {
		new File("models/" + model + "/meta.json").delete();
		new File("models/" + model + "/words.alison").delete();
		new File("models/" + model).delete();
	}

	public static List<AlisonModel> getAllModels() {
		File[] modelFiles = new File("models/").listFiles();
		List<AlisonModel> models = new ArrayList<>();
		if (modelFiles == null) return models;
		Arrays.asList(modelFiles).forEach(model -> models.add(getModel(model.getName())));
		return models;
	}

	public static long getModelCount() {
		File[] files = new File("models/").listFiles();
		return files == null ? 0 : files.length;
	}

	public static long getWordCount() {
		List<AlisonModel> models = getAllModels();
		if (models.isEmpty()) return 0;
		return models.stream().map(AlisonModel::getWordCount).reduce((long) 0, Long::sum);
	}
}