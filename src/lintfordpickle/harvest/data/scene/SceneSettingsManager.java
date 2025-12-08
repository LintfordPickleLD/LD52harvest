package lintfordpickle.harvest.data.scene;

public class SceneSettingsManager extends BaseInstanceManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mSceneWidthInPx;
	private int mSceneHeightInPx;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float sceneWidthInPx() {
		return mSceneWidthInPx;
	}

	public void sceneWidthInPx(int v) {
		mSceneWidthInPx = v;
	}

	public float sceneHeightInPx() {
		return mSceneHeightInPx;
	}

	public void sceneHeightInPx(int v) {
		mSceneHeightInPx = v;
	}

	@Override
	public void initializeInstanceCounter() {
		// ignored
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneSettingsManager() {
		mSceneWidthInPx = 2048;
		mSceneHeightInPx = 2048;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initializeManager() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		final var lSceneSettingsSaveDefinition = sceneSaveDefinition.sceneSettingsSaveDefinition();
		lSceneSettingsSaveDefinition.sceneWidth = mSceneWidthInPx;
		lSceneSettingsSaveDefinition.sceneHeight = mSceneHeightInPx;

	}

	@Override
	public void loadFromTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		final var lSceneSettingsSaveDefinition = sceneSaveDefinition.sceneSettingsSaveDefinition();
		mSceneWidthInPx = lSceneSettingsSaveDefinition.sceneWidth;
		mSceneHeightInPx = lSceneSettingsSaveDefinition.sceneHeight;

	}

	@Override
	public void finalizeAfterLoading(SceneData sceneData) {

	}

}
