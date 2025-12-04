package lintfordpickle.harvest.screens.editor;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import lintfordpickle.harvest.controllers.editor.EditorAssetsController;
import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.controllers.editor.EditorPhysicsController;
import lintfordpickle.harvest.controllers.editor.EditorSceneController;
import lintfordpickle.harvest.controllers.layers.EditorAnimationLayerController;
import lintfordpickle.harvest.controllers.layers.EditorNoiseLayerController;
import lintfordpickle.harvest.controllers.layers.EditorParticleLayerController;
import lintfordpickle.harvest.controllers.layers.EditorTextureLayerController;
import lintfordpickle.harvest.data.assets.SceneAssetsManager;
import lintfordpickle.harvest.data.editor.EditorSceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import lintfordpickle.harvest.renderers.editor.EditorAnimationLayerRenderer;
import lintfordpickle.harvest.renderers.editor.EditorNoiseLayerRenderer;
import lintfordpickle.harvest.renderers.editor.EditorPhysicsRenderer;
import lintfordpickle.harvest.renderers.editor.EditorPhysicsSettingsRenderer;
import lintfordpickle.harvest.renderers.editor.EditorTextureLayerRenderer;
import net.lintfordlib.ConstantsEditor;
import net.lintfordlib.MenuActions;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.camera.CameraBoundsController;
import net.lintfordlib.controllers.camera.CameraZoomController;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.controllers.editor.EditorCameraMovementController;
import net.lintfordlib.controllers.editor.EditorFileController;
import net.lintfordlib.controllers.editor.EditorHashGridController;
import net.lintfordlib.controllers.editor.EditorPhysicsSettingsController;
import net.lintfordlib.controllers.editor.IEditorFileControllerListener;
import net.lintfordlib.controllers.geometry.SpatialHashGridController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.data.DataManager;
import net.lintfordlib.data.editor.EditorLayerBrush;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.renderers.debug.DebugCameraBoundsDrawer;
import net.lintfordlib.renderers.editor.EditorBrushRenderer;
import net.lintfordlib.renderers.editor.EditorHashGridRenderer;
import net.lintfordlib.renderers.editor.panels.UiDockedWindow;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.screens.BaseGameScreen;

public class EditorScreen extends BaseGameScreen implements IEditorFileControllerListener {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	// Data
	private EditorLayerBrush mEditorBrush;
	private EditorSceneData mEditorSceneData;
	private SceneAssetsManager mSceneAssetManager; // TODO: make this generic
	private SceneHeader mSceneHeader;

	// Controllers
	private CameraZoomController mCameraZoomController;
	private EditorCameraMovementController mCameraMoveController;
	private CameraBoundsController mCameraBoundsController;
	private SpatialHashGridController mSpatialHashGridController;
	private EditorSceneController mEditorSceneController;
	private EditorPhysicsSettingsController mEditorPhysicsSettingsController;
	private EditorAssetsController mEditorAssetsController;
	private EditorPhysicsController mEditorPhysicsController;
	private EditorHashGridController mHashGridController;
	private EditorBrushController mEditorBrushController;
	private EditorFileController mEditorFileController;

	private EditorLayerController mEditorLayerController;
	private EditorTextureLayerController mEditorTextureLayerController;
	private EditorNoiseLayerController mEditorNoiseLayerController;
	private EditorParticleLayerController mEditorParticleLayerController;
	private EditorAnimationLayerController mEditorAnimationLayerController;

	// Renderers
	private UiDockedWindow mEditorGui;
	private EditorBrushRenderer mEditorBrushRenderer;
	private EditorHashGridRenderer mEditorHashGridRenderer;
	private EditorPhysicsSettingsRenderer mEditorPhysicsSettingsRenderer;
	private EditorPhysicsRenderer mEditorPhysicsRenderer;
	private DebugCameraBoundsDrawer mDebugCameraBoundsDrawer;

	private EditorTextureLayerRenderer mEditorTextureLayerRenderer;
	private EditorNoiseLayerRenderer mEditorNoiseLayerRenderer;
	private EditorAnimationLayerRenderer mEditorAnimationLayerRenderer;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorScreen(ScreenManager screenManager, SceneHeader sceneHeader) {
		super(screenManager);

		mSceneHeader = sceneHeader;

		// takes the resolution of the desktop
		mOverrideUiStretch = true;
		mOverrideGameStretch = true;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		resourceManager.spriteSheetManager().loadSpriteSheet("res/spritesheets/spritesheetHud.json", ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
	}

	@Override
	public void handleInput(LintfordCore core) {
		super.handleInput(core);

		core.controllerManager().handleInput(core, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);

		final var actionManager = core.input().actionManager();
		final var menuEscapeAction = actionManager.getActionState(MenuActions.NAV_BACK);

		if (menuEscapeAction.isDownTimed(this)) {
			screenManager.addScreen(new EditorPauseScreen(screenManager, mSceneHeader));
			return;
		}

	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		core.controllerManager().update(core, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void createData(DataManager dataManager) {
		// This creates an empty scene
		mEditorSceneData = new EditorSceneData();

		if (mSceneHeader != null && mSceneHeader.isSceneValid()) {
			loadTrackDefinitionFromFile(mSceneHeader.sceneDataFilePath());
		}
		mEditorSceneData.finalizeAfterLoading();

		mEditorBrush = new EditorLayerBrush();
		mSceneAssetManager = new SceneAssetsManager();
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

		mEditorSceneData.createSceneFromSaveDefinition(lSceneSaveDefinition);
	}

	// ---------------------------------------------

	@Override
	protected void createControllers(ControllerManager controllerManager) {

		final var hashGrid = mEditorSceneData.hashGridManager().hashGrid();

		mCameraMoveController = new EditorCameraMovementController(controllerManager, mGameCamera, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mCameraZoomController = new CameraZoomController(controllerManager, mGameCamera, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mCameraBoundsController = new CameraBoundsController(controllerManager, mGameCamera, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorPhysicsSettingsController = new EditorPhysicsSettingsController(controllerManager, mEditorSceneData.physicsSettingsManager().physicsSettings(), ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mSpatialHashGridController = new SpatialHashGridController(controllerManager, hashGrid, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorSceneController = new EditorSceneController(controllerManager, mSceneHeader, mEditorSceneData, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorPhysicsController = new EditorPhysicsController(controllerManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorBrushController = new EditorBrushController(controllerManager, mEditorBrush, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mHashGridController = new EditorHashGridController(controllerManager, hashGrid, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorFileController = new EditorFileController(controllerManager, mSceneHeader, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorLayerController = new EditorLayerController(controllerManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorTextureLayerController = new EditorTextureLayerController(controllerManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorNoiseLayerController = new EditorNoiseLayerController(controllerManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorParticleLayerController = new EditorParticleLayerController(controllerManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorAnimationLayerController = new EditorAnimationLayerController(controllerManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);

		mEditorAssetsController = new EditorAssetsController(controllerManager, mSceneAssetManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);

		mEditorFileController.setCallbackListener(this);
	}

	@Override
	protected void initializeControllers(LintfordCore core) {
		mEditorSceneController.initialize(core);
		mCameraMoveController.initialize(core);
		mCameraZoomController.initialize(core);
		mCameraBoundsController.initialize(core);
		mHashGridController.initialize(core);
		mEditorPhysicsSettingsController.initialize(core);
		mSpatialHashGridController.initialize(core);
		mEditorBrushController.initialize(core);
		mEditorFileController.initialize(core);
		mEditorLayerController.initialize(core);
		mEditorTextureLayerController.initialize(core);
		mEditorNoiseLayerController.initialize(core);
		mEditorParticleLayerController.initialize(core);
		mEditorAnimationLayerController.initialize(core);
		mEditorAssetsController.initialize(core);
		mEditorPhysicsController.initialize(core);
	}

	// ---------------------------------------------
	// Render
	// ---------------------------------------------

	@Override
	protected void createRenderers(LintfordCore core) {
		mEditorTextureLayerRenderer = new EditorTextureLayerRenderer(mRendererManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorNoiseLayerRenderer = new EditorNoiseLayerRenderer(mRendererManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorAnimationLayerRenderer = new EditorAnimationLayerRenderer(mRendererManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);

		mEditorGui = new EditorGui(mRendererManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorBrushRenderer = new EditorBrushRenderer(mRendererManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorHashGridRenderer = new EditorHashGridRenderer(mRendererManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorPhysicsSettingsRenderer = new EditorPhysicsSettingsRenderer(mRendererManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mEditorPhysicsRenderer = new EditorPhysicsRenderer(mRendererManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
		mDebugCameraBoundsDrawer = new DebugCameraBoundsDrawer(mRendererManager, ConstantsEditor.EDITOR_RESOURCE_GROUP_ID);
	}

	@Override
	protected void createRendererStructure(LintfordCore core) {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void onSave() {
		final var lDataFilename = mSceneHeader.sceneDataFilePath();
		mEditorSceneController.saveToFile(lDataFilename);
		mSceneHeader.saveSceneHeaderFile();
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSceneNameChanged(String newSceneName) {

	}

	@Override
	public void onFilepathChanged(String newBaseSceneDirectory) {

	}

}