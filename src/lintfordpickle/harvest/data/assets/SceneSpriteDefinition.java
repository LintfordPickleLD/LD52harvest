package lintfordpickle.harvest.data.assets;

import net.lintfordlib.core.entities.definitions.BaseDefinition;
import net.lintfordlib.core.graphics.sprites.SpriteContainer;

public class SceneSpriteDefinition extends BaseDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1093633752600449126L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final SpriteContainer sceneSpriteContainer = new SpriteContainer();
	public final SpriteContainer iconSpriteContainer = new SpriteContainer();

	public int propDefaultRadius;
	public int propDefaultWidth;
	public int propDefaultHeight;

}
