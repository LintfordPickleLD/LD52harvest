package lintfordpickle.harvest.data.scene.layers;

import lintfordpickle.harvest.data.scene.layers.savedefinitions.BaseSceneLayerSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneNoiseLayerSaveDefinition;
import net.lintfordlib.core.maths.Matrix4f;

public class SceneParticleLayer extends SceneBaseLayer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Matrix4f worldMatrix = new Matrix4f();

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneParticleLayer(int uid) {
		super(uid);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public BaseSceneLayerSaveDefinition getSaveDefinition() {
		final var lSaveDefinition = new SceneNoiseLayerSaveDefinition();

		fillBaseSceneLayerSaveDefinition(lSaveDefinition);

		// TODO: noise input parameters

		lSaveDefinition.translationSpeedModX = translationSpeedModX;
		lSaveDefinition.translationSpeedModY = translationSpeedModY;

		return lSaveDefinition;
	}

}
