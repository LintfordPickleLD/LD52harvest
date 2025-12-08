package lintfordpickle.harvest.data.scene.ships;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.scene.BaseInstanceManager;
import lintfordpickle.harvest.data.scene.SceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;

public class ShipManager extends BaseInstanceManager {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private final List<Ship> mShips = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public List<Ship> ships() {
		return mShips;
	}

	public Ship playerShip() {
		return mShips.get(0);
	}

	@Override
	public void initializeInstanceCounter() {
		// TODO Auto-generated method stub
		
	}
	
	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ShipManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void initializeManager() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFromTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalizeAfterLoading(SceneData sceenData) {

	}

}
