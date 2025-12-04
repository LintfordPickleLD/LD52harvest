package lintfordpickle.harvest.screens.editor.panels;

import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneTextureLayer;
import lintfordpickle.harvest.renderers.editor.EditorTextureLayerRenderer;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.keyboard.IUiInputKeyPressCallback;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
import net.lintfordlib.renderers.windows.components.UiHorizontalEntryGroup;
import net.lintfordlib.renderers.windows.components.UiInputFloat;
import net.lintfordlib.renderers.windows.components.UiInputInteger;
import net.lintfordlib.renderers.windows.components.UiInputText;
import net.lintfordlib.renderers.windows.components.UiLabel;

public class LayerTexturePanel extends LayerPanel<SceneTextureLayer> implements IUiInputKeyPressCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private final static int BUTTON_TEXTURE_NAME = 5;
	private final static int BUTTON_TEXTURE_PATH = 6;
	private final static int BUTTON_REFRESH = 10;

	private final static int SLIDER_TRANSLATION_SPEED_X = 15;
	private final static int SLIDER_TRANSLATION_SPEED_Y = 16;

	private final static int SLIDER_CENTER_X = 17;
	private final static int SLIDER_CENTER_Y = 18;

	private final static int SLIDER_SCALE_X = 19;
	private final static int SLIDER_SCALE_Y = 20;

	private static final int INPUT_NAME_KEY_UID = 100;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiLabel mLayerNameLabel;
	private UiInputText mLayerName;

	private UiLabel mTextureNameLabel;
	private UiInputText mTextureName;

	private UiLabel mTexturePathLabel;
	private UiInputText mTexturePath;
	private UiButton mRefreshButton;

	private UiInputInteger mCenterXInput;
	private UiInputInteger mCenterYInput;

	private UiInputFloat mTranslationSpeedModX;
	private UiInputFloat mTranslationSpeedModY;

	private UiInputFloat mWidth;
	private UiInputFloat mHeight;

	private EditorTextureLayerRenderer mEditorTextureLayerRenderer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return mEditorTextureLayerRenderer.hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LayerTexturePanel(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "Texture Layer", entityGroupUid);

		mEditorActiveLayerUid = EditorLayersData.Layer_Texture;

		mShowShowLayerButton = true;
		mShowActiveLayerButton = true;
		mIsExpandable = false;
		mIsPanelOpen = false;

		mLayerNameLabel = new UiLabel("Layer Name");
		mLayerName = new UiInputText();

		mTextureNameLabel = new UiLabel("Name");
		mTextureName = new UiInputText();
		mTexturePathLabel = new UiLabel("Path");
		mTexturePath = new UiInputText();
		mTexturePath.maxnumInputCharacters(160);
		mRefreshButton = new UiButton("Refresh");

		mTextureName.setUiWidgetListener(this, BUTTON_TEXTURE_NAME);
		mTexturePath.setUiWidgetListener(this, BUTTON_TEXTURE_PATH);
		mTexturePath.inputString("res/textures/");
		mTexturePath.emptyString("res/textures/");
		mRefreshButton.setUiWidgetListener(this, BUTTON_REFRESH);
		mLayerName.setKeyUpdateListener(this, INPUT_NAME_KEY_UID);

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
		lHorizontalGroup0.widgets().add(mCenterXInput);
		lHorizontalGroup0.widgets().add(mCenterYInput);

		final var lHorizontalGroup1 = new UiHorizontalEntryGroup();
		lHorizontalGroup1.widgets().add(mTranslationSpeedModX);
		lHorizontalGroup1.widgets().add(mTranslationSpeedModY);

		final var lHorizontalGroup2 = new UiHorizontalEntryGroup();
		lHorizontalGroup2.widgets().add(mWidth);
		lHorizontalGroup2.widgets().add(mHeight);

		addWidget(mLayerNameLabel);
		addWidget(mLayerName);

		addWidget(mTextureNameLabel);
		addWidget(mTextureName);
		addWidget(mTexturePathLabel);
		addWidget(mTexturePath);

		addWidget(mRefreshButton);

		addWidget(lHorizontalGroup0);
		addWidget(lHorizontalGroup1);
		addWidget(lHorizontalGroup2);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		mEditorTextureLayerRenderer = (EditorTextureLayerRenderer) mParentWindow.rendererManager().getRenderer(EditorTextureLayerRenderer.RENDERER_NAME);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lSelectedLayer = mEditorLayerController.selectedLayer();

		if (lSelectedLayer != null) {

			if (mCenterXInput.currentValue() != lSelectedLayer.centerX) {
				mCenterXInput.inputString((int) lSelectedLayer.centerX);
			}

			if (mCenterYInput.currentValue() != lSelectedLayer.centerY) {
				mCenterYInput.inputString((int) lSelectedLayer.centerY);
			}

			if (mWidth.hasFocus() == false && mWidth.currentValue() != lSelectedLayer.width) {
				mWidth.inputString(lSelectedLayer.width);
			}

			if (mHeight.hasFocus() == false && mHeight.currentValue() != lSelectedLayer.height) {
				mHeight.inputString(lSelectedLayer.height);
			}
		}
	}

	protected void newLayerSelected(SceneBaseLayer selectedLayer) {
		if (selectedLayer instanceof SceneTextureLayer) {
			selectLayer((SceneTextureLayer) selectedLayer);
		}
	}

	protected void selectLayer(SceneTextureLayer selectedLayer) {
		mSelectedLayer = selectedLayer;
		mIsExpandable = true;
		mIsPanelOpen = true;

		mLayerName.inputString(selectedLayer.name);
		if (selectedLayer.textureName() != null)
			mTextureName.inputString(selectedLayer.textureName());

		if (selectedLayer.textureFilepath() != null)
			mTexturePath.inputString(selectedLayer.textureFilepath());

		mTranslationSpeedModX.inputString(selectedLayer.translationSpeedModX);
		mTranslationSpeedModY.inputString(selectedLayer.translationSpeedModY);

		mWidth.inputString(selectedLayer.width);
		mHeight.inputString(selectedLayer.height);

	}

	// --------------------------------------

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case BUTTON_REFRESH:

			if (mSelectedLayer == null)
				return;

			mSelectedLayer.name = mLayerName.inputString().toString();
			mSelectedLayer.setTextureName(mTextureName.inputString().toString());
			mSelectedLayer.setTextureFilepath(mTexturePath.inputString().toString());
			mSelectedLayer.texture = null;

			break;

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

		case BUTTON_TEXTURE_NAME:
			if (mSelectedLayer == null)
				return;

			mSelectedLayer.setTextureName(mTextureName.inputString().toString());
			break;

		case BUTTON_TEXTURE_PATH:
			if (mSelectedLayer == null)
				return;

			mSelectedLayer.setTextureFilepath(mTexturePath.inputString().toString());
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