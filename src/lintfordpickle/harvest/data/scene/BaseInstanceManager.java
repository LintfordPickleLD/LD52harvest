package lintfordpickle.harvest.data.scene;

public abstract class BaseInstanceManager {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected int mInstanceUidCounter;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public int getNewInstanceUid() {
		return mInstanceUidCounter++;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BaseInstanceManager() {
		mInstanceUidCounter = 0;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	/**
	 * After loading, the mInstanceUidCounter needs to be set to the next free counter index. Otherwise, you will get collisions in the instance indices.
	 */
	public abstract void initializeInstanceCounter();

	public abstract void initializeManager();

	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {

	}

	public void loadFromTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		initializeInstanceCounter();
	}

	public abstract void finalizeAfterLoading(SceneData sceneData);

}
