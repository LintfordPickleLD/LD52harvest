package lintfordpickle.harvest.data.editor.physics;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.editor.BaseEditorInstanceManager;
import lintfordpickle.harvest.data.editor.EditorSceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import net.lintfordlib.assets.ResourceGroupProvider;
import net.lintfordlib.core.maths.Vector2f;

public class EditorPhysicsObjectsManager extends BaseEditorInstanceManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<EditorPhysicsObjectInstance> mPhysicsObjects = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<EditorPhysicsObjectInstance> physicsObjects() {
		return mPhysicsObjects;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorPhysicsObjectsManager() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addPhyiscsObjectInstance(EditorPhysicsObjectInstance physicsObjectInstance) {
		if (mPhysicsObjects.contains(physicsObjectInstance))
			return;

		mPhysicsObjects.add(physicsObjectInstance);
	}

	public void removePhysicsObjectInstance(EditorPhysicsObjectInstance physicsObjectInstance) {
		if (!mPhysicsObjects.contains(physicsObjectInstance))
			return;

		mPhysicsObjects.remove(physicsObjectInstance);
	}

	// ---------------------------------------------
	// Inherited-Methods
	// ---------------------------------------------

	@Override
	public void initializeManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneDefinition) {
		final var physicsObjectsToSave = sceneDefinition.physicsObjects().physicsObjects;
		physicsObjectsToSave.clear();

		final int numFloorDefinitions = mPhysicsObjects.size();
		for (int i = 0; i < numFloorDefinitions; i++) {
			final var lPhysicsObject = mPhysicsObjects.get(i);
			final var lPhysicsObjectToSave = new ScenePhysicsObjectSaveDefinition();

			lPhysicsObjectToSave.worldCenterX = lPhysicsObject.wcx;
			lPhysicsObjectToSave.worldCenterY = lPhysicsObject.wcy;
			lPhysicsObjectToSave.rotation = lPhysicsObject.angle;

			lPhysicsObjectToSave.localPoints.add(new Vector2f(lPhysicsObject.a.worldPosition.x - lPhysicsObject.wcx, lPhysicsObject.a.worldPosition.y - lPhysicsObject.wcy));
			lPhysicsObjectToSave.localPoints.add(new Vector2f(lPhysicsObject.b.worldPosition.x - lPhysicsObject.wcx, lPhysicsObject.b.worldPosition.y - lPhysicsObject.wcy));
			lPhysicsObjectToSave.localPoints.add(new Vector2f(lPhysicsObject.c.worldPosition.x - lPhysicsObject.wcx, lPhysicsObject.c.worldPosition.y - lPhysicsObject.wcy));
			lPhysicsObjectToSave.localPoints.add(new Vector2f(lPhysicsObject.d.worldPosition.x - lPhysicsObject.wcx, lPhysicsObject.d.worldPosition.y - lPhysicsObject.wcy));

			physicsObjectsToSave.add(lPhysicsObjectToSave);
		}

	}

	@Override
	public void loadFromTrackSaveDefinition(SceneSaveDefinition sceneDefinition) {
		final var floorDefinitions = sceneDefinition.physicsObjects().physicsObjects;
		final var numFloorDefinitions = floorDefinitions.size();
		for (int i = 0; i < numFloorDefinitions; i++) {
			final var lFloorSaveDefinition = floorDefinitions.get(i);
			final var lNewPhyiscsObjectInstance = new EditorPhysicsObjectInstance(ResourceGroupProvider.getRollingEntityNumber());

			// @formatter:off
			lNewPhyiscsObjectInstance.initialize(
					lFloorSaveDefinition.worldCenterX, 
					lFloorSaveDefinition.worldCenterY, 
					lFloorSaveDefinition.rotation);
			
			final var wcx = lFloorSaveDefinition.worldCenterX;
			final var wcy = lFloorSaveDefinition.worldCenterY;
			
			lNewPhyiscsObjectInstance.setWorldVertices(
					lFloorSaveDefinition.localPoints.get(0).add(wcx, wcy), 
					lFloorSaveDefinition.localPoints.get(1).add(wcx, wcy), 
					lFloorSaveDefinition.localPoints.get(2).add(wcx, wcy), 
					lFloorSaveDefinition.localPoints.get(3).add(wcx, wcy));

			mPhysicsObjects.add(lNewPhyiscsObjectInstance);
		}

	}

	@Override
	public void finalizeAfterLoading(EditorSceneData sceneData) {
		final var hashGrid = sceneData.hashGridManager().hashGrid();

		final int lNumPhysicsObjectInstances = mPhysicsObjects.size();
		for (int i = 0; i < lNumPhysicsObjectInstances; i++) {
			final var lPhysicsObject = mPhysicsObjects.get(i);

			hashGrid.addEntity(lPhysicsObject);
		}
	}

}
