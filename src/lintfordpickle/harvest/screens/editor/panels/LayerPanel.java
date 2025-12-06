package lintfordpickle.harvest.screens.editor.panels;

import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.editor.panels.UiPanel;
import net.lintfordlib.renderers.windows.UiWindow;

public abstract class LayerPanel<T extends SceneBaseLayer> extends UiPanel {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected EditorLayerController mEditorLayerController;
	protected T mSelectedLayer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LayerPanel(UiWindow parentWindow, String panelName, int entityGroupUid) {
		super(parentWindow, panelName, entityGroupUid);

		mRenderPanelTitle = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		mEditorLayerController = (EditorLayerController) lControllerManager.getControllerByNameRequired(EditorLayerController.CONTROLLER_NAME, mEntityGroupUid);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		handleLayerSelection();
	}

	private void handleLayerSelection() {
		final var lCurrentlySelectedLayer = (SceneBaseLayer) mEditorLayerController.selectedLayer();

		if (lCurrentlySelectedLayer != null) {

			if (lCurrentlySelectedLayer.equals(mSelectedLayer))
				return;

			selectedLayer();
			newLayerSelected(lCurrentlySelectedLayer);

		} else {
			if (mSelectedLayer != null) {
				newLayerSelected(lCurrentlySelectedLayer);
			}
		}
	}

	// --------------------------------------

	protected abstract void newLayerSelected(SceneBaseLayer selectedLayer);

	protected void selectedLayer() {
		mSelectedLayer = null;
		mIsExpandable = false;
		mIsPanelOpen = false;
	}

	// --------------------------------------

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {

		}
	}

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {

	}

	protected void refreshLayerPanels() {
//		if (mParentWindow instanceof UiDockedWindow) {
//			final var lDockedWindowWithPanels = (UiDockedWindow) mParentWindow;
//			final var lPanels = lDockedWindowWithPanels.editorPanels();
//
//			final var lNumPanels = lPanels.size();
//			for (int i = 0; i < lNumPanels; i++) {
//				if (lPanels.get(i) instanceof LayersPanel) {
//					final var lLayersPanel = (LayersPanel) lPanels.get(i);
//					lLayersPanel.createUiListItemsFromManager();
//				}
//			}
//		}
	}
}