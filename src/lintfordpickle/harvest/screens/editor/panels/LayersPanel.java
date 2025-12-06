package lintfordpickle.harvest.screens.editor.panels;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.data.editor.LayerListBoxItem;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.editor.panels.UiPanel;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
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

	public static final int BUTTON_MOVE_LAYER_UP = 50;
	public static final int BUTTON_MOVE_LAYER_DOWN = 51;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiVerticalTextListBox mLayerListWidget;

	private UiButton mDeleteSelected;
	private UiButton mAddTextureLayer;
	private UiButton mAddAnimationLayer;
	private UiButton mAddNoiseLayer;

	private UiButton mMoveUp;
	private UiButton mMoveDown;

	private EditorLayerController mEditorLayerController;

	private final List<SceneBaseLayer> mTempOrderedSceneList = new ArrayList<>();

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

		final var lHorizontaGroup = new UiHorizontalEntryGroup();

		mMoveUp = new UiButton("Up");
		mMoveUp.setUiWidgetListener(this, BUTTON_MOVE_LAYER_UP);

		mMoveDown = new UiButton("Down");
		mMoveDown.setUiWidgetListener(this, BUTTON_MOVE_LAYER_DOWN);

		lHorizontaGroup.widgets().add(mMoveUp);
		lHorizontaGroup.widgets().add(mMoveDown);

		addWidget(mLayerListWidget);
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

		addLoadedLayersToListBox();
	}

	private void addLoadedLayersToListBox() {
		final var lLayerManager = mEditorLayerController.layersManager();
		final var lLayerList = lLayerManager.layers();

		final var lNumLayers = lLayerList.size();
		for (int i = 0; i < lNumLayers; i++) {
			addLayerToUiList(lLayerList.get(i));
		}

		refreshLayerItems();
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
			break;
		}

		case BUTTON_ADD_ANIM_LAYER: {
			final var lNewLayer = mEditorLayerController.addNewAnimationLayer();
			addLayerToUiList(lNewLayer);
			break;
		}

		case BUTTON_ADD_NOISE_LAYER: {
			final var lNewLayer = mEditorLayerController.addNewNoiseLayer();
			addLayerToUiList(lNewLayer);
			break;
		}

		case BUTTON_SHOW_LAYER:
			if (isLayerVisible() == false) {
				mEditorLayerController.selectedLayer(null);
			}
			return;

		case BUTTON_MOVE_LAYER_UP: {
			mLayerListWidget.moveSelectedIndexUp();

			break;
		}

		case BUTTON_MOVE_LAYER_DOWN: {
			mLayerListWidget.moveSelectedIndexDown();

			break;
		}

		}

		refreshLayerItems();
	}

	private void addLayerToUiList(SceneBaseLayer layer) {
		if (layer == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Cannot add null SceneBaseLayer to UiVerticalListBox.");
			return;
		}

		final var lNewListItemBox = new LayerListBoxItem(layer.layerUid);
		lNewListItemBox.layer(layer);

		lNewListItemBox.displayName = layer.name;
		lNewListItemBox.listOrderIndex = mLayerListWidget.items().size();
		layer.zDepth = lNewListItemBox.listOrderIndex;

		mLayerListWidget.addItem(lNewListItemBox);

	}

	public void refreshLayerItems() {
		final var lLayerListItems = mLayerListWidget.items();
		final var lNumLayerItems = lLayerListItems.size();

		mTempOrderedSceneList.clear();

		for (int i = 0; i < lNumLayerItems; i++) {
			final var lListBoxLayerItem = (LayerListBoxItem) mLayerListWidget.items().get(i);
			final var lSceneLayer = lListBoxLayerItem.layer();

			lListBoxLayerItem.displayName = lSceneLayer.name;
			lSceneLayer.zDepth = i;

			mTempOrderedSceneList.add(lSceneLayer);
		}

		mEditorLayerController.reorderLayersPerZDepth(mTempOrderedSceneList);
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

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onItemSelected(UiListBoxItem selectedItem) {
		if (selectedItem == null) {
			mEditorLayerController.setSelectedLayer(-1);
			return;
		}

		final var lLayerUid = selectedItem.itemUid;
		mEditorLayerController.setSelectedLayer(lLayerUid);

		final var lBackingSceneLayer = mEditorLayerController.selectedLayer();
		if (lBackingSceneLayer != null) {

		}
	}

	@Override
	public void onItemAdded(UiListBoxItem newItem) {

	}

	@Override
	public void onItemRemoved(UiListBoxItem oldItem) {

	}
}