package lintfordpickle.harvest.controllers.camera;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.controllers.SceneController;
import lintfordpickle.harvest.data.scene.ships.Ship;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.maths.Vector2f;

public class CameraShipChaseController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Camera Ship Chase Controller";

	private static final float CAMERA_MAN_MOVE_SPEED = 0.2f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mGameCamera;
	private Ship mTrackedEntity;
	private boolean mAllowManualControl;
	private boolean mIsTrackingPlayer;

	private Vector2f mVelocity;
	public Vector2f mPosition;

	public float mZoomFactor;
	public float mZoomVelocity;

	private SceneController mSceneController;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ICamera gameCamera() {
		return mGameCamera;
	}

	public boolean trackPlayer() {
		return mIsTrackingPlayer;
	}

	public void trackPlayer(boolean pNewValue) {
		mIsTrackingPlayer = pNewValue;
	}

	public boolean allowManualControl() {
		return mAllowManualControl;
	}

	public void allowManualControl(boolean pNewValue) {
		mAllowManualControl = pNewValue;
	}

	@Override
	public boolean isInitialized() {
		return mGameCamera != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraShipChaseController(ControllerManager controllerManager, ICamera camera, Ship trackedShip, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mVelocity = new Vector2f();
		mPosition = new Vector2f();

		mGameCamera = camera;

		if (trackedShip != null) {
			mTrackedEntity = trackedShip;
			final var lBody = trackedShip.body();
			mPosition.x = lBody.transform.p.x;
			mPosition.y = lBody.transform.p.y;
			mIsTrackingPlayer = true;
		}

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();

		mSceneController = (SceneController) lControllerManager.getControllerByNameRequired(SceneController.CONTROLLER_NAME, mEntityGroupUid);

	}

	public void setTrackedEntity(ICamera cameCamera, Ship trackedEntity) {
		mGameCamera = cameCamera;
		mTrackedEntity = trackedEntity;
	}

	@Override
	public void unloadController() {

	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (mGameCamera == null)
			return false;

		if (mAllowManualControl) {
			final float speed = CAMERA_MAN_MOVE_SPEED;

			// Just listener for clicks - couldn't be easier !!?!
			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_A)) {
				mVelocity.x -= speed;
				mIsTrackingPlayer = false;
			}

			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D)) {
				mVelocity.x += speed;
				mIsTrackingPlayer = false;
			}

			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_S)) {
				mVelocity.y += speed;
				mIsTrackingPlayer = false;
			}

			if (core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_W)) {
				mVelocity.y -= speed;
				mIsTrackingPlayer = false;
			}
		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore) {
		if (mGameCamera == null)
			return;

		if (mTrackedEntity != null) {
			// updateSpring(pCore);

			final var lSceneManager = mSceneController.sceneData();

			if (mTrackedEntity != null) {
				mPosition.x = mTrackedEntity.body().transform.p.x * ConstantsPhysics.UnitsToPixels();
				mPosition.y = mTrackedEntity.body().transform.p.y * ConstantsPhysics.UnitsToPixels();
			}

			final var lCamWidth = mGameCamera.getWidth();
			final var lCamHeight = mGameCamera.getHeight();
			final var lSceneSettings = lSceneManager.sceneSettingsManager();

			// ensure camera doesn't go beyond scene extents
			if (mPosition.x - lCamWidth * .5f < -lSceneSettings.sceneWidthInPx() * 0.5f)
				mPosition.x = -lSceneSettings.sceneWidthInPx() * 0.5f + lCamWidth * .5f;
			if (mPosition.y - lCamHeight * .5f < -lSceneSettings.sceneHeightInPx() * 0.5f)
				mPosition.y = -lSceneSettings.sceneHeightInPx() * 0.5f + lCamHeight * .5f;

			if (mPosition.x + lCamWidth * .5f > lSceneSettings.sceneWidthInPx() * 0.5f)
				mPosition.x = lSceneSettings.sceneWidthInPx() * 0.5f - lCamWidth * .5f;
			if (mPosition.y + lCamHeight * .5f > lSceneSettings.sceneHeightInPx() * 0.5f)
				mPosition.y = lSceneSettings.sceneHeightInPx() * 0.5f - lCamHeight * .5f;

			mGameCamera.setPosition(mPosition.x, mPosition.y);
			mGameCamera.setZoomFactor(1.75f);
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void zoomIn(float zoomFactor) {
		mGameCamera.setZoomFactor(zoomFactor);
	}
}
