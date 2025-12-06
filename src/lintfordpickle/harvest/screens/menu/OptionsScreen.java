package lintfordpickle.harvest.screens.menu;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.layouts.ListLayout;
import net.lintfordlib.screenmanager.screens.AudioOptionsScreen;
import net.lintfordlib.screenmanager.screens.ControllerOptionsScreen;
import net.lintfordlib.screenmanager.screens.KeyBindOptionsScreen;

public class OptionsScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int BUTTON_GAME = 10;
	private static final int BUTTON_AUDIO = 11;
	private static final int BUTTON_GRAPHICS = 12;
	private static final int BUTTON_KEY_BINDS = 13;
	private static final int BUTTON_CONTROLLER = 14;
	private static final int BUTTON_BACK = 30;

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public OptionsScreen(ScreenManager pScreenManager) {
		super(pScreenManager, "SETTINGS");

		final var layout = new ListLayout(this);
		// layout.setDrawBackground(true, ColorConstants.WHITE());
		layout.layoutWidth(LAYOUT_WIDTH.HALF);
		layout.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		layout.showTitle(false);
		layout.cropPaddingTop(10.f);
		layout.cropPaddingBottom(10.f);

		final var gameSettingsEntry = new MenuEntry(screenManager, this, "Game");
		gameSettingsEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		gameSettingsEntry.registerClickListener(this, BUTTON_GAME);

		final var audioSettingsEntry = new MenuEntry(screenManager, this, "Audio");
		audioSettingsEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		audioSettingsEntry.registerClickListener(this, BUTTON_AUDIO);

		final var graphicsEntry = new MenuEntry(screenManager, this, "Graphics");
		graphicsEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		graphicsEntry.registerClickListener(this, BUTTON_GRAPHICS);

		final var keyBindsEntry = new MenuEntry(screenManager, this, "Key Binds");
		keyBindsEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		keyBindsEntry.registerClickListener(this, BUTTON_KEY_BINDS);

		final var controllerSettingsEntry = new MenuEntry(screenManager, this, "Controller");
		controllerSettingsEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		controllerSettingsEntry.registerClickListener(this, BUTTON_CONTROLLER);

		final var backEntry = new MenuEntry(screenManager, this, "Back");
		backEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		backEntry.registerClickListener(this, BUTTON_BACK);
		backEntry.gamepadMenuIcon.manualGamepadInputCode(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST);

		layout.addMenuEntry(gameSettingsEntry);
		layout.addMenuEntry(audioSettingsEntry);
		layout.addMenuEntry(graphicsEntry);
		layout.addMenuEntry(keyBindsEntry);
		layout.addMenuEntry(controllerSettingsEntry);
		layout.addMenuEntry(MenuEntry.menuSeparator());
		layout.addMenuEntry(backEntry);

		mScreenPaddingTop = 30.f;
		mLayoutPaddingHorizontal = 50.f;
		mLayoutAlignment = LAYOUT_ALIGNMENT.LEFT;

		mShowBackgroundScreens = false;

		mLayouts.add(layout);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);
	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_AUDIO:
			screenManager.addScreen(new AudioOptionsScreen(screenManager));
			break;

		case BUTTON_KEY_BINDS:
			screenManager.addScreen(new KeyBindOptionsScreen(screenManager));
			break;

		case BUTTON_CONTROLLER:
			screenManager.addScreen(new ControllerOptionsScreen(screenManager));
			break;

		case BUTTON_BACK:
			exitScreen();
			break;
		}
	}

}
