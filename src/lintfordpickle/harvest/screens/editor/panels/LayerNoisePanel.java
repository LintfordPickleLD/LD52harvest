package lintfordpickle.harvest.screens.editor.panels;

import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneNoiseLayer;
import lintfordpickle.harvest.renderers.editor.EditorLayersRenderer;
import lintfordpickle.harvest.renderers.editor.EditorNoiseLayerRenderer;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.keyboard.IUiInputKeyPressCallback;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiHorizontalEntryGroup;
import net.lintfordlib.renderers.windows.components.UiInputFloat;
import net.lintfordlib.renderers.windows.components.UiInputInteger;
import net.lintfordlib.renderers.windows.components.UiInputText;
import net.lintfordlib.renderers.windows.components.UiLabel;

public class LayerNoisePanel extends LayerPanel<SceneNoiseLayer> implements IUiInputKeyPressCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int INPUT_NAME_KEY_UID = 100;

	private final static int SLIDER_TRANSLATION_SPEED_X = 15;
	private final static int SLIDER_TRANSLATION_SPEED_Y = 16;

	private final static int SLIDER_CENTER_X = 17;
	private final static int SLIDER_CENTER_Y = 18;

	private final static int SLIDER_SCALE_X = 19;
	private final static int SLIDER_SCALE_Y = 20;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiLabel mNameLabel;
	private UiInputText mLayerName;

	private UiInputInteger mCenterXInput;
	private UiInputInteger mCenterYInput;

	private UiInputFloat mTranslationSpeedModX;
	private UiInputFloat mTranslationSpeedModY;

	private UiInputFloat mWidth;
	private UiInputFloat mHeight;

	private EditorNoiseLayerRenderer mEditorNoiseLayerRenderer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return mEditorNoiseLayerRenderer.hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LayerNoisePanel(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "Noise Layer", entityGroupUid);

		mEditorActiveLayerUid = EditorLayersData.Layer_Noise;

		mShowShowLayerButton = true;
		mShowActiveLayerButton = true;
		mIsExpandable = false;
		mIsPanelOpen = false;

		mNameLabel = new UiLabel("Name");
		mLayerName = new UiInputText();
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

		addWidget(mNameLabel);
		addWidget(mLayerName);

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

		final var layersRenderer = (EditorLayersRenderer) mParentWindow.rendererManager().getRenderer(EditorLayersRenderer.RENDERER_NAME);
		mEditorNoiseLayerRenderer = layersRenderer.noiseLayerRenderer();

		isLayerVisible(true);
		mEditorNoiseLayerRenderer.renderNoiseLayer(true);

	}

	protected void newLayerSelected(SceneBaseLayer selectedLayer) {
		if (selectedLayer instanceof SceneNoiseLayer) {
			selectLayer((SceneNoiseLayer) selectedLayer);
		}
	}

	protected void selectLayer(SceneNoiseLayer selectedLayer) {
		mSelectedLayer = selectedLayer;
		mIsExpandable = true;
		mIsPanelOpen = true;

		mLayerName.inputString(selectedLayer.name);
	}

	// --------------------------------------

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
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