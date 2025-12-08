package lintfordpickle.harvest.data.scene.physics;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.scene.BaseInstanceManager;
import lintfordpickle.harvest.data.scene.SceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import net.lintfordlib.ConstantsPhysics;

public class PhysicsObjectsManager extends BaseInstanceManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<ScenePhysicsObjectInstance> mWorldSceneBodies = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<ScenePhysicsObjectInstance> worldSceneBodies() {
		return mWorldSceneBodies;
	}

	@Override
	public void initializeInstanceCounter() {

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PhysicsObjectsManager() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initializeManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		// physics objects not generated in normal game mode - hence no saving
	}

	@Override
	public void loadFromTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		final var lFloorDefinitions = sceneSaveDefinition.physicsObjects().physicsObjects;
		final var lNumFloorDefinitions = lFloorDefinitions.size();

		final var lToUnits = ConstantsPhysics.PixelsToUnits();

		for (int i = 0; i < lNumFloorDefinitions; i++) {
			final var lFloorSaveDefinition = lFloorDefinitions.get(i);
			final var lNewPhyiscsObjectInstance = new ScenePhysicsObjectInstance(getNewInstanceUid());

			// @formatter:off
			lNewPhyiscsObjectInstance.initialize(
					lFloorSaveDefinition.worldCenterX, 
					lFloorSaveDefinition.worldCenterY, 
					lFloorSaveDefinition.rotation);
						
			lNewPhyiscsObjectInstance.setPolygonVertices(
					lFloorSaveDefinition.localPoints.get(0).mul(lToUnits), 
					lFloorSaveDefinition.localPoints.get(1).mul(lToUnits), 
					lFloorSaveDefinition.localPoints.get(2).mul(lToUnits), 
					lFloorSaveDefinition.localPoints.get(3).mul(lToUnits));

			mWorldSceneBodies.add(lNewPhyiscsObjectInstance);
		}

	}

	@Override
	public void finalizeAfterLoading(SceneData sceneData) {
		// finalized in SceneController
	}
}
