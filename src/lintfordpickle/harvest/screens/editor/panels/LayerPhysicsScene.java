package lintfordpickle.harvest.screens.editor.panels;

import lintfordpickle.harvest.controllers.editor.EditorSceneController;
import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.scene.physics.PhysicsSettingsManager;
import lintfordpickle.harvest.renderers.editor.EditorPhysicsSettingsRenderer;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.controllers.editor.EditorHashGridController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.editor.panels.UiPanel;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
import net.lintfordlib.renderers.windows.components.UiHorizontalEntryGroup;
import net.lintfordlib.renderers.windows.components.UiInputFloat;
import net.lintfordlib.renderers.windows.components.UiInputInteger;

public class LayerPhysicsScene extends UiPanel {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int INPUT_GRAVITY_X = 10;
	private static final int INPUT_GRAVITY_Y = 11;

	private static final int INPUT_GRID_WIDTH = 20;
	private static final int INPUT_GRID_HEIGHT = 21;
	private static final int INPUT_GRID_TILES_WIDE = 22;
	private static final int INPUT_GRID_TILES_HIGH = 23;
	private static final int INPUT_TAKE_FROM_SCENE_GRID = 24;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EditorSceneController mEditorSceneController;
	private PhysicsSettingsManager mPhysicsSettingsManager;
	private EditorPhysicsSettingsRenderer mEditorPhysicsSettingsRenderer;
	private EditorHashGridController mHashGridController;

	private UiInputFloat mGravityX;
	private UiInputFloat mGravityY;
	private UiInputFloat mGridWidth;
	private UiInputFloat mGridHeight;
	private UiInputInteger mGridTilesWide;
	private UiInputInteger mGridTilesHigh;
	private UiButton mTakeFromGridSettings;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return mEditorPhysicsSettingsRenderer.hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LayerPhysicsScene(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "Physics Settings", entityGroupUid);

		mShowActiveLayerButton = false;
		mShowShowLayerButton = true;

		mEditorActiveLayerUid = EditorLayersData.Physics;

		mRenderPanelTitle = true;
		mPanelTitle = "Physics Settings";

		mGravityX = new UiInputFloat();
		mGravityX.label("Gravity X");
		mGravityX.setUiWidgetListener(this, INPUT_GRAVITY_X);
		mGravityY = new UiInputFloat();
		mGravityY.label("Gravity Y");
		mGravityY.setUiWidgetListener(this, INPUT_GRAVITY_Y);

		mGridWidth = new UiInputFloat();
		mGridWidth.label("Width");
		mGridWidth.setMinMax(500, 10000);
		mGridWidth.stepSize(1);
		mGridWidth.setUiWidgetListener(this, INPUT_GRID_WIDTH);

		mGridHeight = new UiInputFloat();
		mGridHeight.label("Height");
		mGridHeight.setMinMax(500, 10000);
		mGridHeight.stepSize(50);
		mGridHeight.setUiWidgetListener(this, INPUT_GRID_HEIGHT);

		mGridTilesWide = new UiInputInteger();
		mGridTilesWide.label("Tiles X");
		mGridTilesWide.setMinMax(5, 20);
		mGridTilesWide.setUiWidgetListener(this, INPUT_GRID_TILES_WIDE);

		mGridTilesHigh = new UiInputInteger();
		mGridTilesHigh.label("Tiles Y");
		mGridTilesHigh.setMinMax(5, 20);
		mGridTilesHigh.setUiWidgetListener(this, INPUT_GRID_TILES_HIGH);

		mTakeFromGridSettings = new UiButton();
		mTakeFromGridSettings.buttonLabel("Take From Scene Grid");
		mTakeFromGridSettings.setUiWidgetListener(this, INPUT_TAKE_FROM_SCENE_GRID);

		UiHorizontalEntryGroup mGridSize = new UiHorizontalEntryGroup();
		mGridSize.widgets().add(mGridWidth);
		mGridSize.widgets().add(mGridHeight);

		UiHorizontalEntryGroup mGridCells = new UiHorizontalEntryGroup();
		mGridCells.widgets().add(mGridTilesWide);
		mGridCells.widgets().add(mGridTilesHigh);

		addWidget(mGravityX);
		addWidget(mGravityY);

		addWidget(mGridSize);
		addWidget(mGridCells);
		addWidget(mTakeFromGridSettings);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();

		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, mEntityGroupUid);
		mEditorBrushController.showPosition(true);

		mEditorSceneController = (EditorSceneController) lControllerManager.getControllerByNameRequired(EditorSceneController.CONTROLLER_NAME, mEntityGroupUid);
		mPhysicsSettingsManager = mEditorSceneController.sceneData().physicsSettingsManager();

		mHashGridController = (EditorHashGridController) lControllerManager.getControllerByNameRequired(EditorHashGridController.CONTROLLER_NAME, mEntityGroupUid);

		final var lPhysicsSettings = mPhysicsSettingsManager.physicsSettings();

		mGravityX.inputString(lPhysicsSettings.gravityX);
		mGravityY.inputString(lPhysicsSettings.gravityY);

		mGridWidth.currentValue(lPhysicsSettings.hashGridWidthInUnits);
		mGridHeight.currentValue(lPhysicsSettings.hashGridHeightInUnits);
		mGridTilesWide.currentValue(lPhysicsSettings.hashGridCellsWide);
		mGridTilesHigh.currentValue(lPhysicsSettings.hashGridCellsHigh);

		mEditorPhysicsSettingsRenderer = (EditorPhysicsSettingsRenderer) mParentWindow.rendererManager().getRenderer(EditorPhysicsSettingsRenderer.RENDERER_NAME);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case BUTTON_SHOW_LAYER:
			final var lCurentVisibility = mEditorPhysicsSettingsRenderer.renderHashGrid();
			mEditorPhysicsSettingsRenderer.renderHashGrid(!lCurentVisibility);
			return;

		case INPUT_TAKE_FROM_SCENE_GRID:
			final var sceneHashGrid = mHashGridController.hashGrid();
			final var lPhysicsSettings = mPhysicsSettingsManager.physicsSettings();

			final var lToUnits = ConstantsPhysics.PixelsToUnits();

			lPhysicsSettings.hashGridWidthInUnits = sceneHashGrid.boundaryWidth() * lToUnits;
			lPhysicsSettings.hashGridHeightInUnits = sceneHashGrid.boundaryHeight() * lToUnits;
			lPhysicsSettings.hashGridCellsWide = sceneHashGrid.numTilesWide();
			lPhysicsSettings.hashGridCellsHigh = sceneHashGrid.numTilesHigh();

			mGridWidth.currentValue(sceneHashGrid.boundaryWidth());
			mGridHeight.currentValue(sceneHashGrid.boundaryHeight());
			mGridTilesWide.currentValue(sceneHashGrid.numTilesWide());
			mGridTilesHigh.currentValue(sceneHashGrid.numTilesHigh());

			return;
		}
	}

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {

		final var lToUnits = ConstantsPhysics.PixelsToUnits();
		final var lPhysicsSettings = mPhysicsSettingsManager.physicsSettings();

		switch (entryUid) {
		case INPUT_GRAVITY_X: {
			lPhysicsSettings.gravityX = mGravityX.currentValue();
			return;
		}

		case INPUT_GRAVITY_Y: {
			lPhysicsSettings.gravityY = mGravityY.currentValue();
			return;
		}

		case INPUT_GRID_WIDTH: {
			lPhysicsSettings.hashGridWidthInUnits = (int) (mGridWidth.currentValue() * lToUnits);
			return;
		}

		case INPUT_GRID_HEIGHT: {
			lPhysicsSettings.hashGridHeightInUnits = (int) (mGridHeight.currentValue() * lToUnits);
			return;
		}

		case INPUT_GRID_TILES_WIDE: {
			lPhysicsSettings.hashGridCellsWide = mGridTilesWide.currentValue();
			return;
		}

		case INPUT_GRID_TILES_HIGH: {
			lPhysicsSettings.hashGridCellsHigh = mGridTilesHigh.currentValue();
			return;
		}

		}
	}

}