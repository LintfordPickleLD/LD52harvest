package lintfordpickle.harvest.controllers.actionevents;

import java.nio.ByteBuffer;

import lintfordpickle.harvest.GameActions;
import lintfordpickle.harvest.data.actionevents.SatActionEventMap;
import lintfordpickle.harvest.data.actionevents.SatActionFrame;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.actionevents.ActionEventController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.time.LogicialCounter;

// TODO: SatActionEventController is still a work in progress

public class SatActionEventController extends ActionEventController<SatActionFrame> {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int HEADER_VERSION_BYTE_SIZE = 2; // short
	private static final int HEADER_TICKCOUNT_BYTE_SIZE = 4; // int
	private static final int HEADER_SIZE_IN_BYTES = HEADER_VERSION_BYTE_SIZE + HEADER_TICKCOUNT_BYTE_SIZE;

	private static final int INPUT_MAX_BUFFER_SIZE_IN_BYTES = 5 * 1024 * 1024; // 5MB - I think this is ~8 hours at 3 bytes per tick?

	// ---------------------------------------------

	public static final boolean IS_RECORD_MODE = false;
	public static final String mRecordFilename = "input_new.lms";

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SatActionEventController(ControllerManager controllerManager, LogicialCounter frameCounter, int entityGroupUid) {
		super(controllerManager, frameCounter, entityGroupUid);
	}

	// ---------------------------------------------

	@Override
	protected int getHeaderSizeInBytes() {
		return HEADER_SIZE_IN_BYTES;
	}

	@Override
	protected int getInputSizeInBytes() {
		return INPUT_MAX_BUFFER_SIZE_IN_BYTES;
	}

	@Override
	protected SatActionFrame createActionFrameInstance() {
		return new SatActionFrame();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	protected void updateInputActionEvents(LintfordCore core, ActionEventPlayer player) {

		final var actionManager = core.input().actionManager();

		player.currentActionEvents.isSpaceDown = actionManager.getActionState(GameActions.THRUSTER_UP).isDown();
		player.currentActionEvents.isDownDown = actionManager.getActionState(GameActions.THRUSTER_DOWN).isDown();
		player.currentActionEvents.isLeftDown = actionManager.getActionState(GameActions.THRUSTER_LEFT).isDown();
		player.currentActionEvents.isRightDown = actionManager.getActionState(GameActions.THRUSTER_RIGHT).isDown();

		player.currentActionEvents.mouseX = core.gameCamera().getMouseWorldSpaceX();
		player.currentActionEvents.mouseY = core.gameCamera().getMouseWorldSpaceY();

		player.currentActionEvents.isLeftMouseDown = core.input().mouse().isMouseLeftButtonDown();
		player.currentActionEvents.isLeftMouseDownTimed = core.input().mouse().isMouseLeftButtonDownTimed(player);
		player.currentActionEvents.isRightMouseDown = core.input().mouse().isMouseRightButtonDown();

		// detect changes in keyboard / mouse / gamepad and set the flags
		// (n.b. we don't consider the mouse movement as input by default - but we record the mouse position when the player clicks a mouse button.)

		player.currentActionEvents.setChangeFlags(player.lastActionEvents);

	}

	// RECORDING -----------------------------------

	@Override
	protected void saveHeaderToBuffer(SatActionFrame currentFrame, ByteBuffer headerBuffer) {
		headerBuffer.putShort((short) 0);
		headerBuffer.putInt(mLogicialCounter.getCounter());
	}

	@Override
	protected void saveActionEvents(SatActionFrame frame, ByteBuffer dataBuffer) {
		var controlByte = (byte) 0;

		// FRAME NUMBER ---------
		mCurrentTick = frame.frameNumber;
		dataBuffer.putShort((short) frame.frameNumber);
		System.out.println("  frame number: " + frame.frameNumber);

		// CONTROL BYTE ---------

		if (frame.markEndOfGame) {
			controlByte |= SatActionEventMap.BYTEMASK_CONTROL_END_GAME;
			System.out.println("       control: END GAME");
		}

		if (frame._isKeyboardChanged) {
			controlByte |= SatActionEventMap.BYTEMASK_CONTROL_KEYBOARD;
			System.out.println("       control: KEYBOARD");
		}

		if (frame._isMouseChanged) {
			controlByte |= SatActionEventMap.BYTEMASK_CONTROL_MOUSE;
			System.out.println("       control: MOUSE");
		}

		if (frame._isGamepadChanged) {
			controlByte |= SatActionEventMap.BYTEMASK_CONTROL_GAMEPAD;
			System.out.println("       control: GAMEPAD");
		}

		dataBuffer.put(controlByte);

		// keyboard
		if (frame._isKeyboardChanged) {
			System.out.println("  + keyboard");
			byte keyboardInputValue0 = 0;

			if (frame.isLeftShiftDown) {
				keyboardInputValue0 |= SatActionEventMap.BYTEMASK_LEFT_SHIFT;
				System.out.println("       keyboard left shift");
			}

			if (frame.isSpaceDown) {
				keyboardInputValue0 |= SatActionEventMap.BYTEMASK_SPACE;
				System.out.println("       keyboard space");
			}

			if (frame.isRDown) {
				keyboardInputValue0 |= SatActionEventMap.BYTEMASK_R;
				System.out.println("       keyboard r");
			}

			if (frame.isUpDown) {
				keyboardInputValue0 |= SatActionEventMap.BYTEMASK_UP;
				System.out.println("       keyboard up");
			}

			if (frame.isDownDown) {
				keyboardInputValue0 |= SatActionEventMap.BYTEMASK_DOWN;
				System.out.println("       keyboard down");
			}

			if (frame.isLeftDown) {
				keyboardInputValue0 |= SatActionEventMap.BYTEMASK_LEFT;
				System.out.println("       keyboard left");
			}

			if (frame.isRightDown) {
				keyboardInputValue0 |= SatActionEventMap.BYTEMASK_RIGHT;
				System.out.println("       keyboard right");
			}

			dataBuffer.put(keyboardInputValue0);
		}

		// gamepad
		// --->

		// mouse
		if (frame._isMouseChanged) {
			System.out.println("   + mouse");
			byte mouseButtonValues = 0;

			if (frame.isLeftMouseDown) {
				mouseButtonValues |= SatActionEventMap.BYTEMASK_MOUSE_LEFT;
				System.out.println("       mouse left down");
			}

			if (frame.isMiddleMouseDown) {
				mouseButtonValues |= SatActionEventMap.BYTEMASK_MOUSE_MIDDLE;
				System.out.println("       mouse middle down");
			}

			if (frame.isRightMouseDown) {
				mouseButtonValues |= SatActionEventMap.BYTEMASK_MOUSE_RIGHT;
				System.out.println("       mouse right down");
			}

			if (frame.isLeftMouseDownTimed) {
				mouseButtonValues |= SatActionEventMap.BYTEMASK_MOUSE_LEFT_TIMED;
				System.out.println("       mouse left timed down");
			}

			if (frame.isRightMouseDownTimed) {
				mouseButtonValues |= SatActionEventMap.BYTEMASK_MOUSE_RIGHT_TIMED;
				System.out.println("       mouse right timed down");
			}

			dataBuffer.put(mouseButtonValues);
			dataBuffer.putFloat(frame.mouseX);
			dataBuffer.putFloat(frame.mouseY);
		}
	}

	@Override
	protected void saveCustomActionEvents(SatActionFrame frame, ByteBuffer dataBuffer) {

	}

	@Override
	protected void saveEndOfFile(SatActionFrame frame, ByteBuffer dataBuffer) {
		frame.markEndOfGame = true;
		saveActionEvents(frame, dataBuffer);
	}

	// PLAYBACK -----------------------------------

	@Override
	protected void readHeaderBuffer(ByteBuffer headerBuffer) {
		headerBuffer.getShort(); // consume version
		mTotalTicks = headerBuffer.getInt();

	}

	@Override
	protected void readNextFrame(ByteBuffer dataBuffer, ActionEventPlayer player) {
		// Read the next player input from the 'file' into temp
		final var lRemaining = dataBuffer.limit() - dataBuffer.position();
		if (lRemaining < 2) { // only check for the exisitence of the tick number
			System.out.println("END OF INPUT BUFFER REACHED BUT NO MARKER!");
			return;
		}

		final short nextFrameNumber = dataBuffer.getShort();
		final var nextControlByte = dataBuffer.get();

		System.out.println("  next frame number: " + nextFrameNumber);

		switch (nextControlByte) {
		case SatActionEventMap.BYTEMASK_CONTROL_END_GAME:
			System.out.println("       control: END GAME");
			break;

		case SatActionEventMap.BYTEMASK_CONTROL_KEYBOARD:
			System.out.println("       control: KEYBOARD");
			break;

		case SatActionEventMap.BYTEMASK_CONTROL_MOUSE:
			System.out.println("       control: MOUSE");
			break;

		case SatActionEventMap.BYTEMASK_CONTROL_GAMEPAD:
			System.out.println("       control: GAMEPAD");
			break;
		}

		if ((nextControlByte & SatActionEventMap.BYTEMASK_CONTROL_KEYBOARD) == SatActionEventMap.BYTEMASK_CONTROL_KEYBOARD) {
			System.out.println("      keyboard: ");

			final var nextInput = dataBuffer.get();

			player.tempFrameInput.isLeftShiftDown = (nextInput & SatActionEventMap.BYTEMASK_LEFT_SHIFT) == SatActionEventMap.BYTEMASK_LEFT_SHIFT;
			player.tempFrameInput.isSpaceDown = (nextInput & SatActionEventMap.BYTEMASK_SPACE) == SatActionEventMap.BYTEMASK_SPACE;
			player.tempFrameInput.isRDown = (nextInput & SatActionEventMap.BYTEMASK_R) == SatActionEventMap.BYTEMASK_R;
			player.tempFrameInput.isUpDown = (nextInput & SatActionEventMap.BYTEMASK_UP) == SatActionEventMap.BYTEMASK_UP;
			player.tempFrameInput.isDownDown = (nextInput & SatActionEventMap.BYTEMASK_DOWN) == SatActionEventMap.BYTEMASK_DOWN;
			player.tempFrameInput.isLeftDown = (nextInput & SatActionEventMap.BYTEMASK_LEFT) == SatActionEventMap.BYTEMASK_LEFT;
			player.tempFrameInput.isRightDown = (nextInput & SatActionEventMap.BYTEMASK_RIGHT) == SatActionEventMap.BYTEMASK_RIGHT;

			if (player.tempFrameInput.isSpaceDown) {
				System.out.println("            space");
			}

			if (player.tempFrameInput.isRDown) {
				System.out.println("            r");
			}

			if (player.tempFrameInput.isUpDown) {
				System.out.println("            up");
			}

			if (player.tempFrameInput.isDownDown) {
				System.out.println("            down");
			}

			if (player.tempFrameInput.isLeftDown) {
				System.out.println("            left");
			}

			if (player.tempFrameInput.isRightDown) {
				System.out.println("            right");
			}
		}

		if ((nextControlByte & SatActionEventMap.BYTEMASK_CONTROL_MOUSE) == SatActionEventMap.BYTEMASK_CONTROL_MOUSE) {
			System.out.println("      mouse: ");

			final var nextMouseInput = dataBuffer.get();

			player.tempFrameInput.mouseX = dataBuffer.getFloat();
			player.tempFrameInput.mouseY = dataBuffer.getFloat();

			player.tempFrameInput.isLeftMouseDown = (nextMouseInput & SatActionEventMap.BYTEMASK_MOUSE_LEFT) == SatActionEventMap.BYTEMASK_MOUSE_LEFT;
			player.tempFrameInput.isRightMouseDown = (nextMouseInput & SatActionEventMap.BYTEMASK_MOUSE_RIGHT) == SatActionEventMap.BYTEMASK_MOUSE_RIGHT;
			player.tempFrameInput.isLeftMouseDownTimed = (nextMouseInput & SatActionEventMap.BYTEMASK_MOUSE_LEFT_TIMED) == SatActionEventMap.BYTEMASK_MOUSE_LEFT_TIMED;
			player.tempFrameInput.isRightMouseDownTimed = (nextMouseInput & SatActionEventMap.BYTEMASK_MOUSE_RIGHT_TIMED) == SatActionEventMap.BYTEMASK_MOUSE_RIGHT_TIMED;

			if (player.tempFrameInput.isLeftMouseDown) {
				System.out.println("            left");
			}

			if (player.tempFrameInput.isRightMouseDown) {
				System.out.println("            right");
			}

			if (player.tempFrameInput.isLeftMouseDownTimed) {
				System.out.println("            left timed");
			}

			if (player.tempFrameInput.isRightMouseDownTimed) {
				System.out.println("            right timed");
			}

		}

		player.tempFrameInput.frameNumber = nextFrameNumber;
	}

}
