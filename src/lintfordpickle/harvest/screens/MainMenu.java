package lintfordpickle.harvest.screens;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.replays.ReplayController;
import lintfordpickle.harvest.screens.editor.EditorSceneSelectionScreen;
import lintfordpickle.harvest.screens.menu.MenuHelpScreen;
import lintfordpickle.harvest.screens.menu.OptionsScreen;
import lintfordpickle.harvest.screens.menu.TimeTrialLandingScreen;
import net.lintfordlib.ConstantsEditor;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.layouts.ListLayout;

public class MainMenu extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String TITLE = null;

	private static final int SCREEN_BUTTON_PLAY = 11;
	private static final int SCREEN_BUTTON_EDITOR = 99;
	private static final int SCREEN_BUTTON_HELP = 12;
	private static final int SCREEN_BUTTON_OPTIONS = 13;
	private static final int SCREEN_BUTTON_EXIT = 15;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ReplayController mReplayController;
	private ListLayout mMainMenuListBox;

	private Texture mMenuLogoTexture;

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public MainMenu(ScreenManager pScreenManager) {
		super(pScreenManager, TITLE);

		mLayoutAlignment = LAYOUT_ALIGNMENT.LEFT;

		mMainMenuListBox = new ListLayout(this);
		mMainMenuListBox.setDrawBackground(true, ColorConstants.getColor(.7f, .3f, .7f, .5f));
		mMainMenuListBox.layoutWidth(LAYOUT_WIDTH.HALF);
		mMainMenuListBox.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		final var lStartGameEntry = new MenuEntry(screenManager, this, "Start Game");
		lStartGameEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		lStartGameEntry.registerClickListener(this, SCREEN_BUTTON_PLAY);
		lStartGameEntry.setToolTip("Harvest and deliver food from each of the farms in the fastest time.");

		final var lEditorEntry = new MenuEntry(screenManager, this, "Editor");
		lEditorEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		lEditorEntry.registerClickListener(this, SCREEN_BUTTON_EDITOR);

		final var lHelpButton = new MenuEntry(screenManager, this, "Instructions");
		lHelpButton.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		lHelpButton.registerClickListener(this, SCREEN_BUTTON_HELP);

		final var lOptionsEntry = new MenuEntry(screenManager, this, "Options");
		lOptionsEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		lOptionsEntry.registerClickListener(this, SCREEN_BUTTON_OPTIONS);

		final var lExitEntry = new MenuEntry(screenManager, this, "Exit");
		lExitEntry.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		lExitEntry.registerClickListener(this, SCREEN_BUTTON_EXIT);

		mMainMenuListBox.addMenuEntry(lStartGameEntry);
		mMainMenuListBox.addMenuEntry(MenuEntry.menuSeparator());
		mMainMenuListBox.addMenuEntry(lEditorEntry);
		mMainMenuListBox.addMenuEntry(MenuEntry.menuSeparator());
		mMainMenuListBox.addMenuEntry(lHelpButton);
		mMainMenuListBox.addMenuEntry(lOptionsEntry);
		mMainMenuListBox.addMenuEntry(MenuEntry.menuSeparator());
		mMainMenuListBox.addMenuEntry(lExitEntry);

		mLayouts.add(mMainMenuListBox);

		mSelectedLayoutIndex = mLayouts.size() - 1;
		mSelectedEntryIndex = 0;

		mScreenPaddingTop = 40.f;
		mLayoutPaddingHorizontal = 50.f;

		mIsPopup = false;
		mShowBackgroundScreens = true;
		mESCBackEnabled = false;

		screenManager.contextHintManager().drawContextBackground(true);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final var lControllerManager = screenManager.core().controllerManager();
		mReplayController = (ReplayController) lControllerManager.getControllerByNameRequired(ReplayController.CONTROLLER_NAME, ConstantsGame.GAME_RESOURCE_GROUP_ID);

		final var lReplayManager = mReplayController.replayManager();
		lReplayManager.loadRecordedGame();
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mMenuLogoTexture = resourceManager.textureManager().loadTexture("TEXTURE_MENU_LOGO", "res/textures/textureTextHarvester.png", entityGroupUid());
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mMenuLogoTexture = null;
	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);

		final var lUiStructureController = screenManager.UiStructureController();
		final var lHeaderRect = lUiStructureController.menuTitleRectangle();

		if (mMenuLogoTexture != null) {
			final var spriteBatch = core.sharedResources().uiSpriteBatch();

			final var s_w = core.HUD().boundingRectangle().width() / 800.f;
			final var s_h = core.HUD().boundingRectangle().height() / 600.f;

			final float logoWidth = mMenuLogoTexture.getTextureWidth() * s_w;
			final float logoHeight = mMenuLogoTexture.getTextureHeight() * s_h;

			spriteBatch.begin(core.HUD());
			spriteBatch.draw(mMenuLogoTexture, 0, 0, logoWidth / s_w, logoHeight / s_h, -logoWidth * .5f, lHeaderRect.top() + 5, logoWidth, logoHeight, -0.01f);
			spriteBatch.end();
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {

		switch (mClickAction.consume()) {
		case SCREEN_BUTTON_PLAY: {
			final var topMostScreen = screenManager.getTopScreen();

			if (topMostScreen instanceof TimeTrialLandingScreen)
				return;

			if (!(topMostScreen instanceof MainMenu))
				screenManager.removeScreen(topMostScreen);

			screenManager.addScreen(new TimeTrialLandingScreen(screenManager));
			break;
		}

		case SCREEN_BUTTON_EDITOR: {
			ConstantsEditor.EDITOR_RESOURCE_GROUP_ID = ConstantsGame.GAME_RESOURCE_GROUP_ID;

			final var lTopMostScreen = screenManager.getTopScreen();
			if (!(lTopMostScreen instanceof MainMenu)) {
				screenManager.removeScreen(lTopMostScreen);
			}

			final var pathsConfig = screenManager.core().config().resourcePaths();

			screenManager.addScreen(new EditorSceneSelectionScreen(screenManager, pathsConfig, true));
			


			break;
		}

		case SCREEN_BUTTON_OPTIONS: {
			final var lTopMostScreen = screenManager.getTopScreen();
			if (!(lTopMostScreen instanceof MainMenu)) {
				screenManager.removeScreen(lTopMostScreen);
			}

			screenManager.addScreen(new OptionsScreen(screenManager));
			break;
		}

		case SCREEN_BUTTON_HELP: {
			final var lTopMostScreen = screenManager.getTopScreen();
			if (!(lTopMostScreen instanceof MainMenu)) {
				screenManager.removeScreen(lTopMostScreen);
			}

			screenManager.addScreen(new MenuHelpScreen(screenManager));
			break;
		}

		case SCREEN_BUTTON_EXIT:
			screenManager.exitGame();
			break;
		}
	}
}
