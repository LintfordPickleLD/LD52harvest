package lintfordpickle.harvest.data.scene.layers.savedefinitions;

import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;

public class SceneSpritesLayerSaveDefinition extends BaseSceneLayerSaveDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7569238335511621943L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneSpritesLayerSaveDefinition() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public SceneBaseLayer getSceneLayer() {
		final var lAnimationLayer = new SceneSpriteLayer(layerUid);
		lAnimationLayer.zDepth = layerZDepth;
		lAnimationLayer.name = layerName;

		return lAnimationLayer;
	}

}
