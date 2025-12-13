package lintfordpickle.harvest.screens.editor;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import lintfordpickle.harvest.controllers.editor.EditorAssetsController;
import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.controllers.editor.EditorPhysicsController;
import lintfordpickle.harvest.controllers.editor.EditorSceneController;
import lintfordpickle.harvest.data.editor.EditorSceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import lintfordpickle.harvest.renderers.editor.EditorLayersRenderer;
import lintfordpickle.harvest.renderers.editor.EditorPhysicsRenderer;
import lintfordpickle.harvest.renderers.editor.EditorPhysicsSettingsRenderer;
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
	private SceneHeader mSceneHeader;

	// Controllers
	private CameraZoomController mCameraZoomController;
	private EditorCameraMovementController mCameraMoveController;
	private CameraBoundsController mCameraBoundsController;
	private SpatialHashGridController mSpatialHashGridController;
	private EditorSceneController mEditorSceneController;
	private EditorPhysicsSettingsController mEditorPhysicsSettingsController;
	private EditorPhysicsController mEditorPhysicsController;
	private EditorHashGridController mHashGridController;
	private EditorBrushController mEditorBrushController;
	private EditorFileController mEditorFileController;
	private EditorAssetsController mEditorAssetsController;
	private EditorLayerController mEditorLayerController;

	// Renderers
	private UiDockedWindow mEditorGui;
	private EditorBrushRenderer mEditorBrushRenderer;
	private EditorHashGridRenderer mEditorHashGridRenderer;
	private EditorPhysicsSettingsRenderer mEditorPhysicsSettingsRenderer;
	private EditorPhysicsRenderer mEditorPhysicsRenderer;
	private EditorLayersRenderer mEditorLayersRenderer;

	private DebugCameraBoundsDrawer mDebugCameraBoundsDrawer;

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

		mEditorSceneData = new EditorSceneData();
		if (mSceneHeader != null && mSceneHeader.isSceneValid()) {
			loadTrackDefinitionFromFile(mSceneHeader.sceneDataFilePath());

		} else {
			if (!mSceneHeader.dataExistsOnDisk()) {
				// need to do something with a new scene ?

			}
		}

		mEditorSceneData.finalizeAfterLoading();

		mEditorBrush = new EditorLayerBrush();

	}

	public void loadTrackDefinitionFromFile(String filename) {
		final var gson = new GsonBuilder().create();
		
		// TODO: this is getting called twice

		String sceneRawFileContents = null;
		SceneSaveDefinition sceneSaveDefinition = null;

		try {
			sceneRawFileContents = FileUtils.loadString(filename);
			sceneSaveDefinition = gson.fromJson(sceneRawFileContents, SceneSaveDefinition.class);

		} catch (JsonSyntaxException ex) {
			Debug.debugManager().logger().printException(getClass().getSimpleName(), ex);
		}

		if (sceneSaveDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "There was an error reading the scene save definition file (" + filename + ")");
			return;
		}

		mEditorSceneData.createSceneFromSaveDefinition(sceneSaveDefinition);
	}

	// ---------------------------------------------

	@Override
	protected void createControllers(ControllerManager controllerManager) {

		final var assetsManager = mEditorSceneData.spriteManager();
		final var hashGrid = mEditorSceneData.hashGridManager().hashGrid();

		mCameraMoveController = new EditorCameraMovementController(controllerManager, mGameCamera, entityGroupUid());
		mCameraZoomController = new CameraZoomController(controllerManager, mGameCamera, entityGroupUid());
		mCameraBoundsController = new CameraBoundsController(controllerManager, mGameCamera, entityGroupUid());
		mEditorPhysicsSettingsController = new EditorPhysicsSettingsController(controllerManager, mEditorSceneData.physicsSettingsManager().physicsSettings(), entityGroupUid());
		mSpatialHashGridController = new SpatialHashGridController(controllerManager, hashGrid, entityGroupUid());
		mEditorSceneController = new EditorSceneController(controllerManager, mSceneHeader, mEditorSceneData, entityGroupUid());
		mEditorPhysicsController = new EditorPhysicsController(controllerManager, entityGroupUid());
		mEditorBrushController = new EditorBrushController(controllerManager, mEditorBrush, entityGroupUid());
		mHashGridController = new EditorHashGridController(controllerManager, hashGrid, entityGroupUid());
		mEditorFileController = new EditorFileController(controllerManager, mSceneHeader, entityGroupUid());
		mEditorLayerController = new EditorLayerController(controllerManager, entityGroupUid());
		mEditorAssetsController = new EditorAssetsController(controllerManager, assetsManager, entityGroupUid());
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
		mEditorAssetsController.initialize(core);
		mEditorPhysicsController.initialize(core);
	}

	// ---------------------------------------------
	// Render
	// ---------------------------------------------

	@Override
	protected void createRenderers(LintfordCore core) {

		mEditorLayersRenderer = new EditorLayersRenderer(mRendererManager, entityGroupUid());

		mEditorGui = new EditorGui(mRendererManager, entityGroupUid());
		mEditorBrushRenderer = new EditorBrushRenderer(mRendererManager, entityGroupUid());
		mEditorHashGridRenderer = new EditorHashGridRenderer(mRendererManager, entityGroupUid());
		mEditorPhysicsSettingsRenderer = new EditorPhysicsSettingsRenderer(mRendererManager, entityGroupUid());
		mEditorPhysicsRenderer = new EditorPhysicsRenderer(mRendererManager, entityGroupUid());
		mDebugCameraBoundsDrawer = new DebugCameraBoundsDrawer(mRendererManager, entityGroupUid());
	}

	@Override
	protected void createRendererStructure(LintfordCore core) {

		// ignored (automatic in the SimpleRendererManagare).

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void onSave() {
		final var dataFilename = mSceneHeader.sceneDataFilePath();
		mEditorSceneController.saveToFile(dataFilename);
		mSceneHeader.saveSceneHeaderFile();
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSceneNameChanged(String newSceneName) {
		mSceneHeader.sceneName(newSceneName);
	}

	@Override
	public void onSceneFileNameChanged(String newScenename) {
		// TODO: Auto-generated method stub

	}

	@Override
	public void onSceneDirectoryChanged(String newDirectory) {
		// TODO: Auto-generated method stub

	}

}