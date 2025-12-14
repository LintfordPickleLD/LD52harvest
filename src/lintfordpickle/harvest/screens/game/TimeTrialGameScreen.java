package lintfordpickle.harvest.screens.game;

import org.lwjgl.opengl.GL11;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.AudioController;
import lintfordpickle.harvest.controllers.CargoController;
import lintfordpickle.harvest.controllers.EnvironmentController;
import lintfordpickle.harvest.controllers.LayerController;
import lintfordpickle.harvest.controllers.LevelController;
import lintfordpickle.harvest.controllers.PlatformController;
import lintfordpickle.harvest.controllers.SceneController;
import lintfordpickle.harvest.controllers.ShipController;
import lintfordpickle.harvest.controllers.TimeTrialGameStateController;
import lintfordpickle.harvest.controllers.actionevents.GameActionEventController;
import lintfordpickle.harvest.controllers.camera.CameraShipChaseController;
import lintfordpickle.harvest.data.CollisionHandler;
import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.data.scene.SceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import lintfordpickle.harvest.renderers.PlatformsRenderer;
import lintfordpickle.harvest.renderers.ShipRenderer;
import lintfordpickle.harvest.renderers.hud.MinimapRenderer;
import lintfordpickle.harvest.renderers.hud.TimeTrialHudRenderer;
import lintfordpickle.harvest.renderers.scene.SceneAdWallRenderer;
import lintfordpickle.harvest.renderers.scene.SceneRenderer;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.MenuActions;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.core.particles.ParticleFrameworkController;
import net.lintfordlib.controllers.debug.physics.DebugPhysicsWorldWatcher;
import net.lintfordlib.controllers.physics.IPhysicsControllerCallback;
import net.lintfordlib.controllers.physics.PhysicsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.physics.PhysicsWorld;
import net.lintfordlib.core.physics.resolvers.CollisionResolverRotationAndFriction;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.data.DataManager;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.renderers.debug.physics.DebugPhysicsGridRenderer;
import net.lintfordlib.renderers.debug.physics.DebugPhysicsRenderer;
import net.lintfordlib.renderers.particles.ParticleFrameworkRenderer;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.screens.BaseGameScreen;
import net.lintfordlib.screenmanager.screens.LoadingScreen;

public class TimeTrialGameScreen extends BaseGameScreen implements IPhysicsControllerCallback {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private RenderTarget mRenderTarget;

	private CollisionHandler mCollisionHandler;

	// Data
	private SceneHeader mSceneHeader;
	private SceneData mSceneData;

	private PlayerManager mPlayerManager;

	private ParticleFrameworkData mParticleFrameworkData;

	// Controllers
	private AudioController mAudioController;
	private GameActionEventController mGameActionEventController;
	private CameraShipChaseController mCameraShipChaseController;
	private CargoController mCargoController;
	private LevelController mLevelController;
	private ShipController mShipController;
	private SceneController mSceneController;
	private LayerController mLayerController;
	private PlatformController mPlatformsController;
	private TimeTrialGameStateController mGameStateController;
	private PhysicsController mPhysicsController;
	private ParticleFrameworkController mParticleFrameworkController;
	private EnvironmentController mEnvironmentController;

	// Renderers
	private DebugPhysicsWorldWatcher mPhysicsWorldDebugWatcher;
	private DebugPhysicsRenderer mPhysicsRenderer;
	private DebugPhysicsGridRenderer mPhysicsDebugGridRenderer;
	private ShipRenderer mShipRenderer;
	private SceneRenderer mSceneRenderer;
	private SceneAdWallRenderer mSceneAdWallRenderer;
	private PlatformsRenderer mPlatformsRenderer;
	private TimeTrialHudRenderer mHudRenderer;
	private MinimapRenderer mMinimapRenderer;
	private ParticleFrameworkRenderer mParticleFrameworkRenderer;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public SceneHeader sceneHeader() {
		return mSceneHeader;
	}

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public TimeTrialGameScreen(ScreenManager screenManager, SceneHeader sceneHeader, PlayerManager playerManager) {
		super(screenManager);

		mPlayerManager = playerManager;
		mPlayerManager.resetSessions();

		mSceneHeader = sceneHeader;

		ConstantsPhysics.setPhysicsWorldConstants(64.f);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		mPhysicsController.simulationRunning(true);

	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		final var lDisplaySettings = resourceManager.config().display();
		final var lCanvasWidth = lDisplaySettings.gameResolutionWidth();
		final var lCanvasHeight = lDisplaySettings.gameResolutionHeight();

		mRenderTarget = mRendererManager.createRenderTarget("RT_MAIN", lCanvasWidth, lCanvasHeight, 1.f, GL11.GL_NEAREST, false, null);

		// TODO: Change this!
		resourceManager.textureManager().loadTexture("TEXTURE_PARTICLES", "res/textureParticles.png", entityGroupUid());
	}

	@Override
	public void unloadResources() {
		mRendererManager.unloadRenderTarget(mRenderTarget);

		super.unloadResources();
	}

	@Override
	public void handleInput(LintfordCore core) {
		super.handleInput(core);

		final var actionManager = core.input().actionManager();
		final var menuEscapeAction = actionManager.getActionState(MenuActions.NAV_BACK);

		if (menuEscapeAction.isDownTimed(this)) {
			if (ConstantsGame.ESCAPE_RESTART_MAIN_SCENE) {

				final var gameScreen = new TimeTrialGameScreen(screenManager, mSceneHeader, mPlayerManager);
				screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, true, true, gameScreen));

				return;
			}

			screenManager.addScreen(new PauseScreen(screenManager, mSceneHeader, mPlayerManager));
			return;
		}

		// TODO: Start game stuff here
	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		// mGameCamera.setZoomFactor(1.f);

		if (otherScreenHasFocus == false) {
//			final var lPlayerScoreCard = mGameState.getScoreCard(0);
//
//			if (lPlayerScoreCard.isPlayerDead && mGameStateController.gameState().isGameRunning) {
//				mGameActionEventController.finalizeInputFile();
//
//				mMinimapRenderer.isActive(false);
//
//				mGameState.isGameRunning = false;
//				screenManager().addScreen(new TimeTrialEndScreen(mScreenManager, mPlayerManager, false, mGameState.timeAliveInMs, mGameActionEventController.fastestTimeOnExitReached()));
//
//				return;
//			}
		}

	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);

		// mRendererManager.drawWindowRenderers(core);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	// DATA ----------------------------------------

	@Override
	protected void createData(DataManager dataManager) {
		mParticleFrameworkData = new ParticleFrameworkData(dataManager, entityGroupUid());
		mParticleFrameworkData.loadFromMetaFiles();
		mCollisionHandler = new CollisionHandler();

		mGameCamera.setPosition(ConstantsPhysics.toPixels(0.f), ConstantsPhysics.toPixels(0.f));

		mSceneData = new SceneData(); // We fill *the components* of the SceneData, by deserilizing objects using the SceneHeader.

		if (mSceneHeader != null && mSceneHeader.isSceneValid()) {
			loadTrackDefinitionFromFile(mSceneHeader.sceneDataFilePath());
		} else
			throw new RuntimeException("Couldn't deserialize level file.");
	}

	public void createNewScene() {

	}

	public void loadTrackDefinitionFromFile(String filename) {
		final var lGson = new GsonBuilder().create();

		String lSceneRawFileContents = null;
		SceneSaveDefinition lSceneSaveDefinition = null;

		try {
			lSceneRawFileContents = FileUtils.loadString(filename);
			lSceneSaveDefinition = lGson.fromJson(lSceneRawFileContents, SceneSaveDefinition.class);

		} catch (JsonSyntaxException ex) {
			Debug.debugManager().logger().printException(getClass().getSimpleName(), ex);
		}

		if (lSceneSaveDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the scene save definition file (" + filename + ")");
			return;
		}

		mSceneData.createSceneFromSaveDefinition(lSceneSaveDefinition);
	}

	// CONTROLLERS ---------------------------------

	@Override
	protected void createControllers(ControllerManager controllerManager) {
		mAudioController = new AudioController(controllerManager, screenManager.core().resources().audioManager(), entityGroupUid());
		mGameActionEventController = new GameActionEventController(controllerManager, mPlayerManager, inputCounter(), entityGroupUid());
		mPhysicsController = new PhysicsController(controllerManager, this, entityGroupUid());
		mLevelController = new LevelController(controllerManager, entityGroupUid());
		mLayerController = new LayerController(controllerManager, mSceneData.layersManager(), entityGroupUid());
		mCameraShipChaseController = new CameraShipChaseController(controllerManager, mGameCamera, null, entityGroupUid());
		mSceneController = new SceneController(controllerManager, mSceneHeader, mSceneData, entityGroupUid());
		mGameStateController = new TimeTrialGameStateController(controllerManager, mSceneData, mPlayerManager, entityGroupUid());
		mCargoController = new CargoController(controllerManager, entityGroupUid());
		mShipController = new ShipController(controllerManager, mPlayerManager, entityGroupUid());
		mPlatformsController = new PlatformController(controllerManager, entityGroupUid());
		mParticleFrameworkController = new ParticleFrameworkController(controllerManager, mParticleFrameworkData, entityGroupUid());
		mEnvironmentController = new EnvironmentController(controllerManager, mGameCamera, entityGroupUid());
		mPhysicsWorldDebugWatcher = new DebugPhysicsWorldWatcher(controllerManager, entityGroupUid());
	}

	@Override
	protected void initializeControllers(LintfordCore core) {
		mAudioController.initialize(core);
		mParticleFrameworkController.initialize(core);
		mGameActionEventController.initialize(core);
		mPhysicsController.initialize(core);
		mLayerController.initialize(core);
		mLevelController.initialize(core);
		mCameraShipChaseController.initialize(core);
		mSceneController.initialize(core);
		mCargoController.initialize(core);
		mShipController.initialize(core);
		mPlatformsController.initialize(core);
		mGameStateController.initialize(core);
		mEnvironmentController.initialize(core);
		mPhysicsWorldDebugWatcher.initialize(core);
		mPhysicsWorldDebugWatcher.physicsWorld(mPhysicsController.world());

	}

	// RENDERERS -----------------------------------

	@Override
	protected void createRenderers(LintfordCore core) {
		mSceneRenderer = new SceneRenderer(mRendererManager, entityGroupUid());
		if (ConstantsGame.PHYICS_DEBUG_MODE) {
			mPhysicsRenderer = new DebugPhysicsRenderer(mRendererManager, entityGroupUid());
			mPhysicsDebugGridRenderer = new DebugPhysicsGridRenderer(mRendererManager, entityGroupUid());
		}

		mShipRenderer = new ShipRenderer(mRendererManager, entityGroupUid());
		mSceneAdWallRenderer = new SceneAdWallRenderer(mRendererManager, entityGroupUid());
		mPlatformsRenderer = new PlatformsRenderer(mRendererManager, entityGroupUid());
		mParticleFrameworkRenderer = new ParticleFrameworkRenderer(mRendererManager, entityGroupUid());

		mHudRenderer = new TimeTrialHudRenderer(mRendererManager, entityGroupUid());
		mMinimapRenderer = new MinimapRenderer(mRendererManager, entityGroupUid());
	}

	@Override
	protected void createRendererStructure(LintfordCore core) {
		// TODO Auto-generated method stub

	}

	// -----------------------------------

	@Override
	public void exitScreen() {
		super.exitScreen();

		mGameActionEventController.finalizeInputFile();
	}

	@Override
	public PhysicsWorld createPhysicsWorld() {

		final var lPhysicsSettingsManager = mSceneData.physicsSettingsManager();
		final var lPhysicsSettings = lPhysicsSettingsManager.physicsSettings();
		final var lPhysicsWorld = new PhysicsWorld(lPhysicsSettings);

		lPhysicsWorld.initialize();
		lPhysicsWorld.setGravity(lPhysicsSettings.gravityX, lPhysicsSettings.gravityY);

		lPhysicsWorld.setContactResolver(new CollisionResolverRotationAndFriction());
		lPhysicsWorld.addCollisionCallback(mCollisionHandler);

		mSceneData.physicsWorld(lPhysicsWorld);

		return lPhysicsWorld;
	}
}
