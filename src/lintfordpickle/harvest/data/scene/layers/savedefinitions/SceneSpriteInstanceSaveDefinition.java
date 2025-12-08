package lintfordpickle.harvest.data.scene.layers.savedefinitions;

import java.io.Serializable;

import net.lintfordlib.core.geometry.Rectangle;

public class SceneSpriteInstanceSaveDefinition implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 1882315064329829612L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String assetDefinitionName;
	public Rectangle destinationRectangle;

	public int entityUid;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneSpriteInstanceSaveDefinition() {
	}
}
