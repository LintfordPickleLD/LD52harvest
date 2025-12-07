package lintfordpickle.harvest.controllers.editor;

import lintfordpickle.harvest.data.assets.SceneSpriteInstance;
import lintfordpickle.harvest.data.assets.SceneSpritesManager;
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

	private SceneSpritesManager mSceneAssetsManager;

	private SceneSpriteInstance mSelectedAssetInstance;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SceneSpritesManager sceneAssetsManager() {
		return mSceneAssetsManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorAssetsController(ControllerManager controllerManager, SceneSpritesManager sceneAssetManager, int entityGroupUid) {
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
