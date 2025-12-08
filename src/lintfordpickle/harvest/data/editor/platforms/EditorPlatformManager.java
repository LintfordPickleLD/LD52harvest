package lintfordpickle.harvest.data.editor.platforms;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.editor.BaseEditorInstanceManager;
import lintfordpickle.harvest.data.editor.EditorSceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import lintfordpickle.harvest.data.scene.platforms.PlatformInstance;

public class EditorPlatformManager extends BaseEditorInstanceManager {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private final List<PlatformInstance> mPlatforms = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public List<PlatformInstance> platforms() {
		return mPlatforms;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorPlatformManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void addPlatform(PlatformInstance platform) {
		if (mPlatforms.contains(platform) == false)
			mPlatforms.add(platform);
	}

	public void removePlatform(PlatformInstance platform) {
		if (mPlatforms.contains(platform))
			mPlatforms.remove(platform);
	}

	@Override
	public void initializeManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeInTrackDefinition(SceneSaveDefinition sceneSaveDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFromTrackDefinition(SceneSaveDefinition sceneSaveDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalizeAfterLoading(EditorSceneData sceneData) {

	}

}
