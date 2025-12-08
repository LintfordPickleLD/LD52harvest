package lintfordpickle.harvest.data.scene.platforms;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.scene.BaseInstanceManager;
import lintfordpickle.harvest.data.scene.SceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;

public class PlatformManager extends BaseInstanceManager {

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

	@Override
	public void initializeInstanceCounter() {

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PlatformManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public PlatformInstance createPlatform() {
		return new PlatformInstance(getNewInstanceUid());
	}

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
	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFromTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalizeAfterLoading(SceneData sceneData) {
		// TODO Auto-generated method stub

	}

}
