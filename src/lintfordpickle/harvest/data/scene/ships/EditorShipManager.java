package lintfordpickle.harvest.data.scene.ships;

import java.util.ArrayList;
import java.util.List;

import lintfordpickle.harvest.data.editor.BaseEditorInstanceManager;
import lintfordpickle.harvest.data.editor.EditorSceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;

public class EditorShipManager extends BaseEditorInstanceManager {

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

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorShipManager() {

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
	public void finalizeAfterLoading(EditorSceneData sceneData) {

	}

}
