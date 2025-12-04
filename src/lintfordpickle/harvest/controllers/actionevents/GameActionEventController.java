package lintfordpickle.harvest.controllers.actionevents;

import java.nio.ByteBuffer;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.GameActions;
import lintfordpickle.harvest.controllers.GameStateController;
import lintfordpickle.harvest.controllers.replays.ReplayController;
import lintfordpickle.harvest.data.actionevents.ActionEventFileHeader;
import lintfordpickle.harvest.data.actionevents.ActionFrame;
import lintfordpickle.harvest.data.actionevents.GameActionEventMap;
import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.data.players.ReplayManager;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.actionevents.ActionEventController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.actionevents.ActionEventManager.PlaybackMode;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.gamepad.Gamepad;
import net.lintfordlib.core.input.gamepad.IGamepadListener;
import net.lintfordlib.core.time.LogicialCounter;

public class GameActionEventController extends ActionEventController<ActionFrame> implements IGamepadListener {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int INPUT_MAX_BUFFER_SIZE_IN_BYTES = 5 * 1024 * 1024;

	// ---------------------------------------------

	public static final boolean IS_RECORD_MODE = true;
	public static final String mRecordFilename = "input_new.lms";

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	private GameStateController mGameStateController;
	private ReplayController mReplayController;

	private PlayerManager mPlayerManager;

	private boolean mFastestTimeOnExitReached;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean fastestTimeOnExitReached() {
		return mFastestTimeOnExitReached;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameActionEventController(ControllerManager controllerManager, PlayerManager playerManager, LogicialCounter frameCounter, int entityGroupUid) {
		super(controllerManager, frameCounter, entityGroupUid);

		mPlayerManager = playerManager;

	}

	// ---------------------------------------------

	@Override
	protected int getHeaderSizeInBytes() {
		return ActionEventFileHeader.HEADER_SIZE_IN_BYTES;
	}

	@Override
	protected int getInputSizeInBytes() {
		return INPUT_MAX_BUFFER_SIZE_IN_BYTES;
	}

	@Override
	protected ActionFrame createActionFrameInstance() {
		return new ActionFrame();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		mReplayController = (ReplayController) lControllerManager.getControllerByNameRequired(ReplayController.CONTROLLER_NAME, ConstantsGame.GAME_RESOURCE_GROUP_ID);
		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupUid());

		// input
		final var lGamepadManager = core.input().gamepads();
		final var lActiveGamepads = lGamepadManager.getActiveGamepads();
		int lActiveControllerIndex = 0;

		final int lNumPlayers = mPlayerManager.numActivePlayers();
		for (int i = 0; i < lNumPlayers; i++) {
			final var lPlayerSession = mPlayerManager.getPlayer(i);
			switch (lPlayerSession.mode()) {
			case Normal:
				lPlayerSession.actionEventUid(ActionEventController.DEFAULT_PLAYER_UID);
				break;
			case Playback:
				final int lActionEventUid = createActionPlayback(lPlayerSession.playerUid(), lPlayerSession.actionFilename());
				lPlayerSession.actionEventUid(lActionEventUid);

				break;
			case Record:
				lPlayerSession.actionEventUid(createActionRecorder(lPlayerSession.playerUid(), lPlayerSession.actionFilename()));
				break;
			}

			if (lPlayerSession.isPlayerControlled()) {
				actionEventPlayer(lPlayerSession.actionEventUid()).isPlayerControlled = true;

				// This is where we actually assign the gamepads to the players
				// TODO: Gamepad to player assignment should probably happen in the menues (either options or player selection*)
				if (lActiveControllerIndex < lActiveGamepads.size()) {
					final var lGamepadController = lActiveGamepads.get(lActiveControllerIndex);
					if (lGamepadController != null) {
						actionEventPlayer(lPlayerSession.actionEventUid()).gamepadUid = lGamepadController.index();
					}
					lActiveControllerIndex++;
				}
			}
		}
	}

	protected void updateInputActionEvents(LintfordCore core, ActionEventPlayer player) {

		final var actionManager = core.input().actionManager();

		player.currentActionEvents.isThrottleDown = actionManager.getActionState(GameActions.THRUSTER_UP).isDown();
		player.currentActionEvents.isDownDown = actionManager.getActionState(GameActions.THRUSTER_DOWN).isDown();
		player.currentActionEvents.isThrottleLeftDown = actionManager.getActionState(GameActions.THRUSTER_LEFT).isDown();
		player.currentActionEvents.isThrottleRightDown = actionManager.getActionState(GameActions.THRUSTER_RIGHT).isDown();

		final var gamepadManager = core.input().gamepads();
		final var gamepad = gamepadManager.getGamepad(player.playerUid);

//		if (gamepad != null) {
//			final float lValue = gamepad.getLeftAxisX();
//			player.currentActionEvents.isThrottleLeftDown |= lValue < -0.4f;
//			player.currentActionEvents.isThrottleRightDown |= lValue > 0.4f;
//
//			player.currentActionEvents.isThrottleDown |= gamepad.getIsButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_A);
//		}

		// detect changes in keyboard / mouse / gamepad and set the flags
		// (n.b. we don't consider the mouse movement as input by default - but we record the mouse position when the player clicks a mouse button.)

		player.currentActionEvents.setChangeFlags(player.lastActionEvents);

	}

	@Override
	public void finalizeInputFile() {
		super.finalizeInputFile();

		mFastestTimeOnExitReached = false;

		final var lGameState = mGameStateController.gameState();
		final var lReplayManager = mReplayController.replayManager();

		final var isPreviousAvailable = lReplayManager.isRecordedGameAvailable();

		final var lMainPlayerUid = 0;
		final var lPlayerScoreCard = lGameState.getScoreCard(lMainPlayerUid);

		final var justGotMoreFood = lPlayerScoreCard.foodDelivered >= lReplayManager.header().numberdeliveredFood();
		final var justGotFasterTime = lGameState.timeAliveInMs <= lReplayManager.header().runtimeInSeconds();

		var shouldWeKeepThisRecording = !isPreviousAvailable || justGotMoreFood && justGotFasterTime;
		if (!shouldWeKeepThisRecording)
			return;

		mFastestTimeOnExitReached = true;

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Best time so far - replay will be recorded");

		final int numActionPlayers = actionEventPlayers().size();
		for (int i = 0; i < numActionPlayers; i++) {
			final var actionPlayer = actionEventPlayers().get(i);
			if (actionPlayer.isPlayerControlled) {
				final var actionManager = actionPlayer.actionEventManager;

				if (actionPlayer.actionEventManager.mode() == PlaybackMode.Record) {
					actionManager.filename(ReplayManager.RecordedGameFilename);
					actionManager.saveToFile();

					Debug.debugManager().logger().i(getClass().getSimpleName(), "  done saving replay");
				}

				return;
			}
		}
	}

	// RECORDING -----------------------------------

	@Override
	protected void saveHeaderToBuffer(ActionFrame currentFrame, ByteBuffer headerBuffer) {
		final var lGameState = mGameStateController.gameState();
		final var lHeaderDefinition = new ActionEventFileHeader();

		final var lMainPlayerUid = 0;
		final var lPlayerScoreCard = lGameState.getScoreCard(lMainPlayerUid);

		lHeaderDefinition.initialize((short) 0, "Just a test", lPlayerScoreCard.foodDelivered, lGameState.timeAliveInMs);

		lHeaderDefinition.saveHeaderToByteBuffer(headerBuffer);
	}

	@Override
	protected void saveActionEvents(ActionFrame frame, ByteBuffer dataBuffer) {
		var controlByte = (byte) 0;

		// control
		if (frame.markEndOfGame)
			controlByte |= GameActionEventMap.BYTEMASK_CONTROL_END_GAME;

		if (frame._isInputSaveNeeded)
			controlByte |= GameActionEventMap.BYTEMASK_CONTROL_INPUT;

		if (frame._isPhysicsStateSaveNeeded)
			controlByte |= GameActionEventMap.BYTEMASK_CONTROL_PHYSICS_STATE;

		mCurrentTick = frame.frameNumber;
		dataBuffer.putShort((short) frame.frameNumber);
		dataBuffer.put(controlByte);

		if (ConstantsGame.DEBUG_OUTPUT_ACTIONEVENT_LOGS) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Writing new input frame");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "  frame number: " + frame.frameNumber);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "       control: " + (byte) controlByte);
		}

		// keyboard
		if (frame._isInputSaveNeeded) {

			if (ConstantsGame.DEBUG_OUTPUT_ACTIONEVENT_LOGS)
				Debug.debugManager().logger().i(getClass().getSimpleName(), "  + input event");

			byte keyboardInputValue0 = 0;

			if (frame.isThrottleDown)
				keyboardInputValue0 |= GameActionEventMap.BYTEMASK_INPUT_UP;

			if (frame.isDownDown)
				keyboardInputValue0 |= GameActionEventMap.BYTEMASK_INPUT_DOWN;

			if (frame.isThrottleLeftDown)
				keyboardInputValue0 |= GameActionEventMap.BYTEMASK_INPUT_LEFT;

			if (frame.isThrottleRightDown)
				keyboardInputValue0 |= GameActionEventMap.BYTEMASK_INPUT_RIGHT;

			dataBuffer.put(keyboardInputValue0);
		}

		// gamepad
		// --->
	}

	@Override
	protected void saveCustomActionEvents(ActionFrame frame, ByteBuffer dataBuffer) {
		var controlByte = (byte) GameActionEventMap.BYTEMASK_CONTROL_PHYSICS_STATE;

		if (ConstantsGame.DEBUG_OUTPUT_ACTIONEVENT_LOGS) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Writing new custom frame");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "  frame number: " + frame.frameNumber);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "       control: " + (byte) controlByte);
		}

		dataBuffer.putShort((short) frame.frameNumber);
		dataBuffer.put(controlByte);

		dataBuffer.putFloat(frame.positionX);
		dataBuffer.putFloat(frame.positionY);

		dataBuffer.putFloat(frame.velocityX);
		dataBuffer.putFloat(frame.velocityY);

		dataBuffer.putFloat(frame.angle);
		dataBuffer.putFloat(frame.angularV);

	}

	@Override
	protected void saveEndOfFile(ActionFrame frame, ByteBuffer dataBuffer) {
		if (ConstantsGame.DEBUG_OUTPUT_ACTIONEVENT_LOGS)
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Marking end of recording");

		frame.markEndOfGame = true;
		saveActionEvents(frame, dataBuffer);
	}

	// PLAYBACK -----------------------------------

	@Override
	protected void readHeaderBuffer(ByteBuffer headerBuffer) {
		final var lLmpVersion = headerBuffer.getShort(); // consume version
		mTotalTicks = headerBuffer.getInt();

		if (ConstantsGame.DEBUG_OUTPUT_ACTIONEVENT_LOGS) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Reading Header buffer");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "  lmp version: " + lLmpVersion);
			Debug.debugManager().logger().i(getClass().getSimpleName(), " total frames: " + mTotalTicks);
		}

	}

	@Override
	protected void readNextFrame(ByteBuffer dataBuffer, ActionEventPlayer player) {
		// Read the next player input from the 'file' into temp
		final var lRemaining = dataBuffer.limit() - dataBuffer.position();
		if (lRemaining < 2) { // only check for the exisitence of the tick number

			if (ConstantsGame.DEBUG_OUTPUT_ACTIONEVENT_LOGS)
				Debug.debugManager().logger().i(getClass().getSimpleName(), "End of input buffer reached - but no marker! Incorrectly saved?");

			return;
		}

		final short nextFrameNumber = dataBuffer.getShort();
		final var nextControlByte = dataBuffer.get();

		if (ConstantsGame.DEBUG_OUTPUT_ACTIONEVENT_LOGS) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "next frame input on: " + nextFrameNumber);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "            control: " + nextControlByte);
		}

		if ((nextControlByte & GameActionEventMap.BYTEMASK_CONTROL_INPUT) == GameActionEventMap.BYTEMASK_CONTROL_INPUT) {
			final var nextInput = dataBuffer.get();

			player.tempFrameInput.isThrottleDown = (nextInput & GameActionEventMap.BYTEMASK_INPUT_UP) == GameActionEventMap.BYTEMASK_INPUT_UP;
			player.tempFrameInput.isThrottleLeftDown = (nextInput & GameActionEventMap.BYTEMASK_INPUT_LEFT) == GameActionEventMap.BYTEMASK_INPUT_LEFT;
			player.tempFrameInput.isThrottleRightDown = (nextInput & GameActionEventMap.BYTEMASK_INPUT_RIGHT) == GameActionEventMap.BYTEMASK_INPUT_RIGHT;
		}

		if ((nextControlByte & GameActionEventMap.BYTEMASK_CONTROL_PHYSICS_STATE) == GameActionEventMap.BYTEMASK_CONTROL_PHYSICS_STATE) {
			player.tempFrameInput.positionX = dataBuffer.getFloat();
			player.tempFrameInput.positionY = dataBuffer.getFloat();

			player.tempFrameInput.velocityX = dataBuffer.getFloat();
			player.tempFrameInput.velocityY = dataBuffer.getFloat();

			player.tempFrameInput.angle = dataBuffer.getFloat();
			player.tempFrameInput.angularV = dataBuffer.getFloat();
		}

		player.tempFrameInput.frameNumber = nextFrameNumber;
	}

	// GAMEPAD CALLBACKS --------------------------

	// TODO: don't think these are needed anymore

	@Override
	public void onGamepadConnected(Gamepad gamepad) {
		final var lPlayer = mPlayerManager.getPlayer(PlayerManager.DEFAULT_PLAYER_SESSION_UID);
		final var lActionEventPlayer = actionEventPlayer(lPlayer.playerUid());

		if (lActionEventPlayer.gamepadUid == -1) {
			lActionEventPlayer.gamepadUid = gamepad.index();
		}
	}

	@Override
	public void onGamepadDisconnected(Gamepad gamepad) {
		final var lPlayer = mPlayerManager.getPlayer(PlayerManager.DEFAULT_PLAYER_SESSION_UID);
		final var lActionEventPlayer = actionEventPlayer(lPlayer.playerUid());

		if (lActionEventPlayer.gamepadUid == gamepad.index()) {
			lActionEventPlayer.gamepadUid = -1;
		}

	}
}
