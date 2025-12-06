package lintfordpickle.harvest.screens.game;

import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.screens.MainMenu;
import lintfordpickle.harvest.screens.menu.MenuBackgroundScreen;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.layouts.ListLayout;
import net.lintfordlib.screenmanager.screens.LoadingScreen;

public class PauseScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int SCREEN_BUTTON_CONTINUE = 10;
	private static final int SCREEN_BUTTON_RESTART = 11;
	private static final int SCREEN_BUTTON_EXIT = 12;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SceneHeader mSceneHeader;
	private PlayerManager mPlayerManager;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PauseScreen(ScreenManager screenManager, SceneHeader sceneHeader, PlayerManager playerManager) {
		super(screenManager, null);

		mSceneHeader = sceneHeader;
		mPlayerManager = playerManager;

		final var layout = new ListLayout(this);
		layout.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);
		layout.setDrawBackground(true, ColorConstants.WHITE());
		layout.showTitle(true);
		layout.title("Paused");

		// ---
		final var playEntry = new MenuEntry(screenManager, this, "Continue");
		playEntry.registerClickListener(this, SCREEN_BUTTON_CONTINUE);

		final var optionsEntry = new MenuEntry(screenManager, this, "Restart");
		optionsEntry.registerClickListener(this, SCREEN_BUTTON_RESTART);

		final var creditsEntry = new MenuEntry(screenManager, this, "Exit");
		creditsEntry.registerClickListener(this, SCREEN_BUTTON_EXIT);

		layout.addMenuEntry(playEntry);
		layout.addMenuEntry(optionsEntry);
		layout.addMenuEntry(creditsEntry);
		

		mLayouts.add(layout);

		mIsPopup = true;
		mShowBackgroundScreens = true;

		mShowContextualKeyHints = false;
	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);

	}

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case SCREEN_BUTTON_CONTINUE:
			exitScreen();
			return;

		case SCREEN_BUTTON_RESTART:
			final var gameScreen = new TimeTrialGameScreen(screenManager, mSceneHeader, mPlayerManager);
			screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, true, true, gameScreen));
			break;

		case SCREEN_BUTTON_EXIT:
			final var menuBackgroundScreen = new MenuBackgroundScreen(screenManager);
			final var menuScreen = new MainMenu(screenManager);
			screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, false, true, menuBackgroundScreen, menuScreen));
			break;

		}
	}
}
