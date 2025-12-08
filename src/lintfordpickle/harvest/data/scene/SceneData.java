package lintfordpickle.harvest.data.scene;

import lintfordpickle.harvest.data.assets.SceneSpriteManager;
import lintfordpickle.harvest.data.game.GameState;
import lintfordpickle.harvest.data.game.GameState.GameMode;
import lintfordpickle.harvest.data.scene.cargo.CargoManager;
import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.physics.PhysicsObjectsManager;
import lintfordpickle.harvest.data.scene.physics.PhysicsSettingsManager;
import lintfordpickle.harvest.data.scene.platforms.PlatformManager;
import lintfordpickle.harvest.data.scene.ships.ShipManager;
import net.lintfordlib.core.physics.PhysicsWorld;

public class SceneData {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private PhysicsWorld mPhysicsWorld;

	private SceneSettingsManager mSceneSettingsManager;
	private GameState mGameState;
	private HashGridManager mHashGridManager;
	private LayersManager mLayersManager;
	private PlatformManager mPlatformManager;
	private ShipManager mShipManager;
	private PhysicsSettingsManager mPhysicsSettingsManager;
	private PhysicsObjectsManager mPhysicsObjectsManager;
	private SceneSpriteManager mSceneSpriteManager;
	private CargoManager mCargoManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SceneSpriteManager spriteManager() {
		return mSceneSpriteManager;
	}

	public SceneSettingsManager sceneSettingsManager() {
		return mSceneSettingsManager;
	}

	public GameState gameState() {
		return mGameState;
	}

	public HashGridManager hashGridManager() {
		return mHashGridManager;
	}

	public LayersManager layersManager() {
		return mLayersManager;
	}

	public PlatformManager platformManager() {
		return mPlatformManager;
	}

	public ShipManager shipManager() {
		return mShipManager;
	}

	public PhysicsSettingsManager physicsSettingsManager() {
		return mPhysicsSettingsManager;
	}

	public PhysicsObjectsManager physicsObjectsManager() {
		return mPhysicsObjectsManager;
	}

	public CargoManager cargoManager() {
		return mCargoManager;
	}

	public PhysicsWorld physicsWorld() {
		return mPhysicsWorld;
	}

	public void physicsWorld(PhysicsWorld physicsWorld) {
		mPhysicsWorld = physicsWorld;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneData() {
		mGameState = new GameState();
		mGameState.startNewGame(GameMode.TimeTrial);

		mSceneSettingsManager = new SceneSettingsManager();
		mHashGridManager = new HashGridManager();
		mLayersManager = new LayersManager();
		mPlatformManager = new PlatformManager();
		mShipManager = new ShipManager();
		mPhysicsSettingsManager = new PhysicsSettingsManager();
		mPhysicsObjectsManager = new PhysicsObjectsManager();

		mSceneSpriteManager = new SceneSpriteManager();
		mCargoManager = new CargoManager();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public SceneSaveDefinition getSceneDefinitionToSave() {
		final var lSceneSaveDefinition = new SceneSaveDefinition();

		mSceneSettingsManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mGameState.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mHashGridManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mLayersManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mPlatformManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mShipManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mPhysicsSettingsManager.storeInTrackSaveDefinition(lSceneSaveDefinition);
		mPhysicsObjectsManager.storeInTrackSaveDefinition(lSceneSaveDefinition);

		mCargoManager.storeInTrackSaveDefinition(lSceneSaveDefinition);

		return lSceneSaveDefinition;
	}

	public void createSceneFromSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		mSceneSettingsManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mGameState.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mHashGridManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mLayersManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mPlatformManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mShipManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mPhysicsSettingsManager.loadFromTrackSaveDefinition(sceneSaveDefinition);
		mPhysicsObjectsManager.loadFromTrackSaveDefinition(sceneSaveDefinition);

		mCargoManager.loadFromTrackSaveDefinition(sceneSaveDefinition);

	}

	public void finalizeAfterLoading() {
		mSceneSettingsManager.finalizeAfterLoading(this);
		mGameState.finalizeAfterLoading(this);
		mHashGridManager.finalizeAfterLoading(this);
		mLayersManager.finalizeAfterLoading(this);
		mPlatformManager.finalizeAfterLoading(this);
		mShipManager.finalizeAfterLoading(this);
		mPhysicsSettingsManager.finalizeAfterLoading(this);
		mPhysicsObjectsManager.finalizeAfterLoading(this);
		mCargoManager.finalizeAfterLoading(this);
	}

}
