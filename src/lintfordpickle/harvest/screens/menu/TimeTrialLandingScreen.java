package lintfordpickle.harvest.screens.menu;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.replays.ReplayController;
import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.data.players.ReplayManager;
import lintfordpickle.harvest.screens.game.TimeTrialGameScreen;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.time.TimeConstants;
import net.lintfordlib.data.scene.SceneManager;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.entries.MenuInputEntry;
import net.lintfordlib.screenmanager.entries.MenuLabelEntry;
import net.lintfordlib.screenmanager.entries.MenuToggleEntry;
import net.lintfordlib.screenmanager.layouts.ListLayout;
import net.lintfordlib.screenmanager.screens.LoadingScreen;

public class TimeTrialLandingScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String TITLE = "";

	private static final int SCREEN_BUTTON_PLAY_TIME_TRIAL = 11;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ReplayController mReplayController;
	private ListLayout mMainMenuListBox;

	private MenuToggleEntry mGhostEnabled;
	private MenuInputEntry mGhostFatestTime;
	private MenuLabelEntry mNoFastestTime;

	private SceneManager mSceneManager;

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public TimeTrialLandingScreen(ScreenManager pScreenManager) {
		super(pScreenManager, TITLE);

		mMainMenuListBox = new ListLayout(this);
		mMainMenuListBox.layoutWidth(LAYOUT_WIDTH.THREEQUARTER);
		mMainMenuListBox.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);
		mMainMenuListBox.setDrawBackground(true, new Color(0.02f, 0.12f, 0.15f, 0.8f));
		mMainMenuListBox.title("Time-Trial Mode");
		mMainMenuListBox.showTitle(true);

		final var lPlayTimeEntry = new MenuEntry(screenManager, this, "Start");
		lPlayTimeEntry.registerClickListener(this, SCREEN_BUTTON_PLAY_TIME_TRIAL);
		lPlayTimeEntry.setToolTip("You need ot harvest and deliver food from each of the farms. Fastest time wins.");

		mGhostFatestTime = new MenuInputEntry(pScreenManager, this);
		mGhostFatestTime.label("Fastest Time");
		mGhostFatestTime.horizontalFillType(FILLTYPE.THIRD_PARENT);
		mGhostFatestTime.readOnly(true);

		mGhostEnabled = new MenuToggleEntry(pScreenManager, this);
		mGhostEnabled.label("Ghost");
		mGhostEnabled.horizontalFillType(FILLTYPE.TAKE_DESIRED_SIZE);
		mGhostEnabled.showInfoButton(true);
		mGhostEnabled.setToolTip("The ghost ship replays the actions of the fastest time, but doesn't interfere with the gameplay");

		mNoFastestTime = new MenuLabelEntry(pScreenManager, this);
		mNoFastestTime.label("No fastest time");

		mMainMenuListBox.addMenuEntry(mNoFastestTime);
		mMainMenuListBox.addMenuEntry(mGhostFatestTime);
		mMainMenuListBox.addMenuEntry(MenuEntry.menuSeparator());
		mMainMenuListBox.addMenuEntry(mGhostEnabled);
		mMainMenuListBox.addMenuEntry(MenuEntry.menuSeparator());
		mMainMenuListBox.addMenuEntry(MenuEntry.menuSeparator());
		mMainMenuListBox.addMenuEntry(lPlayTimeEntry);

		mLayouts.add(mMainMenuListBox);

		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 6;

		mScreenPaddingTop = 40.f;

		mLayoutAlignment = LAYOUT_ALIGNMENT.CENTER;
		mLayoutPaddingHorizontal = 50.f;

		mIsPopup = false;
		mShowBackgroundScreens = true;

		// mBlockMouseInputInBackground = false;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final var dataManager = screenManager.core().dataManager();
		mSceneManager = (SceneManager) dataManager.getDataManagerByName(SceneManager.DATA_MANAGER_NAME, ConstantsGame.GAME_RESOURCE_GROUP_ID);

		final var lControllerManager = screenManager.core().controllerManager();
		mReplayController = (ReplayController) lControllerManager.getControllerByNameRequired(ReplayController.CONTROLLER_NAME, ConstantsGame.GAME_RESOURCE_GROUP_ID);

		final var lReplayManager = mReplayController.replayManager();
		lReplayManager.loadRecordedGame();

		if (lReplayManager.isRecordedGameAvailable()) {

			var tempTime = lReplayManager.header().runtimeInSeconds();
			final var lTotalMinutes = (int) tempTime / TimeConstants.MillisPerMinute;
			tempTime -= lTotalMinutes * TimeConstants.MillisPerMinute;
			final var lTotalSeconds = (int) tempTime / TimeConstants.MillisPerSecond;
			tempTime -= lTotalSeconds * TimeConstants.MillisPerSecond;

			mNoFastestTime.enabled(false);

			mGhostEnabled.isChecked(true);

			mGhostFatestTime.inputString(lTotalMinutes + ":" + lTotalSeconds + " s");
			mGhostFatestTime.canHaveFocus(false);

		} else {
			mNoFastestTime.enabled(true);
			// mNoFastestTime.active(true);

			mGhostEnabled.enabled(false);
			mGhostEnabled.isChecked(false);

			mGhostFatestTime.inputString(null);
		}
	}

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case SCREEN_BUTTON_PLAY_TIME_TRIAL: {

			// TODO: Default player is created automatically - and will be controlled by the player.
			final var lPlayerManager = new PlayerManager();
			lPlayerManager.getPlayer(PlayerManager.DEFAULT_PLAYER_SESSION_UID).setRecorder("player.lmp");
			lPlayerManager.getPlayer(PlayerManager.DEFAULT_PLAYER_SESSION_UID).setPlayerControlled(true);

			if (mGhostEnabled.isChecked()) {
				final var lReplayManager = mReplayController.replayManager();
				if (lReplayManager.isRecordedGameAvailable()) {
					final var lGhostPlayer = lPlayerManager.addNewPlayer();
					lGhostPlayer.setPlayback(ReplayManager.RecordedGameFilename);
					lGhostPlayer.setPlayerControlled(false);
					lGhostPlayer.isGhostMode(true);
				}
			}

			final var lSceneHeader = mSceneManager.loadSceneHeader("level1");
			final var timeTrialGameScreen = new TimeTrialGameScreen(screenManager, lSceneHeader, lPlayerManager);
			screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, true, true, timeTrialGameScreen));
			break;
		}
		}
	}
}
