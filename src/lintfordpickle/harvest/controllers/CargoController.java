package lintfordpickle.harvest.controllers;

import lintfordpickle.harvest.data.scene.cargo.Cargo;
import lintfordpickle.harvest.data.scene.cargo.CargoManager;
import lintfordpickle.harvest.data.scene.cargo.CargoType;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;

public class CargoController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Cargo Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SceneController mSceneController;
	private CargoManager mCargoManager;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public CargoManager cargoManager() {
		return mCargoManager;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CargoController(ControllerManager controllerManager, int entityGroupID) {
		super(controllerManager, CONTROLLER_NAME, entityGroupID);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		mSceneController = (SceneController) lControllerManager.getControllerByNameRequired(SceneController.CONTROLLER_NAME, entityGroupUid());
		mCargoManager = mSceneController.sceneData().cargoManager();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public Cargo createNewCargo(int parentPlatformUid, CargoType cargoType) {
		final var lNewCargo = new Cargo(mCargoManager.getNewInstanceUid(), parentPlatformUid, cargoType);
		mCargoManager.cargo().add(lNewCargo);
		return lNewCargo;
	}

	public void removeCargo(int cargoUid) {
		final var lCargo = mCargoManager.getCargoByEntityUid(cargoUid);
		if (lCargo != null) {

		}
	}

}
