package lintfordpickle.harvest.screens.editor.panels;

import lintfordpickle.harvest.controllers.editor.EditorAssetsController;
import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import lintfordpickle.harvest.renderers.editor.EditorLayersRenderer;
import lintfordpickle.harvest.renderers.editor.EditorSpriteLayerRenderer;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.keyboard.IUiInputKeyPressCallback;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
import net.lintfordlib.renderers.windows.components.UiHorizontalEntryGroup;
import net.lintfordlib.renderers.windows.components.UiHorizontalListBox;
import net.lintfordlib.renderers.windows.components.UiInputFloat;
import net.lintfordlib.renderers.windows.components.UiInputInteger;
import net.lintfordlib.renderers.windows.components.UiInputText;
import net.lintfordlib.renderers.windows.components.UiLabel;
import net.lintfordlib.renderers.windows.components.UiListBoxImageItem;

public class LayerSpritesPanel extends LayerPanelBase<SceneSpriteLayer> implements IUiInputKeyPressCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int INPUT_NAME_KEY_UID = 100;

	private static final int BUTTON_ADD_SPRITE = 150;
	private static final int BUTTON_DEL_SPRITE = 151;

	private final static int SLIDER_TRANSLATION_SPEED_X = 15;
	private final static int SLIDER_TRANSLATION_SPEED_Y = 16;

	private final static int SLIDER_CENTER_X = 17;
	private final static int SLIDER_CENTER_Y = 18;

	private final static int SLIDER_SCALE_X = 19;
	private final static int SLIDER_SCALE_Y = 20;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EditorAssetsController mEditorAssetsController;
	private EditorBrushController mEditorBrushController;
	private UiHorizontalListBox mAnimationAssetList;

	private UiLabel mNameLabel;
	private UiInputText mLayerName;

	private UiButton mAddAnimationButton;
	private UiButton mRemoveAnimationButton;

	private UiInputInteger mCenterXInput;
	private UiInputInteger mCenterYInput;

	private UiInputFloat mTranslationSpeedModX;
	private UiInputFloat mTranslationSpeedModY;

	private UiInputFloat mWidth;
	private UiInputFloat mHeight;

	private EditorSpriteLayerRenderer mEditorAnimationLayerRenderer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return mEditorAnimationLayerRenderer.hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LayerSpritesPanel(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "Sprites Layer", entityGroupUid);

		mEditorActiveLayerUid = EditorLayersData.Layer_Animation;

		mShowShowLayerButton = true;
		mShowActiveLayerButton = true;
		mIsExpandable = false;
		mIsPanelOpen = false;

		mNameLabel = new UiLabel("Layer Name");
		mLayerName = new UiInputText();
		mLayerName.setKeyUpdateListener(this, INPUT_NAME_KEY_UID);

		mAnimationAssetList = new UiHorizontalListBox(entityGroupUid);
		mAnimationAssetList.desiredHeight(170);

		mAddAnimationButton = new UiButton("Add");
		mAddAnimationButton.setUiWidgetListener(this, BUTTON_ADD_SPRITE);
		mRemoveAnimationButton = new UiButton("Delete");
		mRemoveAnimationButton.setUiWidgetListener(this, BUTTON_DEL_SPRITE);

		// ---

		mCenterXInput = new UiInputInteger();
		mCenterXInput.setUiWidgetListener(this, SLIDER_CENTER_X);
		mCenterXInput.label("CenterX");
		mCenterXInput.setMinMax(0, 0);

		mCenterYInput = new UiInputInteger();
		mCenterYInput.setUiWidgetListener(this, SLIDER_CENTER_Y);
		mCenterYInput.label("CenterY");
		mCenterYInput.setMinMax(0, 0);

		mTranslationSpeedModX = new UiInputFloat();
		mTranslationSpeedModX.setUiWidgetListener(this, SLIDER_TRANSLATION_SPEED_X);
		mTranslationSpeedModX.label("Mod X");
		mTranslationSpeedModX.setMinMax(-20.f, 20.f);
		mTranslationSpeedModX.stepSize(.1f);

		mTranslationSpeedModY = new UiInputFloat();
		mTranslationSpeedModY.setUiWidgetListener(this, SLIDER_TRANSLATION_SPEED_Y);
		mTranslationSpeedModY.label("Mod Y");
		mTranslationSpeedModY.setMinMax(0.f, 10.f);
		mTranslationSpeedModY.stepSize(.1f);

		mWidth = new UiInputFloat();
		mWidth.setUiWidgetListener(this, SLIDER_SCALE_X);
		mWidth.label("Width");
		mWidth.setMinMax(0.f, 10.f);
		mWidth.stepSize(.1f);
		mHeight = new UiInputFloat();
		mHeight.setUiWidgetListener(this, SLIDER_SCALE_Y);
		mHeight.label("Height");
		mHeight.setMinMax(0.f, 10.f);
		mHeight.stepSize(.1f);

		final var lHorizontalGroup0 = new UiHorizontalEntryGroup();
		lHorizontalGroup0.widgets().add(mAddAnimationButton);
		lHorizontalGroup0.widgets().add(mRemoveAnimationButton);

		final var lHorizontalGroup1 = new UiHorizontalEntryGroup();
		lHorizontalGroup1.widgets().add(mCenterXInput);
		lHorizontalGroup1.widgets().add(mCenterYInput);

		final var lHorizontalGroup2 = new UiHorizontalEntryGroup();
		lHorizontalGroup2.widgets().add(mTranslationSpeedModX);
		lHorizontalGroup2.widgets().add(mTranslationSpeedModY);

		final var lHorizontalGroup3 = new UiHorizontalEntryGroup();
		lHorizontalGroup3.widgets().add(mWidth);
		lHorizontalGroup3.widgets().add(mHeight);

		addWidget(mNameLabel);
		addWidget(mLayerName);
		addWidget(mAnimationAssetList);
		addWidget(lHorizontalGroup0);
		addWidget(lHorizontalGroup1);
		addWidget(lHorizontalGroup2);
		addWidget(lHorizontalGroup3);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		mEditorAssetsController = (EditorAssetsController) lControllerManager.getControllerByNameRequired(EditorAssetsController.CONTROLLER_NAME, mEntityGroupUid);
		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, mEntityGroupUid);

		loadAssets(core);

		final var layersRenderer = (EditorLayersRenderer) mParentWindow.rendererManager().getRenderer(EditorLayersRenderer.RENDERER_NAME);
		mEditorAnimationLayerRenderer = layersRenderer.spritesLayerRenderer();

		isLayerVisible(true);
		mEditorAnimationLayerRenderer.renderSpritesLayer(true);

	}

	private void loadAssets(LintfordCore core) {
		final var assetDefinitionManager = mEditorAssetsController.sceneAssetsManager().definitionManager();
		final var assetDefinitionList = assetDefinitionManager.definitions();
		final var lListCollectionIterator = assetDefinitionList.iterator();

		while (lListCollectionIterator.hasNext()) {
			final var lAssetDefinition = lListCollectionIterator.next();

			if (lAssetDefinition == null)
				continue;

			final var lNewItem = new UiListBoxImageItem((int) lAssetDefinition.definitionUid());
			lNewItem.setAsset(lAssetDefinition.definitionName(), lAssetDefinition.displayName);
			lNewItem.setIconFrom(lAssetDefinition.iconSpriteContainer);

			lNewItem.iconContainer.loadResources(core.resources(), mEntityGroupUid);

			mAnimationAssetList.addItem(lNewItem);
		}
	}

	// --------------------------------------

	protected void newLayerSelected(SceneBaseLayer selectedLayer) {
		if (selectedLayer instanceof SceneSpriteLayer) {
			selectLayer((SceneSpriteLayer) selectedLayer);
		}
	}

	protected void selectLayer(SceneSpriteLayer selectedLayer) {
		mSelectedLayer = selectedLayer;
		mIsExpandable = true;
		mIsPanelOpen = true;

		mLayerName.inputString(selectedLayer.name);
	}

	// --------------------------------------

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case BUTTON_SHOW_LAYER:
			if (mEditorAnimationLayerRenderer != null) {
				final var curentVisibility = mEditorAnimationLayerRenderer.renderSpritesLayer();
				mEditorAnimationLayerRenderer.renderSpritesLayer(!curentVisibility);
			}
			break;

		case BUTTON_SET_LAYER:
			if (mSelectedLayer != null) {
				mSelectedLayer.editMode = isLayerActive();
			}
			break;

		// Add from asset list
		case BUTTON_ADD_SPRITE: {
			if (mSelectedLayer == null)
				return;

			final var selectedAsset = mAnimationAssetList.getSelectedItem();
			if (selectedAsset != null) {
				final var assetDefinitionName = selectedAsset.definitionName;

				final var worldX = mEditorBrushController.cursorWorldX();
				final var worldY = mEditorBrushController.cursorWorldY();

				final var newAssetInstance = mEditorAssetsController.sceneAssetsManager().createAssetInstanceFromDefinitionName(assetDefinitionName, worldX, worldY);

				if (newAssetInstance != null)
					mSelectedLayer.addAssetToLayer(newAssetInstance);

			}
			break;
		}

		// Remove currently selected sprite
		case BUTTON_DEL_SPRITE: {
			if (mSelectedLayer == null)
				return;

			final var selectedAssetInstance = mEditorAssetsController.selectedAssetinstance();
			if (selectedAssetInstance != null) {
				mSelectedLayer.removeAssetInstance(selectedAssetInstance);
				mEditorAssetsController.selectedAssetinstance(null);
			}
		}
		}
	}

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case INPUT_NAME_KEY_UID:
			if (mSelectedLayer == null)
				return;

			mSelectedLayer.name = mLayerName.inputString().toString();
			break;

		case SLIDER_SCALE_X:
			if (mSelectedLayer == null)
				return;

			mSelectedLayer.width = mWidth.currentValue();
			break;

		case SLIDER_SCALE_Y:
			if (mSelectedLayer == null)
				return;

			mSelectedLayer.height = mHeight.currentValue();
			break;

		case SLIDER_CENTER_X:
			if (mSelectedLayer == null)
				return;

			mSelectedLayer.centerX = mCenterXInput.currentValue();
			break;

		case SLIDER_CENTER_Y:
			if (mSelectedLayer == null)
				return;

			mSelectedLayer.centerY = mCenterYInput.currentValue();
			break;

		case SLIDER_TRANSLATION_SPEED_X:
			if (mSelectedLayer == null)
				return;

			mSelectedLayer.translationSpeedModX = mTranslationSpeedModX.currentValue();

			break;
		case SLIDER_TRANSLATION_SPEED_Y:
			if (mSelectedLayer == null)
				return;

			mSelectedLayer.translationSpeedModY = mTranslationSpeedModY.currentValue();

			break;
		}
	}

	@Override
	public void keyPressUpdate(int codePoint) {
	}

	@Override
	public void UiInputEnded(int inputUid) {
		if (inputUid == INPUT_NAME_KEY_UID && mSelectedLayer != null) {
			mSelectedLayer.name = mLayerName.inputString().toString();

			refreshLayerPanels();
		}
	}

}