package lintfordpickle.harvest.controllers;

import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.data.scene.SceneData;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;

public class TimeTrialGameStateController extends GameStateController {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SceneController mSceneController;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TimeTrialGameStateController(ControllerManager controllerManager, SceneData sceneData, PlayerManager playerManager, int entityGroupUid) {
		super(controllerManager, sceneData, playerManager, entityGroupUid);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		mSceneController = (SceneController) lControllerManager.getControllerByNameRequired(SceneController.CONTROLLER_NAME, mEntityGroupUid);
		mGameState = mSceneController.sceneData().gameState();
		
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lDelta = (float) core.gameTime().elapsedTimeMilli();

		if (mGameState.isGameRunning) {
			mGameState.gameTimer += lDelta;
			mGameState.timeAliveInMs += lDelta;

		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public boolean hasPlayerLostThroughTime() {
		return false;
	}

}