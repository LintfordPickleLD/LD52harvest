package lintfordpickle.harvest.data.scene.layers;

import lintfordpickle.harvest.data.assets.SceneSpriteManager;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.BaseSceneLayerSaveDefinition;

public abstract class SceneBaseLayer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient int layerUid;

	/**
	 * This is the inverse depth. i.e. Z_FAR - zInvDepth;
	 */
	public float zInvDepth;
	public String name;
	public float translationSpeedModX;
	public float translationSpeedModY;

	public float centerX;
	public float centerY;

	public float width;
	public float height;

	public float contentScaleX;
	public float contentScaleY;

	public transient boolean visible;
	public transient boolean editMode;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneBaseLayer(int uid) {
		layerUid = uid;

		width = 64.f;
		height = 64.f;

		contentScaleX = 1.f;
		contentScaleY = 1.f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void resetInput() {

	}

	public void finalizeAfterLoading(SceneSpriteManager spriteManager) {

	}

	public abstract BaseSceneLayerSaveDefinition getSaveDefinition();

	protected void fillBaseSceneLayerSaveDefinition(BaseSceneLayerSaveDefinition saveDefinition) {
		saveDefinition.layerZInvDepth = zInvDepth;
		saveDefinition.layerUid = layerUid;
		saveDefinition.layerName = name;

		saveDefinition.centerX = centerX;
		saveDefinition.centerY = centerY;

		saveDefinition.width = width;
		saveDefinition.height = height;

		saveDefinition.contentScaleX = contentScaleX;
		saveDefinition.contentScaleY = contentScaleY;

		saveDefinition.translationSpeedModX = translationSpeedModX;
		saveDefinition.translationSpeedModY = translationSpeedModY;
	}

}
