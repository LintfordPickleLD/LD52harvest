package lintfordpickle.harvest.data.scene.layers.savedefinitions;

import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneNoiseLayer;

public class SceneNoiseLayerSaveDefinition extends BaseSceneLayerSaveDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7569238335511621943L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneNoiseLayerSaveDefinition() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public SceneBaseLayer getSceneLayer() {
		final var lNoiseLayer = new SceneNoiseLayer(layerUid);

		lNoiseLayer.zInvDepth = layerZInvDepth;
		lNoiseLayer.name = layerName;

		lNoiseLayer.translationSpeedModX = translationSpeedModX;
		lNoiseLayer.translationSpeedModY = translationSpeedModY;

		lNoiseLayer.centerX = centerX;
		lNoiseLayer.centerY = centerY;

		lNoiseLayer.width = width;
		lNoiseLayer.height = height;

		// ensure some sane defaults:
		if (lNoiseLayer.width <= 0.f)
			lNoiseLayer.width = 32.f;

		if (lNoiseLayer.height <= 0.f)
			lNoiseLayer.height = 32.f;

		return lNoiseLayer;
	}
}
