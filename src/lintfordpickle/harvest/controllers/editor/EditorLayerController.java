package lintfordpickle.harvest.controllers.editor;

import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneNoiseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import lintfordpickle.harvest.data.scene.layers.SceneTextureLayer;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;

public class EditorLayerController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Editor Layer Controller";

	public static final int ACTION_OBJECT_TRANSLATE_SELECTED_LAYER = 1;
	public static final int ACTION_OBJECT_SCALE_SELECTED_LAYER_X = 2;
	public static final int ACTION_OBJECT_SCALE_SELECTED_LAYER_Y = 3;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LayersManager mLayersManager;
	private SceneBaseLayer mSelectedLayer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SceneBaseLayer getLayerByUid(int layerUid) {
		final var lLayers = mLayersManager.layers();
		final int lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			if (lLayers.get(i).layerUid == layerUid)
				return lLayers.get(i);
		}
		return null;
	}

	public SceneBaseLayer selectedLayer() {
		return mSelectedLayer;
	}

	public void selectedLayer(SceneBaseLayer newSelectedLayer) {
		mSelectedLayer = newSelectedLayer;
	}

	public LayersManager layersManager() {
		return mLayersManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorLayerController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		final var lSceneController = (EditorSceneController) lControllerManager.getControllerByNameRequired(EditorSceneController.CONTROLLER_NAME, entityGroupUid());
		mLayersManager = lSceneController.sceneData().layersManager();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void refreshLayers() {

	}

	public void loadLayersFromSceneData() {

	}

	public void saveLayersIntoSceneData() {

	}

	public void deleteSelectedLayerByUid(int layerUidToDelete) {
		final var lLayer = getLayerByUid(layerUidToDelete);

		if (lLayer != null)
			mLayersManager.removedLayer(lLayer);
	}

	public void deleteSelectedLayer(SceneBaseLayer layerToDelete) {
		mLayersManager.removedLayer(layerToDelete);

	}

	public SceneBaseLayer addNewTextureLayer() {
		final var newTextureLayer = new SceneTextureLayer(mLayersManager.getNewInstanceUid());
		mLayersManager.addLayer(newTextureLayer);

		newTextureLayer.name = "Tex Layer " + newTextureLayer.layerUid;
		return newTextureLayer;
	}

	public SceneBaseLayer addNewAnimationLayer() {
		final var newAnimationLayer = new SceneSpriteLayer(mLayersManager.getNewInstanceUid());
		mLayersManager.addLayer(newAnimationLayer);

		newAnimationLayer.name = "Anim Layer " + newAnimationLayer.layerUid;
		return newAnimationLayer;
	}

	public SceneBaseLayer addNewNoiseLayer() {
		final var newNoiseLayer = new SceneNoiseLayer(mLayersManager.getNewInstanceUid());
		mLayersManager.addLayer(newNoiseLayer);

		newNoiseLayer.name = "Noise Layer " + newNoiseLayer.layerUid;
		return newNoiseLayer;
	}

	public void setSelectedLayer(int selectedLayerUid) {
		mSelectedLayer = getLayerByUid(selectedLayerUid);
	}

}
