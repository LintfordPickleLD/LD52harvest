package lintfordpickle.harvest.data.scene;

import lintfordpickle.harvest.data.editor.BaseEditorInstanceManager;
import lintfordpickle.harvest.data.editor.EditorSceneData;

public class EditorSceneSettingsManager extends BaseEditorInstanceManager {

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorSceneSettingsManager() {
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
	public void storeInTrackDefinition(SceneSaveDefinition sceneSaveDefinition) {
		final var lSceneSettingsSaveDefinition = sceneSaveDefinition.sceneSettingsSaveDefinition();
		lSceneSettingsSaveDefinition.sceneWidth = mSceneWidthInPx;
		lSceneSettingsSaveDefinition.sceneHeight = mSceneHeightInPx;

	}

	@Override
	public void loadFromTrackDefinition(SceneSaveDefinition sceneSaveDefinition) {
		final var lSceneSettingsSaveDefinition = sceneSaveDefinition.sceneSettingsSaveDefinition();
		mSceneWidthInPx = lSceneSettingsSaveDefinition.sceneWidth;
		mSceneHeightInPx = lSceneSettingsSaveDefinition.sceneHeight;

	}

	@Override
	public void finalizeAfterLoading(EditorSceneData sceneData) {

	}

}
