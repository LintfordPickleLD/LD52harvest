package lintfordpickle.harvest.controllers;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.core.particles.ParticleFrameworkController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class EnvironmentController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Environment Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ICamera mCamera;
	private ParticleSystemInstance mWispParticles;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EnvironmentController(ControllerManager controllerManager, ICamera camera, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mCamera = camera;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		final var lParticleController = (ParticleFrameworkController) lControllerManager.getControllerByNameRequired(ParticleFrameworkController.CONTROLLER_NAME, entityGroupUid());
		mWispParticles = lParticleController.particleFrameworkData().particleSystemManager().createNewParticleSystemFromDefinitionName("PARTICLESYSTEM_WISP");

	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (mWispParticles != null) {
			if (RandomNumbers.getRandomChance(40.f)) {
				final var lMarginX = 600.f;
				final var lMarginY = 600.f;
				final var lMinX = mCamera.getMinX() - lMarginX;
				final var lMaxX = mCamera.getMaxX() + lMarginX;

				final var lMinY = mCamera.getMinY() - lMarginY;
				final var lMaxY = mCamera.getMaxY() + lMarginY;

				final var lGenX = RandomNumbers.random(lMinX, lMaxX);
				final var lGenY = RandomNumbers.random(lMinY, lMaxY);

				mWispParticles.spawnParticle(lGenX, lGenY, -0.2f, 0, 0);

			}
		}
	}
}
