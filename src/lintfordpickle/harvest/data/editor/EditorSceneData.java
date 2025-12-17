package lintfordpickle.harvest.data.editor;

import lintfordpickle.harvest.data.assets.SceneSpriteManager;
import lintfordpickle.harvest.data.editor.physics.EditorPhysicsObjectsManager;
import lintfordpickle.harvest.data.editor.platforms.EditorPlatformManager;
import lintfordpickle.harvest.data.scene.EditorHashGridManager;
import lintfordpickle.harvest.data.scene.EditorSceneSettingsManager;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.EditorLayersManager;
import lintfordpickle.harvest.data.scene.physics.EditorPhysicsSettingsManager;
import lintfordpickle.harvest.data.scene.ships.EditorShipManager;

public class EditorSceneData {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EditorSceneSettingsManager mSceneSettingsManager;
	private EditorHashGridManager mHashGridManager;
	private EditorLayersManager mLayersManager;
	private EditorPlatformManager mPlatformManager;
	private EditorShipManager mShipManager;
	private EditorPhysicsSettingsManager mPhysicsSettingsManager;
	private EditorPhysicsObjectsManager mPhysicsManager;
	private SceneSpriteManager mSceneSpriteManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public EditorSceneSettingsManager sceneSettingsManager() {
		return mSceneSettingsManager;
	}

	public EditorHashGridManager hashGridManager() {
		return mHashGridManager;
	}

	public EditorLayersManager layersManager() {
		return mLayersManager;
	}

	public EditorPlatformManager platformManager() {
		return mPlatformManager;
	}

	public EditorShipManager shipManager() {
		return mShipManager;
	}

	public EditorPhysicsSettingsManager physicsSettingsManager() {
		return mPhysicsSettingsManager;
	}

	public EditorPhysicsObjectsManager physicsObjectsManager() {
		return mPhysicsManager;
	}

	public SceneSpriteManager spriteManager() {
		return mSceneSpriteManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorSceneData() {
		mSceneSettingsManager = new EditorSceneSettingsManager();
		mHashGridManager = new EditorHashGridManager();
		mLayersManager = new EditorLayersManager();
		mPlatformManager = new EditorPlatformManager();
		mShipManager = new EditorShipManager();
		mPhysicsSettingsManager = new EditorPhysicsSettingsManager();
		mPhysicsManager = new EditorPhysicsObjectsManager();
		mSceneSpriteManager = new SceneSpriteManager();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SceneSaveDefinition getSceneDefinitionToSave() {
		final var lSceneSaveDefinition = new SceneSaveDefinition();

		mSceneSettingsManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mHashGridManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mLayersManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mPlatformManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mShipManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mPhysicsSettingsManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mPhysicsManager.storeInTrackSaveDefinition(lSceneSaveDefinition);

		return lSceneSaveDefinition;
	}

	public void createSceneFromSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		mSceneSettingsManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mHashGridManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mLayersManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mPlatformManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mShipManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mPhysicsSettingsManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mPhysicsManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
	}

	public void finalizeAfterLoading() {
		mSceneSettingsManager.finalizeAfterLoading(this);
		mHashGridManager.finalizeAfterLoading(this);
		mLayersManager.finalizeAfterLoading(this);
		mPlatformManager.finalizeAfterLoading(this);
		mShipManager.finalizeAfterLoading(this);
		mPhysicsSettingsManager.finalizeAfterLoading(this);
		mPhysicsManager.finalizeAfterLoading(this);
	}

}
