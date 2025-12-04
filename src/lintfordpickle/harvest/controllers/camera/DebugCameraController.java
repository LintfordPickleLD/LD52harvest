package lintfordpickle.harvest.controllers.camera;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.data.scene.ships.Ship;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.maths.Vector2f;

public class DebugCameraController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Debug Camera Controller";

	private static final float CAMERA_MAN_MOVE_SPEED = 0.01f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mGameCamera;

	private Vector2f mVelocity;
	private Vector2f mPosition;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ICamera gameCamera() {
		return mGameCamera;
	}

	@Override
	public boolean isInitialized() {
		return mGameCamera != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public DebugCameraController(ControllerManager controllerManager, ICamera camera, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mVelocity = new Vector2f();
		mPosition = new Vector2f();

		mGameCamera = camera;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {

	}

	public void setTrackedEntity(ICamera cameCamera, Ship trackedEntity) {
		mGameCamera = cameCamera;
	}

	@Override
	public void unloadController() {

	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (mGameCamera == null)
			return false;

		final float speed = CAMERA_MAN_MOVE_SPEED;

		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_A)) {
			mVelocity.x -= speed;
		}

		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D)) {
			mVelocity.x += speed;
		}

		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_S)) {
			mVelocity.y += speed;
		}

		if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_W)) {
			mVelocity.y -= speed;
		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore) {
		if (mGameCamera == null)
			return;

		final var lDelta = (float) pCore.gameTime().elapsedTimeMilli();

		mPosition.x += mVelocity.x * lDelta;
		mPosition.y += mVelocity.y * lDelta;

		mVelocity.x *= .97f;
		mVelocity.y *= .97f;

		mGameCamera.setPosition(mPosition.x, mPosition.y);
	}
}
