package lintfordpickle.harvest.controllers.editor;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.editor.physics.EditorPhysicsObjectInstance;
import lintfordpickle.harvest.data.editor.physics.EditorPhysicsObjectsManager;
import lintfordpickle.harvest.data.editor.physics.EditorPolygonPoint;
import lintfordpickle.harvest.data.scene.collisions.GridEntityType;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.controllers.editor.EditorHashGridController;
import net.lintfordlib.controllers.editor.IGridControllerCallback;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.partitioning.GridEntity;
import net.lintfordlib.core.geometry.partitioning.SpatialHashGrid;

public class EditorPhysicsController extends BaseController implements IGridControllerCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Editor Physics Controller";

	public static final int ACTION_OBJECT_CREATE = 0;
	public static final int ACTION_OBJECT_TRANSLATE_SELECTED_REGION = 1;
	public static final int ACTION_OBJECT_TRANSLATE_SELECTED_POINTS = 3;
	public static final int ACTION_OBJECT_ROTATE_REGION_SELECTED = 2;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EditorSceneController mEditorSceneController;
	private EditorHashGridController mEditorHashGridController;
	private EditorPhysicsObjectInstance mSelectedRegion;
	private EditorBrushController mEditorBrushController;
	private EditorPhysicsObjectsManager mPhysicsObjectsManager;

	private final List<EditorPolygonPoint> mSelectedPoints = new ArrayList<>();
	private final List<EditorPhysicsObjectInstance> mDirtyRegions = new ArrayList<>();
	private EditorPolygonPoint mSelectedPointByCreation;
	private EditorPolygonPoint mHoverPoint;
	private List<GridEntity> mPolygonsEntitiesNearby;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public EditorPhysicsObjectsManager physicsObjectsManager() {
		return mPhysicsObjectsManager;
	}

	public List<GridEntity> phyiscsObjectEntitiesNearby() {
		return mPolygonsEntitiesNearby;
	}

	public List<EditorPhysicsObjectInstance> dirtyRegions() {
		return mDirtyRegions;
	}

	public void addDirtyRegion(EditorPhysicsObjectInstance region) {
		if (mDirtyRegions.contains(region) == false)
			mDirtyRegions.add(region);
	}

	public void selectedPointByCreation(EditorPolygonPoint newPoint) {
		mSelectedPointByCreation = newPoint;
	}

	public EditorPolygonPoint selectedPointByCreation() {
		return mSelectedPointByCreation;
	}

	public void hoverPoint(EditorPolygonPoint newPoint) {
		mHoverPoint = newPoint;
	}

	public EditorPolygonPoint hoverPoint() {
		return mHoverPoint;
	}

	public EditorPhysicsObjectInstance selectedPhysicsObject() {
		return mSelectedRegion;
	}

	public void selectedRegion(EditorPhysicsObjectInstance selectedRegion) {
		mSelectedRegion = selectedRegion;
	}

	public List<EditorPolygonPoint> selectedPoints() {
		return mSelectedPoints;
	}

	public void addSelectedPoint(EditorPolygonPoint point) {
		mSelectedPoints.add(point);
	}

	public void clearSelectedPoints() {
		mSelectedPoints.clear();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorPhysicsController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();

		mEditorSceneController = (EditorSceneController) lControllerManager.getControllerByNameRequired(EditorSceneController.CONTROLLER_NAME, mEntityGroupUid);
		mPhysicsObjectsManager = mEditorSceneController.sceneData().physicsObjectsManager();

		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, mEntityGroupUid);
		mEditorHashGridController = (EditorHashGridController) lControllerManager.getControllerByNameRequired(EditorHashGridController.CONTROLLER_NAME, mEntityGroupUid);
		mEditorHashGridController.addGridListener(this);

		mPolygonsEntitiesNearby = mEditorHashGridController.hashGrid().findNearbyEntities(0, 0, 0, GridEntityType.GRID_ENTITY_TYPE_PHYSICS_OBJECTS);
	}

	@Override
	public void unloadController() {
		mEditorHashGridController.removeGridListener(this);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void cleanDirtyRegions(LintfordCore core) {
		final int lNumDirtyRegions = mDirtyRegions.size();
		for (int i = 0; i < lNumDirtyRegions; i++) {
			final var lRegion = mDirtyRegions.get(i);
			if (lRegion == null)
				continue;

			mEditorHashGridController.hashGrid().updateEntity(lRegion);
		}

		mDirtyRegions.clear();
	}

	public void clearPhysicsObjectInstancesNearby() {
		mPolygonsEntitiesNearby.clear();
	}

	public void updatePhysicsObjectInstanceNearby(float mouseX, float mouseY) {
		mPolygonsEntitiesNearby = mEditorHashGridController.hashGrid().findNearbyEntities(mouseX, mouseY, 2.0f, GridEntityType.GRID_ENTITY_TYPE_PHYSICS_OBJECTS);
	}

	public void addPhysicsObjectInstance(EditorPhysicsObjectInstance physicsObjectInst) {
		if (physicsObjectInst == null)
			return;

		if (physicsObjectInst.isOnGrid())
			mEditorHashGridController.hashGrid().removeEntity(physicsObjectInst);

		mPhysicsObjectsManager.addPhyiscsObjectInstance(physicsObjectInst);
		mEditorHashGridController.hashGrid().addEntity(physicsObjectInst);
	}

	public void deletePhysicsObjectInstance(EditorPhysicsObjectInstance physicsObjectInst) {
		if (physicsObjectInst == null)
			return;

		mPhysicsObjectsManager.removePhysicsObjectInstance(physicsObjectInst);

		mEditorHashGridController.hashGrid().removeEntity(physicsObjectInst);
	}

	public void deleteAllPolygons() {
		mSelectedPoints.clear();
		mSelectedRegion = null;

		final var lAllPhysicsObjectInstances = mPhysicsObjectsManager.physicsObjects();
		final var lNumPhysicsObjectInstances = lAllPhysicsObjectInstances.size();
		for (int i = 0; i < lNumPhysicsObjectInstances; i++) {
			final var lRegion = lAllPhysicsObjectInstances.get(i);

			mEditorHashGridController.hashGrid().removeEntity(lRegion);
		}

		mPhysicsObjectsManager.physicsObjects().clear();
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void gridCreated(SpatialHashGrid<GridEntity> grid) {
		final var lPhysicsObjects = mPhysicsObjectsManager.physicsObjects();
		final var lNumObjects = lPhysicsObjects.size();
		for (int i = 0; i < lNumObjects; i++) {
			grid.addEntity(lPhysicsObjects.get(i));
		}
	}

	@Override
	public void gridDeleted(SpatialHashGrid<GridEntity> grid) {
		final var lPhysicsObjects = mPhysicsObjectsManager.physicsObjects();
		final var lNumObjects = lPhysicsObjects.size();
		for (int i = 0; i < lNumObjects; i++) {
			grid.removeEntity(lPhysicsObjects.get(i));
		}
	}

	public void setSelectedObjectCenterToCursor() {
		if (mSelectedRegion == null)
			return;

		mSelectedRegion.wcx = mEditorBrushController.cursorWorldX();
		mSelectedRegion.wcy = mEditorBrushController.cursorWorldY();

	}
}
