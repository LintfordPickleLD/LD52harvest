package lintfordpickle.harvest;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.input.BindableActionMap;
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;

// @formatter:off
public class GameActions extends BindableActionMap {

	public static final int THRUSTER_UP 	= 1000;
	public static final int THRUSTER_DOWN 	= 1001;
	public static final int THRUSTER_LEFT 	= 1002;
	public static final int THRUSTER_RIGHT 	= 1003;

	public GameActions() {

		// These will appear on the keybinds screen.

		addNewEventAction("Forward", 		THRUSTER_UP, 		GLFW.GLFW_KEY_UP, 		GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP);
		addNewEventAction("Backwards", 		THRUSTER_DOWN, 		GLFW.GLFW_KEY_DOWN, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN);
		addNewEventAction("Left", 			THRUSTER_LEFT, 		GLFW.GLFW_KEY_LEFT, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT);
		addNewEventAction("Right", 			THRUSTER_RIGHT, 	GLFW.GLFW_KEY_RIGHT, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT);

	}	
}
