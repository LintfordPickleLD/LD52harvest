package lintfordpickle.harvest.controllers.layers;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.layers.SceneTextureLayer;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;

public class EditorTextureLayerController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Editor Texture Layer Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<SceneTextureLayer> mTextureLayers = new ArrayList<>();
	private LayersManager mLayersManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<SceneTextureLayer> textureLayers() {
		return mTextureLayers;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorTextureLayerController(ControllerManager controllerManager, int entityGroupUid) {
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

		mTextureLayers.clear();

		final var lLayers = mLayersManager.layers();
		final int lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			if (lLayers.get(i) instanceof SceneTextureLayer) {
				mTextureLayers.add((SceneTextureLayer) lLayers.get(i));
			}
		}
	}

	public void addTextureLayer(SceneTextureLayer noiseLayer) {
		if (mTextureLayers.contains(noiseLayer) == false)
			mTextureLayers.add(noiseLayer);
	}

	public void removeTextureLayer(SceneTextureLayer noiseLayer) {
		if (mTextureLayers.contains(noiseLayer))
			mTextureLayers.remove(noiseLayer);
	}

}
