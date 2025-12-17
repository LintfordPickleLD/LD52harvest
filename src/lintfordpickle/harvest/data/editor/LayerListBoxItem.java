package lintfordpickle.harvest.data.editor;

import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import net.lintfordlib.renderers.windows.components.UiListBoxItem;

// This class lets us associate a SceneBaseLayer with an entry in a UiListBox
public class LayerListBoxItem extends UiListBoxItem {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -243658458174471622L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private SceneBaseLayer mLayer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public SceneBaseLayer layer() {
		return mLayer;
	}

	public void layer(SceneBaseLayer layer) {
		mLayer = layer;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LayerListBoxItem(int itemUid) {
		super(itemUid);
	}

}
