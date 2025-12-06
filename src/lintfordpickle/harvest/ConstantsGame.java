package lintfordpickle.harvest;

import net.lintfordlib.assets.ResourceGroupProvider;

public class ConstantsGame {

	// ---------------------------------------------
	// Setup
	// ---------------------------------------------

	public static final String FOOTER_TEXT = "2025 (c) LintfordPickle";

	public static final String APPLICATION_NAME = "Harvester 008";
	public static final String WINDOW_TITLE = "Harvester 008";

	public static final float ASPECT_RATIO = 16.f / 9.f;

	public static final int GAME_CANVAS_WIDTH = 960;
	public static final int GAME_CANVAS_HEIGHT = 540;

	public static final int GAME_RESOURCE_GROUP_ID = ResourceGroupProvider.getRollingEntityNumber();

	public static final String CAMPAIGN_SCENES_DIRECTORY = "res/scenes/campaign";
	public static final String CUSTOM_SCENES_DIRECTORY = "res/scenes/custom";

	// ---------------------------------------------
	// Game
	// ---------------------------------------------

	public static final boolean LOCK_ZOOM_TO_ONE = true;

	public static final float SURVIVAL_STARTING_TIME_IN_MS = 60 * 4 * 1000;

	// ---------------------------------------------
	// Physics
	// ---------------------------------------------

	// @formatter:off
	public static final int PHYSICS_WORLD_MASK_SHIP     = 0b0000000000000001;
	public static final int PHYSICS_WORLD_MASK_PLATFORM = 0b0000000000000010;
	public static final int PHYSICS_WORLD_MASK_WALL     = 0b0000000000000100;
	public static final int PHYSICS_WORLD_MASK_GHOST    = 0b0000000000001000;
	// @formatter:on

	// ---------------------------------------------
	// Debug
	// ---------------------------------------------

	public static final boolean IS_DEBUG_MODE = true;
	public static final boolean CAMERA_DEBUG_MODE = true;
	public static final boolean PHYICS_DEBUG_MODE = false;
	public static final boolean SHIP_DEBUG_MODE = true;

	// produces a lot of log data
	public static final boolean DEBUG_OUTPUT_ACTIONEVENT_LOGS = false;

	public static final boolean WRAP_OBJECTS_AROUND_SCREEN_EDGE = false;
	public static final boolean QUICK_LAUNCH_EDITOR = false; // takes precedence over QUICK_LAUNCH_GAME
	public static final boolean QUICK_LAUNCH_GAME = false;
	public static final boolean ESCAPE_RESTART_MAIN_SCENE = false;
}
