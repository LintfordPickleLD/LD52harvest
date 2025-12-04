package lintfordpickle.harvest.controllers.layers;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.layers.SceneNoiseLayer;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;

public class EditorNoiseLayerController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Editor Noise Layer Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<SceneNoiseLayer> mNoiseLayers = new ArrayList<>();
	private LayersManager mLayersManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<SceneNoiseLayer> noiseLayers() {
		return mNoiseLayers;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorNoiseLayerController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setLayerManager(LayersManager layersManager) {
		mLayersManager = layersManager;

		mNoiseLayers.clear();

		final var lLayers = mLayersManager.layers();
		final int lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			if (lLayers.get(i) instanceof SceneNoiseLayer) {
				mNoiseLayers.add((SceneNoiseLayer) lLayers.get(i));
			}
		}
	}

	public void addNoiseLayer(SceneNoiseLayer noiseLayer) {
		if (mNoiseLayers.contains(noiseLayer) == false)
			mNoiseLayers.add(noiseLayer);
	}

	public void removeNoiseLayer(SceneNoiseLayer noiseLayer) {
		if (mNoiseLayers.contains(noiseLayer))
			mNoiseLayers.remove(noiseLayer);
	}

}
