package lintfordpickle.harvest.controllers.editor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lintfordpickle.harvest.data.editor.EditorSceneData;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.data.scene.SceneHeader;

// TODO: This is the same as the TrackController in RazerRunnner - consider adding to lib to make setup faster.
// TODO: I think this can be made 'generic' 
public class EditorSceneController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Scene Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private final SceneHeader mSceneHeader;
	private final EditorSceneData mEditorSceneData;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mEditorSceneData != null;
	}

	public SceneHeader sceneHeader() {
		return mSceneHeader;
	}

	public EditorSceneData sceneData() {
		return mEditorSceneData;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorSceneController(ControllerManager controllerManager, SceneHeader header, EditorSceneData data, int entityGroupID) {
		super(controllerManager, CONTROLLER_NAME, entityGroupID);

		mSceneHeader = header;
		mEditorSceneData = data;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void setSceneWidth(int widthInPixels) {
		mEditorSceneData.sceneSettingsManager().sceneWidthInPx(widthInPixels);
	}

	public void setSceneHeight(int heightInPixels) {
		mEditorSceneData.sceneSettingsManager().sceneHeightInPx(heightInPixels);
	}

	public void setSceneBoundary(int widthInPixels, int heightInPixels) {
		setSceneWidth(widthInPixels);
		setSceneHeight(heightInPixels);
	}

	public void saveToFile(String filename) {
		if (filename == null || filename.length() == 0) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to save the scene data into " + filename);
			return;
		}

		// TODO: This isn't the correct place to be doing logical on the file system.

		final var lSaveDirectory = new File(filename);
		final var lParentDirectory = lSaveDirectory.getParentFile();

		if (lParentDirectory.exists() == false) {
			if (lParentDirectory.mkdirs() == false) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to save the scene data into " + filename);
				return;
			}
		}

		// -------

		final var lSceneSaveDefinition = mEditorSceneData.getSceneDefinitionToSave();

		try (Writer writer = new FileWriter(filename)) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(lSceneSaveDefinition, writer);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return;
	}

}
