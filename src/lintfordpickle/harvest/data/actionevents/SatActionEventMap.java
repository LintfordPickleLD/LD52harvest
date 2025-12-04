package lintfordpickle.harvest.data.actionevents;

public class SatActionEventMap {

	// ---------------------------------------------
	// Event Actions
	// ---------------------------------------------

	// @formatter:off
	
	// public static final int BYTEMASK_CONTROL_START_GAME = 0x00000001;
	public static final int BYTEMASK_CONTROL_END_GAME   = 0b00000001;
	public static final int BYTEMASK_CONTROL_KEYBOARD   = 0b00000010;
	public static final int BYTEMASK_CONTROL_MOUSE      = 0b00000100;
	public static final int BYTEMASK_CONTROL_GAMEPAD    = 0b00001000;

	public static final int BYTEMASK_LEFT_SHIFT         = 0b00000001;
	public static final int BYTEMASK_SPACE              = 0b00000010;
	public static final int BYTEMASK_R                  = 0b00000100;
	public static final int BYTEMASK_UP                 = 0b00001000;
	public static final int BYTEMASK_DOWN               = 0b00010000;
	public static final int BYTEMASK_LEFT               = 0b00100000;
	public static final int BYTEMASK_RIGHT              = 0b01000000;
	
	public static final int BYTEMASK_MOUSE_LEFT         = 0b00000001;
	public static final int BYTEMASK_MOUSE_MIDDLE       = 0b00000010;
	public static final int BYTEMASK_MOUSE_RIGHT        = 0b00000100;
	public static final int BYTEMASK_MOUSE_LEFT_TIMED   = 0b00001000;
	public static final int BYTEMASK_MOUSE_RIGHT_TIMED  = 0b00010000;
	
}
