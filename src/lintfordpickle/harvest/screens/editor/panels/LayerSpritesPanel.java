package lintfordpickle.harvest.screens.editor.panels;

import lintfordpickle.harvest.controllers.editor.EditorAssetsController;
import lintfordpickle.harvest.data.assets.SceneSpriteInstance;
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
import net.lintfordlib.renderers.windows.components.UiSeparator;

public class LayerSpritesPanel extends LayerPanelBase<SceneSpriteLayer> implements IUiInputKeyPressCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int INPUT_NAME_KEY_UID = 100;

	private static final int BUTTON_ADD_SPRITE = 150;
	private static final int BUTTON_DEL_SPRITE = 151;

	// layer options
	private final static int SLIDER_TRANSLATION_SPEED_X = 15;
	private final static int SLIDER_TRANSLATION_SPEED_Y = 16;

	private final static int SLIDER_CENTER_X = 17;
	private final static int SLIDER_CENTER_Y = 18;

	// selected sprite options
	private final static int SPRITE_WIDTH = 30;
	private final static int SPRITE_HEIGHT = 31;
	private final static int SPRITE_TRANSLATION_MOD_X = 32;
	private final static int SPRITE_TRANSLATION_MOD_Y = 33;
	private static final int SPRITE_RESET_SIZE = 34;

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

	// selected sprite options
	private UiInputFloat mSpriteWidth;
	private UiInputFloat mSpriteHeight;
	private UiInputFloat mSpriteTranslationSpeedModX;
	private UiInputFloat mSpriteTranslationSpeedModY;
	private UiButton mResetSpriteDimensions;

	private EditorSpriteLayerRenderer mEditorAnimationLayerRenderer;
	private SceneSpriteInstance mTrackedSelectedSprite;

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

		mSpriteWidth = new UiInputFloat();
		mSpriteWidth.setUiWidgetListener(this, SPRITE_WIDTH);
		mSpriteWidth.label("Width");
		mSpriteWidth.setMinMax(0, 0);
		mSpriteWidth.stepSize(.1f);
		mSpriteHeight = new UiInputFloat();
		mSpriteHeight.setUiWidgetListener(this, SPRITE_HEIGHT);
		mSpriteHeight.label("Height");
		mSpriteHeight.setMinMax(0, 0);
		mSpriteHeight.stepSize(.1f);

		mSpriteTranslationSpeedModX = new UiInputFloat();
		mSpriteTranslationSpeedModX.setUiWidgetListener(this, SPRITE_TRANSLATION_MOD_X);
		mSpriteTranslationSpeedModX.label("Mod X");
		mSpriteTranslationSpeedModX.setMinMax(0.f, 10.f);
		mSpriteTranslationSpeedModX.stepSize(.1f);
		mSpriteTranslationSpeedModY = new UiInputFloat();
		mSpriteTranslationSpeedModY.setUiWidgetListener(this, SPRITE_TRANSLATION_MOD_Y);
		mSpriteTranslationSpeedModY.label("Mod Y");
		mSpriteTranslationSpeedModY.setMinMax(0.f, 10.f);
		mSpriteTranslationSpeedModY.stepSize(.1f);

		mResetSpriteDimensions = new UiButton("Reset Size");
		mResetSpriteDimensions.setUiWidgetListener(this, SPRITE_RESET_SIZE);

		final var horizontalGroup0 = new UiHorizontalEntryGroup();
		horizontalGroup0.widgets().add(mAddAnimationButton);
		horizontalGroup0.widgets().add(mRemoveAnimationButton);

		final var horizontalGroup1 = new UiHorizontalEntryGroup();
		horizontalGroup1.widgets().add(mCenterXInput);
		horizontalGroup1.widgets().add(mCenterYInput);

		final var horizontalGroup2 = new UiHorizontalEntryGroup();
		horizontalGroup2.widgets().add(mTranslationSpeedModX);
		horizontalGroup2.widgets().add(mTranslationSpeedModY);

		final var horizontalGroup3 = new UiHorizontalEntryGroup();
		horizontalGroup3.widgets().add(mSpriteWidth);
		horizontalGroup3.widgets().add(mSpriteHeight);

		final var horizontalGroup4 = new UiHorizontalEntryGroup();
		horizontalGroup4.widgets().add(mSpriteTranslationSpeedModX);
		horizontalGroup4.widgets().add(mSpriteTranslationSpeedModY);

		addWidget(mNameLabel);
		addWidget(mLayerName);
		addWidget(mAnimationAssetList);
		addWidget(horizontalGroup0);
		addWidget(new UiSeparator());
		addWidget(new UiLabel("Layer"));
		addWidget(horizontalGroup1);
		addWidget(horizontalGroup2);
		addWidget(new UiSeparator());
		addWidget(new UiLabel("Sprite"));
		addWidget(horizontalGroup3);
		addWidget(horizontalGroup4);
		addWidget(mResetSpriteDimensions);
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
		final var listCollectionIterator = assetDefinitionList.iterator();

		while (listCollectionIterator.hasNext()) {
			final var assetDefinition = listCollectionIterator.next();

			if (assetDefinition == null)
				continue;

			final var newItem = new UiListBoxImageItem((int) assetDefinition.definitionUid());
			newItem.setAsset(assetDefinition.definitionName(), assetDefinition.displayName);
			newItem.setIconFrom(assetDefinition.iconSpriteContainer);

			newItem.iconContainer.loadResources(core.resources(), mEntityGroupUid);

			mAnimationAssetList.addItem(newItem);
		}
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		// Here sync selected sprite in controller with the panel, check for changes
		final var controllerSelected = mEditorAssetsController.selectedAssetinstance();
		final var spriteChange = controllerSelected != mTrackedSelectedSprite;

		if (spriteChange) {
			updateUiOnSpriteChange();
		}

	}

	private void updateUiOnSpriteChange() {
		final var controllerSelected = mEditorAssetsController.selectedAssetinstance();
		if (controllerSelected == null) {

			// controller is unset

			if (mTrackedSelectedSprite != null) {
				// update Ui
				mSpriteWidth.currentValue(0);
				mSpriteHeight.currentValue(0);

				// deselected
				mTrackedSelectedSprite = null;

			} else {
				// ignore
			}
		} else {

			// controller is set

			if (mTrackedSelectedSprite != null) {
				// should be ignored (same already set?)
			} else {
				// update Ui
				mTrackedSelectedSprite = controllerSelected;

				mSpriteWidth.currentValue(mTrackedSelectedSprite.destRect.width());
				mSpriteHeight.currentValue(mTrackedSelectedSprite.destRect.height());

			}
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
		if (!mEditorBrushController.isLayerActive(EditorLayersData.Layer_Animation))
			return;

		switch (entryUid) {
		case SPRITE_RESET_SIZE: {
			final var selectedSpriteInstance = mEditorAssetsController.selectedAssetinstance();
			if (selectedSpriteInstance != null) {
				final var assetDefinition = selectedSpriteInstance.definition;
				selectedSpriteInstance.destRect.width(assetDefinition.propDefaultWidth);
				selectedSpriteInstance.destRect.height(assetDefinition.propDefaultHeight);
			}

			break;
		}

		case BUTTON_SHOW_LAYER: {
			if (mEditorAnimationLayerRenderer != null) {
				final var curentVisibility = mEditorAnimationLayerRenderer.renderSpritesLayer();
				mEditorAnimationLayerRenderer.renderSpritesLayer(!curentVisibility);
			}
			break;
		}

		case BUTTON_SET_LAYER: {
			if (mSelectedLayer != null) {
				mSelectedLayer.editMode = isLayerActive();
			}
			break;
		}

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

			final var selectedSpriteInstance = mEditorAssetsController.selectedAssetinstance();
			if (selectedSpriteInstance != null) {
				mSelectedLayer.removeAssetInstance(selectedSpriteInstance);
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
			
		case SPRITE_WIDTH: {
			final var selectedSpriteInstance = mEditorAssetsController.selectedAssetinstance();
			if (selectedSpriteInstance != null) {
				selectedSpriteInstance.destRect.width(mSpriteWidth.currentValue());
			}
			break;
		}

		case SPRITE_HEIGHT: {
			final var selectedSpriteInstance = mEditorAssetsController.selectedAssetinstance();
			if (selectedSpriteInstance != null) {
				selectedSpriteInstance.destRect.height(mSpriteHeight.currentValue());
			}
			break;
		}
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