package lintfordpickle.harvest.controllers;

import lintfordpickle.harvest.data.scene.platforms.PlatformInstance;
import lintfordpickle.harvest.data.scene.platforms.PlatformManager;
import lintfordpickle.harvest.data.scene.platforms.PlatformType;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.physics.PhysicsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.physics.PhysicsWorld;

public class LevelController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Level Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private boolean mLevelCreated;
	private PhysicsWorld mPhysicsWorld;
	private PlatformManager mPlatformManager;

	private PhysicsController mPhysicsController;
	private PlatformController mPlatformsController;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isLevelCreated() {
		return mLevelCreated;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public LevelController(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mLevelCreated = false;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();

		mPhysicsController = (PhysicsController) lControllerManager.getControllerByNameRequired(PhysicsController.CONTROLLER_NAME, mEntityGroupUid);
		mPlatformsController = (PlatformController) lControllerManager.getControllerByNameRequired(PlatformController.CONTROLLER_NAME, mEntityGroupUid);

		mPlatformManager = mPlatformsController.platformManager();
		mPhysicsWorld = mPhysicsController.world();

		mLevelCreated = true;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void createPlatform(int platformUid, float x, float y, float w, float h, PlatformType type) {
		final float worldXOffset = -1024;
		final float worldYOffset = -1024;

		final var lNewPlatform = new PlatformInstance(platformUid);
		lNewPlatform.set(worldXOffset + x, worldYOffset + y, w, h);
		lNewPlatform.platformType = type;

		mPlatformManager.addPlatform(lNewPlatform);

		switch (type) {
		case Farm:
			lNewPlatform.stockValueI = 0;
			lNewPlatform.stockValueF = 0.f;
			break;
		case Water:
			lNewPlatform.stockValueF = 1.f;
			break;
		default: // warehouse
			break;
		}
	}

}
