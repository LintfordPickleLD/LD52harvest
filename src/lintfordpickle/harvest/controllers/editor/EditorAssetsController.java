package lintfordpickle.harvest.controllers.editor;

import lintfordpickle.harvest.data.assets.SceneSpriteInstance;
import lintfordpickle.harvest.data.assets.SceneSpriteManager;
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

	private SceneSpriteManager mSceneAssetsManager;
	private SceneSpriteInstance mSelectedAssetInstance;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SceneSpriteManager sceneAssetsManager() {
		return mSceneAssetsManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorAssetsController(ControllerManager controllerManager, SceneSpriteManager sceneAssetManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mSceneAssetsManager = sceneAssetManager;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void selectedAssetinstance(SceneSpriteInstance assetInstance) {
		mSelectedAssetInstance = assetInstance;
	}

	public SceneSpriteInstance selectedAssetinstance() {
		return mSelectedAssetInstance;
	}

}
