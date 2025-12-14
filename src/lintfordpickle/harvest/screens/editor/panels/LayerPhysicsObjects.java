package lintfordpickle.harvest.screens.editor.panels;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.controllers.editor.EditorPhysicsController;
import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.editor.physics.EditorPhysicsObjectInstance;
import lintfordpickle.harvest.renderers.editor.EditorPhysicsRenderer;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.editor.panels.UiPanel;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
import net.lintfordlib.renderers.windows.components.UiButtonToggle;
import net.lintfordlib.renderers.windows.components.UiLabelledInt;

public class LayerPhysicsObjects extends UiPanel {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int BUTTON_PLACE_FLOOR = 10;
	private static final int BUTTON_DELETE_SELECTED = 11;
	private static final int BUTTON_DELETE_ALL = 12;
	private static final int BUTTON_TOGGLE_SNAP = 13;
	private static final int BUTTON_TOGGLE_STATIC = 14;
	private static final int BUTTON_TOGGLE_DRAW_UIDS = 20;
	private static final int BUTTON_SET_OBJECT_CENTER_TO_CURSOR = 16;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiButton mAddNewPolygon;
	private UiButton mDeletePolygon;
	private UiButton mSetFloorCenterToCursor;
	private UiButtonToggle mToggleStatic;
	private UiLabelledInt mNumFloorRegions;

	private EditorPhysicsController mEditorPhysicsController;
	private EditorPhysicsRenderer mEditorPhysicsRenderer;

	private EditorPhysicsObjectInstance mSelectedObjectInstance;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return mEditorPhysicsRenderer.hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LayerPhysicsObjects(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "Physics Scene", entityGroupUid);

		mShowActiveLayerButton = true;
		mShowShowLayerButton = true;

		mEditorActiveLayerUid = EditorLayersData.Physics;

		mRenderPanelTitle = true;
		mPanelTitle = "Physics Scene";

		mNumFloorRegions = new UiLabelledInt();
		mNumFloorRegions.labelText("Number Objects: ");

		mAddNewPolygon = new UiButton();
		mAddNewPolygon.setUiWidgetListener(this, BUTTON_PLACE_FLOOR);
		mAddNewPolygon.buttonLabel("Add Polygon");

		mDeletePolygon = new UiButton();
		mDeletePolygon.setUiWidgetListener(this, BUTTON_DELETE_SELECTED);
		mDeletePolygon.buttonLabel("Delete");

		mSetFloorCenterToCursor = new UiButton();
		mSetFloorCenterToCursor.setUiWidgetListener(this, BUTTON_SET_OBJECT_CENTER_TO_CURSOR);
		mSetFloorCenterToCursor.buttonLabel("Center");

		mToggleStatic = new UiButtonToggle();
		mToggleStatic.setUiWidgetListener(this, BUTTON_TOGGLE_STATIC);
		mToggleStatic.buttonLabel("Is Static");

		addWidget(mNumFloorRegions);

		addWidget(mAddNewPolygon);
		addWidget(mDeletePolygon);

		addWidget(mToggleStatic);
		addWidget(mSetFloorCenterToCursor);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();

		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, mEntityGroupUid);
		mEditorBrushController.showPosition(true);

		mEditorPhysicsController = (EditorPhysicsController) lControllerManager.getControllerByNameRequired(EditorPhysicsController.CONTROLLER_NAME, mEntityGroupUid);

		final var lRendererManager = mParentWindow.rendererManager();
		mEditorPhysicsRenderer = (EditorPhysicsRenderer) lRendererManager.getRenderer(EditorPhysicsRenderer.RENDERER_NAME);
	}

	@Override
	public boolean handleInput(LintfordCore core) {

		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_DELETE, this)) {
			final var lSelectedPhysicsObject = mEditorPhysicsController.selectedPhysicsObject();

			if (lSelectedPhysicsObject != null) {
				mEditorPhysicsController.deletePhysicsObjectInstance(lSelectedPhysicsObject);
				mEditorPhysicsRenderer.clearSelectedRegion();
			}
		}

		return super.handleInput(core);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (isOpen() == false)
			return;

		if (mSelectedObjectInstance != mEditorPhysicsController.selectedPhysicsObject()) {
			mSelectedObjectInstance = mEditorPhysicsController.selectedPhysicsObject();

			if (mSelectedObjectInstance != null) {
				mToggleStatic.isEnabled(true);
			} else {
				mToggleStatic.isToggledOn(false);
				mToggleStatic.isEnabled(false);
			}
		}

		if (mSelectedObjectInstance != null) {
			mToggleStatic.isToggledOn(mSelectedObjectInstance.body_isStatic);

		}

		mNumFloorRegions.value(mEditorPhysicsController.physicsObjectsManager().physicsObjects().size());

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		final var lIsLayerActive = mEditorBrushController.isLayerActive(mEditorActiveLayerUid);
		final var lPhysicsObjectsHashCode = mEditorPhysicsRenderer.hashCode();

		switch (entryUid) {
		case BUTTON_PLACE_FLOOR: {
			if (lIsLayerActive && mEditorBrushController.setAction(EditorPhysicsController.ACTION_OBJECT_CREATE, "Creating Physics Object", lPhysicsObjectsHashCode)) {
				mEditorPhysicsRenderer.startPhysicsObjectCreation();

			}
			return;
		}

		case BUTTON_DELETE_SELECTED: {

			if (!lIsLayerActive)
				return;

			final var lSelectedPhysicsObject = mEditorPhysicsController.selectedPhysicsObject();
			mEditorPhysicsController.deletePhysicsObjectInstance(lSelectedPhysicsObject);
			mEditorPhysicsRenderer.clearSelectedRegion();
			return;
		}

		case BUTTON_DELETE_ALL:

			if (!lIsLayerActive)
				return;

			mEditorPhysicsController.deleteAllPolygons();
			return;

		case BUTTON_SET_OBJECT_CENTER_TO_CURSOR:

			if (!lIsLayerActive)
				return;

			mEditorPhysicsController.setSelectedObjectCenterToCursor();
			return;

		case BUTTON_SHOW_LAYER:
			mEditorPhysicsRenderer.renderPhysicsObjects(isLayerVisible());
			return;

		case BUTTON_TOGGLE_STATIC:
			if (lIsLayerActive && mSelectedObjectInstance != null) {
				mSelectedObjectInstance.body_isStatic = mToggleStatic.isToggledOn();
			}

			return;

		}

	}

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {

	}

}