package lintfordpickle.harvest.controllers.editor;

import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;

public interface ILayerSelectionListener {

	public abstract void OnLayerSelected(SceneBaseLayer layer);

	public abstract void OnLayerDeselected(SceneBaseLayer layer);

}
