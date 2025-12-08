package lintfordpickle.harvest.data.scene.physics;

import lintfordpickle.harvest.data.scene.BaseInstanceManager;
import lintfordpickle.harvest.data.scene.SceneData;
import lintfordpickle.harvest.data.scene.SceneSaveDefinition;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.physics.PhysicsSettings;

public class PhysicsSettingsManager extends BaseInstanceManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MINIMUM_PHYSICS_GRID_WIDTH_PX = 500;
	public static final int MAXIMUM_PHYSICS_GRID_WIDTH_PX = 100000;

	public static final int MINIMUM_PHYSICS_GRID_HEIGHT_PX = 500;
	public static final int MAXIMUM_PHYSICS_GRID_HEIGHT_PX = 100000;

	public static final int MINIMUM_CELLS_DIMENSIONS = 5; // on either x or y
	public static final int MAXIMUM_CELLS_DIMENSIONS = 20; // on either x or y

	// --------------------------------------
	// Variables
	// --------------------------------------

	private PhysicsSettings mPhysicsSettings;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public PhysicsSettings physicsSettings() {
		return mPhysicsSettings;
	}

	@Override
	public void initializeInstanceCounter() {
		// ignored
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PhysicsSettingsManager() {
		mPhysicsSettings = new PhysicsSettings();

		mPhysicsSettings.hashGridWidthInUnits = (int) ConstantsPhysics.toUnits(1024);
		mPhysicsSettings.hashGridHeightInUnits = (int) ConstantsPhysics.toUnits(1024);
		mPhysicsSettings.hashGridCellsWide = 5;
		mPhysicsSettings.hashGridCellsHigh = 5;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initializeManager() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void storeInTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		final var lSceneSettingsSaveDefinition = sceneSaveDefinition.physicsSettings();
		lSceneSettingsSaveDefinition.gravity.set(mPhysicsSettings.gravityX, mPhysicsSettings.gravityY);
		lSceneSettingsSaveDefinition.hashGridWidthInUnits = mPhysicsSettings.hashGridWidthInUnits;
		lSceneSettingsSaveDefinition.hashGridHeightInUnits = mPhysicsSettings.hashGridHeightInUnits;
		lSceneSettingsSaveDefinition.hashGridCellsWide = mPhysicsSettings.hashGridCellsWide;
		lSceneSettingsSaveDefinition.hashGridCellsHigh = mPhysicsSettings.hashGridCellsHigh;
	}

	@Override
	public void loadFromTrackSaveDefinition(SceneSaveDefinition sceneSaveDefinition) {
		final var lSceneSettingsSaveDefinition = sceneSaveDefinition.physicsSettings();
		mPhysicsSettings.gravityX = lSceneSettingsSaveDefinition.gravity.x;
		mPhysicsSettings.gravityY = lSceneSettingsSaveDefinition.gravity.y;

		mPhysicsSettings.hashGridWidthInUnits = lSceneSettingsSaveDefinition.hashGridWidthInUnits;
		mPhysicsSettings.hashGridHeightInUnits = lSceneSettingsSaveDefinition.hashGridHeightInUnits;
		mPhysicsSettings.hashGridCellsWide = lSceneSettingsSaveDefinition.hashGridCellsWide;
		mPhysicsSettings.hashGridCellsHigh = lSceneSettingsSaveDefinition.hashGridCellsHigh;

		// Check defaults are valid
		if (mPhysicsSettings.hashGridWidthInUnits < ConstantsPhysics.toUnits(MINIMUM_PHYSICS_GRID_WIDTH_PX)) {
			mPhysicsSettings.hashGridWidthInUnits = (int) ConstantsPhysics.toUnits(MINIMUM_PHYSICS_GRID_WIDTH_PX);
		}

		if (mPhysicsSettings.hashGridWidthInUnits > ConstantsPhysics.toUnits(MAXIMUM_PHYSICS_GRID_WIDTH_PX)) {
			mPhysicsSettings.hashGridWidthInUnits = (int) ConstantsPhysics.toUnits(MAXIMUM_PHYSICS_GRID_WIDTH_PX);
		}

		if (mPhysicsSettings.hashGridHeightInUnits < ConstantsPhysics.toUnits(MINIMUM_PHYSICS_GRID_HEIGHT_PX)) {
			mPhysicsSettings.hashGridHeightInUnits = (int) ConstantsPhysics.toUnits(MINIMUM_PHYSICS_GRID_HEIGHT_PX);
		}

		if (mPhysicsSettings.hashGridHeightInUnits > ConstantsPhysics.toUnits(MAXIMUM_PHYSICS_GRID_HEIGHT_PX)) {
			mPhysicsSettings.hashGridHeightInUnits = (int) ConstantsPhysics.toUnits(MAXIMUM_PHYSICS_GRID_HEIGHT_PX);
		}

		mPhysicsSettings.hashGridCellsWide = MathHelper.clampi(mPhysicsSettings.hashGridCellsWide, MINIMUM_CELLS_DIMENSIONS, MAXIMUM_CELLS_DIMENSIONS);
		mPhysicsSettings.hashGridCellsHigh = MathHelper.clampi(mPhysicsSettings.hashGridCellsHigh, MINIMUM_CELLS_DIMENSIONS, MAXIMUM_CELLS_DIMENSIONS);

	}

	@Override
	public void finalizeAfterLoading(SceneData sceneData) {

	}

}
