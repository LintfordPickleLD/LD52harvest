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

	public abstract void storeInTrackDefinition(SceneSaveDefinition sceneDefinition);

	public abstract void loadFromTrackDefinition(SceneSaveDefinition sceneDefinition);

	public abstract void finalizeAfterLoading(EditorSceneData sceneData);

}
