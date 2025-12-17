package lintfordpickle.harvest.data.editor;

import lintfordpickle.harvest.data.scene.SceneSaveDefinition;

public abstract class BaseEditorInstanceManager {

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BaseEditorInstanceManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public abstract void initializeManager();

	public abstract void storeInTrackSaveDefinition(SceneSaveDefinition sceneDefinition);

	public abstract void loadFromTrackSaveDefinition(SceneSaveDefinition sceneDefinition);

	public abstract void finalizeAfterLoading(EditorSceneData sceneData);

}
