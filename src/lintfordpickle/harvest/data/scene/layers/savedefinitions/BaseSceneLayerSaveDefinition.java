package lintfordpickle.harvest.data.scene.layers.savedefinitions;

import java.io.Serializable;

import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;

public abstract class BaseSceneLayerSaveDefinition implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2551331403008677499L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public int layerUid;
	public int layerZDepth;
	public String layerName;
	public float translationSpeedModX;
	public float translationSpeedModY;
	public float centerX;
	public float centerY;
	public float width;
	public float height;
	public float contentScaleX;
	public float contentScaleY;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public BaseSceneLayerSaveDefinition() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public abstract SceneBaseLayer getSceneLayer();

}
