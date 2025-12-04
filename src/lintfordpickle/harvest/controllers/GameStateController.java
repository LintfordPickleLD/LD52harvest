package lintfordpickle.harvest.controllers;

import lintfordpickle.harvest.data.game.GameState;
import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.data.scene.SceneData;
import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;

public abstract class GameStateController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Game State Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected PlayerManager mPlayerManager;
	protected GameState mGameState;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public GameState gameState() {
		return mGameState;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameStateController(ControllerManager controllerManager, SceneData sceneData, PlayerManager playerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mGameState = sceneData.gameState();
		mPlayerManager = playerManager;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lPlayerSessions = mPlayerManager.playerSessions();
		final var lNumPlayerSessions = lPlayerSessions.size();
		for (int i = 0; i < lNumPlayerSessions; i++) {
			final var lPlayerSession = lPlayerSessions.get(i);

			mGameState.addPlayerScoreCard(lPlayerSession.playerUid());
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public abstract boolean hasPlayerLostThroughTime();

	public boolean isPlayerDead(int playerUid) {
		final var lPlayerScoreCard = mGameState.getScoreCard(playerUid);
		return lPlayerScoreCard.isPlayerDead;
	}

	public void setPlayerDied(int playerUid) {
		final var lPlayerScoreCard = mGameState.getScoreCard(playerUid);
		lPlayerScoreCard.isPlayerDead = true;
	}

}