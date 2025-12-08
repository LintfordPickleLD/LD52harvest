package lintfordpickle.harvest.data.scene.cargo;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.scene.BaseInstanceManager;
import lintfordpickle.harvest.data.scene.SceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;

public class CargoManager extends BaseInstanceManager {

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	private final List<Cargo> mCargo = new ArrayList<>();

	public List<Cargo> cargo() {
		return mCargo;
	}

	public Cargo getCargoByEntityUid(int cargoUid) {
		final var lNumCargo = mCargo.size();
		for (var i = 0; i < lNumCargo; i++) {
			if (mCargo.get(i).cargoUid == cargoUid)
				return mCargo.get(i);
		}

		return null;
	}

	@Override
	public void initializeInstanceCounter() {
		mInstanceUidCounter = 0;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CargoManager() {
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void initializeManager() {

	}

	@Override
	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {

	}

	@Override
	public void finalizeAfterLoading(SceneData sceneData) {

	}

}