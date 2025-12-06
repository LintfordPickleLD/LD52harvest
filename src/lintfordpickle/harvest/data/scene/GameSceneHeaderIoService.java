package lintfordpickle.harvest.data.scene;

import java.io.File;
import java.nio.file.Paths;

import com.google.gson.GsonBuilder;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

public class GameSceneHeaderIoService {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private GameSceneHeaderIoService() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static GameSceneHeader loadSceneHeaderFileFromFilepath(String sceneName, String sceneDirectory) {
		final var sceneFilePath = Paths.get(sceneDirectory, sceneName, GameSceneHeader.HEADER_FILENAME).toString();

		if (sceneFilePath == null || sceneFilePath.length() == 0) {
			Debug.debugManager().logger().e(GameSceneHeader.class.getSimpleName(), "Filepath for SceneHeader file cannot be null or empty!");
			return null;
		}

		return loadSceneHeaderFileFromFilepath(sceneFilePath);
	}

	public static GameSceneHeader loadSceneHeaderFileFromFilepath(String sceneHeaderFilePath) {
		if (sceneHeaderFilePath == null || sceneHeaderFilePath.length() == 0)
			return null; // TODO: log it

		final var sceneHeaderFile = new File(sceneHeaderFilePath);
		if (!sceneHeaderFile.exists())
			return null; // TODO: log it

		final var sceneDirectory = sceneHeaderFile.getParent();

		final var gson = new GsonBuilder().create();

		final var fileContents = FileUtils.loadString(sceneHeaderFilePath);
		final var sceneHeader = gson.fromJson(fileContents, GameSceneHeader.class);

		if (sceneHeader == null) {
			Debug.debugManager().logger().e(GameSceneHeaderIoService.class.getSimpleName(), "Couldn't deserialize SceneHeader file!");
			return null;
		}

		// need to set the header filePath so it can be saved later.
		sceneHeader.sceneHeaderFilePath(sceneHeaderFilePath);

		if (!sceneHeader.dataExistsOnDisk()) {
			final var sceneDataPath = Paths.get(sceneDirectory, GameSceneHeader.DATA_FILENAME).toString();
			final var sceneDataFile = new File(sceneDataPath);
			if (sceneDataFile.exists()) {
				sceneHeader.sceneDataFilePath(sceneDataPath);
			} else {
				Debug.debugManager().logger().w(GameSceneHeaderIoService.class.getSimpleName(), "Could not find or resolve the scene data file. Looking in " + sceneDataPath);
			}
		}

		return sceneHeader;

	}
}
