package lintfordpickle.harvest.data.scene.layers.savedefinitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LayersManagerSaveDefinition implements Serializable {

	private static final long serialVersionUID = -5966028123660010928L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final List<SceneTextureLayerSaveDefinition> textureLayers = new ArrayList<>();
	public final List<SceneSpritesLayerSaveDefinition> spriteLayers = new ArrayList<>();
	public final List<SceneNoiseLayerSaveDefinition> noiseLayers = new ArrayList<>();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LayersManagerSaveDefinition() {
	}

}
