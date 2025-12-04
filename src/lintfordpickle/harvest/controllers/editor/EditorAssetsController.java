package lintfordpickle.harvest.controllers.editor;

import lintfordpickle.harvest.data.assets.SceneAssetInstance;
import lintfordpickle.harvest.data.assets.SceneAssetsManager;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;

public class EditorAssetsController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Editor Assets Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private SceneAssetsManager mSceneAssetsManager;

	private SceneAssetInstance mSelectedAssetInstance;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SceneAssetsManager sceneAssetsManager() {
		return mSceneAssetsManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorAssetsController(ControllerManager controllerManager, SceneAssetsManager sceneAssetManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mSceneAssetsManager = sceneAssetManager;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void selectedAssetinstance(SceneAssetInstance assetInstance) {
		mSelectedAssetInstance = assetInstance;
	}

	public SceneAssetInstance selectedAssetinstance() {
		return mSelectedAssetInstance;
	}

}
