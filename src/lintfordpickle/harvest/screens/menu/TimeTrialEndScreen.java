package lintfordpickle.harvest.screens.menu;

import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.screens.MainMenu;
import lintfordpickle.harvest.screens.game.TimeTrialGameScreen;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.time.TimeConstants;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.layouts.ListLayout;
import net.lintfordlib.screenmanager.screens.LoadingScreen;

public class TimeTrialEndScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int SCREEN_BUTTON_RESTART = 11;
	private static final int SCREEN_BUTTON_EXIT = 12;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SceneHeader mSceneHeader;
	private PlayerManager mPlayerManager;
	private float mTotalTimeInMs;
	private boolean mSurvived;

	private Texture mMenuTextureWrecked;
	private Texture mMenuTextureCompleted;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TimeTrialEndScreen(ScreenManager screenManager, SceneHeader sceneHeader, PlayerManager playerManager, boolean survived, float totalTimeInMs, boolean fastestTimeRecorded) {
		super(screenManager, "");

		mSurvived = survived;
		mSceneHeader = sceneHeader;
		mPlayerManager = playerManager;
		mTotalTimeInMs = totalTimeInMs;

		final var lLayout = new ListLayout(this);

		// ---

		var tempTime = mTotalTimeInMs;
		final var lTotalMinutes = (int) tempTime / TimeConstants.MillisPerMinute;
		tempTime -= lTotalMinutes * TimeConstants.MillisPerMinute;
		final var lTotalSeconds = (int) tempTime / TimeConstants.MillisPerSecond;
		tempTime -= lTotalSeconds * TimeConstants.MillisPerSecond;

		final var lRetryButton = new MenuEntry(screenManager, this, "Go Again");
		lRetryButton.registerClickListener(this, SCREEN_BUTTON_RESTART);

		final var lExitToMenuButton = new MenuEntry(screenManager, this, "Back to Menu");
		lExitToMenuButton.registerClickListener(this, SCREEN_BUTTON_EXIT);

		lLayout.addMenuEntry(lRetryButton);
		lLayout.addMenuEntry(lExitToMenuButton);

		mLayouts.add(lLayout);

		mIsPopup = true;
		mShowBackgroundScreens = true;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mMenuTextureWrecked = resourceManager.textureManager().loadTexture("TEXTURE_MENU_WRECKED", "res/textures/textureTextWrecked.png", entityGroupUid());
		mMenuTextureCompleted = resourceManager.textureManager().loadTexture("TEXTURE_MENU_COMPLETED", "res/textures/textureTextComplete.png", entityGroupUid());
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mMenuTextureWrecked = null;
		mMenuTextureCompleted = null;
	}

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case SCREEN_BUTTON_RESTART:
			screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, false, false, new TimeTrialGameScreen(screenManager, mSceneHeader, mPlayerManager)));
			break;

		case SCREEN_BUTTON_EXIT:
			screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, false, false, new MenuBackgroundScreen(screenManager), new MainMenu(screenManager)));
			break;
		}
	}

	@Override
	public void draw(LintfordCore core) {

		super.draw(core);

		final var titleFont = core.sharedResources().uiTitleFont();
		final var lFont = core.sharedResources().uiTextFont();
		final var spriteBatch = core.sharedResources().uiSpriteBatch();
		final var uiStructureController = screenManager.UiStructureController();
		final var headerRect = uiStructureController.menuTitleRectangle();

		lFont.begin(core.HUD());
		mSurvived = true;
		if (mSurvived) {
			if (mMenuTextureWrecked != null) {
				final float logoWidth = mMenuTextureCompleted.getTextureWidth();
				final float logoHeight = mMenuTextureCompleted.getTextureHeight();

				spriteBatch.setColorWhite();
				spriteBatch.begin(core.HUD());
				spriteBatch.draw(mMenuTextureCompleted, 0, 0, logoWidth, logoHeight, -logoWidth * .5f, headerRect.top(), logoWidth, logoHeight, .01f);
				spriteBatch.end();
			}

			var tempTime = mTotalTimeInMs;
			final var totalMinutes = (int) tempTime / TimeConstants.MillisPerMinute;
			tempTime -= totalMinutes * TimeConstants.MillisPerMinute;
			final var totalSeconds = (int) tempTime / TimeConstants.MillisPerSecond;
			tempTime -= totalSeconds * TimeConstants.MillisPerSecond;

			final var headerText = "TIME: " + totalMinutes + ":" + totalSeconds + ":" + (int) tempTime;
			final var headerTextWidth = titleFont.getStringWidth(headerText);
			final var screenHeight = core.config().display().windowHeight();

			final var textTitleHeight = -screenHeight / 4.f;
			mMenuHeaderPadding = screenHeight / 20.f;

			titleFont.begin(core.HUD());
			titleFont.drawText(headerText, -headerTextWidth / 2, textTitleHeight, .01f, 1.f);
			titleFont.end();

			final var gameOverText0 = "Well Done!";
			final var textWidth0 = lFont.getStringWidth(gameOverText0);
			lFont.drawText(gameOverText0, -textWidth0 / 2, textTitleHeight + 50, .01f, 1.f);

			final var isNewTopTime = true;
			if (isNewTopTime) {
				final var gameOverText1 = "You have set a new record time";
				final var textWidth1 = lFont.getStringWidth(gameOverText1);
				lFont.drawText(gameOverText1, -textWidth1 / 2, textTitleHeight + 75, .01f, 1.f);
			}

		} else {
			if (mMenuTextureWrecked != null) {
				final float logoWidth = mMenuTextureWrecked.getTextureWidth();
				final float logoHeight = mMenuTextureWrecked.getTextureHeight();

				spriteBatch.setColorWhite();
				spriteBatch.begin(core.HUD());
				spriteBatch.draw(mMenuTextureWrecked, 0, 0, logoWidth, logoHeight, -logoWidth * .5f, headerRect.top(), logoWidth, logoHeight, .01f);
				spriteBatch.end();
			}

			final var headerText = "Failed to deliver food";
			final var headerTextWidth = titleFont.getStringWidth(headerText);
			final var screenHeight = core.config().display().windowHeight();

			final var textTitleHeight = -screenHeight / 4.f;
			mMenuHeaderPadding = screenHeight / 20.f;

			titleFont.begin(core.HUD());
			titleFont.drawText(headerText, -headerTextWidth / 2, textTitleHeight, .01f, 1.f);
			titleFont.end();

			final var fameOverText0 = "You totaled your ship!";
			final var textWidth0 = lFont.getStringWidth(fameOverText0);
			lFont.drawText(fameOverText0, -textWidth0 / 2, textTitleHeight + 50, .01f, 1.f);

		}

		lFont.end();

		mScreenPaddingTop = 250.f;
	}

}
