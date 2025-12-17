package lintfordpickle.harvest.screens.editor.panels;

import java.util.Collections;

import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.data.editor.LayerListBoxItem;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.editor.panels.UiPanel;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
import net.lintfordlib.renderers.windows.components.UiCheckBox;
import net.lintfordlib.renderers.windows.components.UiHorizontalEntryGroup;
import net.lintfordlib.renderers.windows.components.UiListBoxItem;
import net.lintfordlib.renderers.windows.components.UiVerticalTextListBox;
import net.lintfordlib.renderers.windows.components.interfaces.IUiListBoxListener;

public class LayersPanel extends UiPanel implements IUiListBoxListener {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_DELETE_LAYER = 5;

	public static final int BUTTON_ADD_TEX_LAYER = 15;
	public static final int BUTTON_ADD_ANIM_LAYER = 16;
	public static final int BUTTON_ADD_NOISE_LAYER = 17;
	public static final int BUTTON_LAYER_VISIBLE = 18;

	public static final int BUTTON_MOVE_LAYER_UP = 50;
	public static final int BUTTON_MOVE_LAYER_DOWN = 51;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiButton mDeleteSelected;
	private UiButton mAddTextureLayer;
	private UiButton mAddAnimationLayer;
	private UiButton mAddNoiseLayer;

	private UiButton mMoveUp;
	private UiButton mMoveDown;

	private UiCheckBox mLayerVisible;

	private EditorLayerController mEditorLayerController;

	private UiVerticalTextListBox<LayerListBoxItem> mLayerListWidget;

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

	public LayersPanel(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "Layers Panel", entityGroupUid);

		mShowActiveLayerButton = false;
		mShowShowLayerButton = false;

		mRenderPanelTitle = true;
		mPanelTitle = "Layers";

		mLayerListWidget = new UiVerticalTextListBox(entityGroupUid);
		mLayerListWidget.addCallbackListener(this);
		mLayerListWidget.setHeightMinMax(200, 200);

		mDeleteSelected = new UiButton("Delete");
		mDeleteSelected.setUiWidgetListener(this, BUTTON_DELETE_LAYER);
		mAddTextureLayer = new UiButton("Add Texture");
		mAddTextureLayer.setUiWidgetListener(this, BUTTON_ADD_TEX_LAYER);
		mAddAnimationLayer = new UiButton("Add Anim");
		mAddAnimationLayer.setUiWidgetListener(this, BUTTON_ADD_ANIM_LAYER);
		mAddNoiseLayer = new UiButton("Add Noise");
		mAddNoiseLayer.setUiWidgetListener(this, BUTTON_ADD_NOISE_LAYER);

		mLayerVisible = new UiCheckBox("Visible");
		mLayerVisible.setUiWidgetListener(this, BUTTON_LAYER_VISIBLE);
		mLayerVisible.isChecked(true);

		final var lHorizontaGroup = new UiHorizontalEntryGroup();

		mMoveUp = new UiButton("Up");
		mMoveUp.setUiWidgetListener(this, BUTTON_MOVE_LAYER_UP);

		mMoveDown = new UiButton("Down");
		mMoveDown.setUiWidgetListener(this, BUTTON_MOVE_LAYER_DOWN);

		lHorizontaGroup.widgets().add(mMoveUp);
		lHorizontaGroup.widgets().add(mMoveDown);

		addWidget(mLayerListWidget);
		addWidget(mLayerVisible);
		addWidget(lHorizontaGroup);
		addWidget(mDeleteSelected);
		addWidget(mAddTextureLayer);
		addWidget(mAddAnimationLayer);
		addWidget(mAddNoiseLayer);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		mEditorLayerController = (EditorLayerController) lControllerManager.getControllerByNameRequired(EditorLayerController.CONTROLLER_NAME, mEntityGroupUid);

		recreateUiListFromlayersManager();
	}

	// --------------------------------------

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case BUTTON_DELETE_LAYER: {
			final var lSelectedListBoxItem = mLayerListWidget.getSelectedItem();
			if (lSelectedListBoxItem == null)
				return;

			final var lSelectedLayerUid = lSelectedListBoxItem.itemUid;
			final var lSelectedLayer = mEditorLayerController.getLayerByUid(lSelectedLayerUid);
			if (lSelectedLayer != null) {
				mEditorLayerController.deleteSelectedLayer(lSelectedLayer);
				removeLayerFromUiList(lSelectedLayer);
			}
			break;
		}

		case BUTTON_EXPANDED: {
			if (mIsPanelOpen == false) {
				mEditorLayerController.selectedLayer(null);
			}
			break;
		}

		case BUTTON_ADD_TEX_LAYER: {
			final var lNewLayer = mEditorLayerController.addNewTextureLayer();
			addLayerToUiList(lNewLayer);

			updateLayerZDepthBasedOnOrder();
			break;
		}

		case BUTTON_ADD_ANIM_LAYER: {
			final var lNewLayer = mEditorLayerController.addNewAnimationLayer();
			addLayerToUiList(lNewLayer);

			updateLayerZDepthBasedOnOrder();
			break;
		}

		case BUTTON_ADD_NOISE_LAYER: {
			final var lNewLayer = mEditorLayerController.addNewNoiseLayer();
			addLayerToUiList(lNewLayer);

			updateLayerZDepthBasedOnOrder();
			break;
		}

		case BUTTON_SHOW_LAYER:
			if (isLayerVisible() == false) {
				mEditorLayerController.selectedLayer(null);
			}
			return;

		case BUTTON_MOVE_LAYER_UP: {
			final var selectedLayerIndex = mLayerListWidget.selectedItemIndex();
			if (selectedLayerIndex == -1)
				return;

			if (selectedLayerIndex <= 0)
				return; // already last in list

			final var layerManager = mEditorLayerController.layersManager();
			final var layerList = layerManager.layers();
			Collections.swap(layerList, selectedLayerIndex, selectedLayerIndex - 1);

			recreateUiListFromlayersManager();
			mLayerListWidget.selectedItemIndex(selectedLayerIndex - 1);

			updateLayerZDepthBasedOnOrder();
			break;
		}

		case BUTTON_MOVE_LAYER_DOWN: {
			final var selectedLayerIndex = mLayerListWidget.selectedItemIndex();
			if (selectedLayerIndex == -1)
				return;

			if (selectedLayerIndex >= mLayerListWidget.items().size() - 1)
				return; // already last in list

			final var layerManager = mEditorLayerController.layersManager();
			final var layerList = layerManager.layers();
			Collections.swap(layerList, selectedLayerIndex, selectedLayerIndex + 1);

			recreateUiListFromlayersManager();
			mLayerListWidget.selectedItemIndex(selectedLayerIndex + 1);

			updateLayerZDepthBasedOnOrder();
			break;
		}

		}
	}

	// add the layers from the manager to the ui list
	private void recreateUiListFromlayersManager() {
		mLayerListWidget.clearItems();

		final var layerManager = mEditorLayerController.layersManager();
		final var layerList = layerManager.layers();

		// Create a ui element in the list of layers for each Layer in the LayerManager.
		final var numLayers = layerList.size();
		for (int i = 0; i < numLayers; i++) {
			addLayerToUiList(layerList.get(i));
		}
	}

	private void updateLayerZDepthBasedOnOrder() {
		final var stepSize = 0.05f;

		final var items = mLayerListWidget.items();
		final var numItems = items.size();
		for (int i = 0; i < numItems; i++) {
			final var uiListBoxItem = items.get(i);
			final var sceneLayer = uiListBoxItem.layer();

			sceneLayer.zInvDepth = (i * stepSize);

		}
	}

	private void addLayerToUiList(SceneBaseLayer layer) {
		if (layer == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot add null SceneBaseLayer to UiVerticalListBox.");
			return;
		}

		final var newListItemBox = new LayerListBoxItem(layer.layerUid);
		newListItemBox.layer(layer);

		newListItemBox.displayName = layer.name;
		newListItemBox.listOrderIndex = mLayerListWidget.items().size();

		mLayerListWidget.addItem(newListItemBox);

	}

	private void removeLayerFromUiList(SceneBaseLayer layer) {
		if (layer == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot remove null SceneBaseLayer from UiVerticalListBox.");
			return;
		}

		mLayerListWidget.removeItemByUid(layer.layerUid);
	}

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case BUTTON_LAYER_VISIBLE:
			final var selectedLayerIndex = mLayerListWidget.selectedItemIndex();
			if (selectedLayerIndex == -1)
				return;

			final var layerManager = mEditorLayerController.layersManager();
			final var layerList = layerManager.layers();
			final var selectedLayer = layerList.get(selectedLayerIndex);
			selectedLayer.visible = mLayerVisible.isChecked();

			break;
		}
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onItemSelected(UiListBoxItem selectedItem) {
		if (selectedItem == null) {
			mEditorLayerController.setSelectedLayer(-1); // reset previous selection
			return;
		}

		final var layerUid = selectedItem.itemUid;
		mEditorLayerController.setSelectedLayer(layerUid);

		final var backingSceneLayer = mEditorLayerController.selectedLayer();
		if (backingSceneLayer != null) {
			mLayerVisible.isChecked(backingSceneLayer.visible);
		}
	}

	@Override
	public void onItemAdded(UiListBoxItem newItem) {

	}

	@Override
	public void onItemRemoved(UiListBoxItem oldItem) {

	}
}