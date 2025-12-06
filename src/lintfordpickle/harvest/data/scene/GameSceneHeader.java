package lintfordpickle.harvest.data.scene;

import net.lintfordlib.data.scene.SceneHeader;

public class GameSceneHeader extends SceneHeader {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mDescription;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String description() {
		return mDescription;
	}

	public void description(String description) {
		mDescription = description;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private static final long serialVersionUID = 1L;

	public GameSceneHeader(String sceneName) {
		super(sceneName);

	}

}
