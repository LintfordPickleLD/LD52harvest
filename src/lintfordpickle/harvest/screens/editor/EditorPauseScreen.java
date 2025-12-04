package lintfordpickle.harvest.screens.editor;

import lintfordpickle.harvest.screens.MainMenu;
import lintfordpickle.harvest.screens.menu.MenuBackgroundScreen;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.layouts.ListLayout;
import net.lintfordlib.screenmanager.screens.LoadingScreen;

public class EditorPauseScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int SCREEN_BUTTON_CONTINUE = 10;
	private static final int SCREEN_BUTTON_EXIT = 12;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SceneHeader mSceneHeader;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorPauseScreen(ScreenManager screenManager, SceneHeader sceneHeader) {
		super(screenManager, null);

		mSceneHeader = sceneHeader;

		final var lLayout = new ListLayout(this);
		lLayout.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);
		lLayout.setDrawBackground(true, ColorConstants.WHITE());
		lLayout.showTitle(true);
		lLayout.title("Paused");

		// ---
		final var lPlayEntry = new MenuEntry(screenManager, this, "Continue");
		lPlayEntry.registerClickListener(this, SCREEN_BUTTON_CONTINUE);

		final var lCreditsEntry = new MenuEntry(screenManager, this, "Exit");
		lCreditsEntry.registerClickListener(this, SCREEN_BUTTON_EXIT);

		lLayout.addMenuEntry(lPlayEntry);
		lLayout.addMenuEntry(lCreditsEntry);
		lLayout.addMenuEntry(MenuEntry.menuSeparator());

		mLayouts.add(lLayout);

		mIsPopup = true;
		mShowBackgroundScreens = true;

		mShowContextualKeyHints = false;
	}

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case SCREEN_BUTTON_CONTINUE:
			exitScreen();
			return;

		case SCREEN_BUTTON_EXIT:
			final var menuBackgroundScreen = new MenuBackgroundScreen(screenManager);
			final var menuScreen = new MainMenu(screenManager);
			screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, false, true, menuBackgroundScreen, menuScreen));
			break;

		}
	}
}
