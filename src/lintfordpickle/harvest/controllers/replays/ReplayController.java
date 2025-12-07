package lintfordpickle.harvest.controllers.replays;

import lintfordpickle.harvest.data.players.ReplayManager;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;

public class ReplayController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Replay Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ReplayManager mReplayState;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ReplayManager replayManager() {
		return mReplayState;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ReplayController(ControllerManager controllerManager, ReplayManager replayState, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mReplayState = replayState;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(LintfordCore core) {
		super.update(core);

	}

}