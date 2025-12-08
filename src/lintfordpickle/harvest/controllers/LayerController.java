package lintfordpickle.harvest.controllers;

import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;

public class LayerController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Layer Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private LayersManager mLayersManager;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public LayersManager layerManager() {
		return mLayersManager;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public LayerController(ControllerManager controllerManager, LayersManager layerManager, int entityGroupID) {
		super(controllerManager, CONTROLLER_NAME, entityGroupID);

		mLayersManager = layerManager;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var layers = mLayersManager.layers();
		final var numLayers = layers.size();
		for (int i = 0; i < numLayers; i++) {
			final var sceneLayer = layers.get(i);
			if (sceneLayer instanceof SceneSpriteLayer) {
				var lAnimationSceneLayer = (SceneSpriteLayer) sceneLayer;

				final var lLayerAnimations = lAnimationSceneLayer.spriteAssets();
				final var lNumAnimations = lLayerAnimations.size();
				for (int j = 0; j < lNumAnimations; j++) {
					lLayerAnimations.get(j).update(core);
				}
			}
		}
	}

}
