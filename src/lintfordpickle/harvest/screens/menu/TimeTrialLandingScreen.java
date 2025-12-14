package lintfordpickle.harvest.screens.menu;

import java.io.File;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.controllers.replays.ReplayController;
import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.data.players.ReplayManager;
import lintfordpickle.harvest.screens.game.TimeTrialGameScreen;
import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.fonts.FontUnit.WrapType;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.core.time.TimeConstants;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.data.scene.SceneManager;
import net.lintfordlib.screenmanager.IListBoxItemDoubleClick;
import net.lintfordlib.screenmanager.IListBoxItemSelected;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.MenuStyles;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.entries.HorizontalEntryGroup;
import net.lintfordlib.screenmanager.entries.MenuInputEntry;
import net.lintfordlib.screenmanager.entries.MenuListBox;
import net.lintfordlib.screenmanager.entries.MenuListBoxItem;
import net.lintfordlib.screenmanager.entries.MenuToggleEntry;
import net.lintfordlib.screenmanager.entries.listboxitems.LabelValueListBoxItem;
import net.lintfordlib.screenmanager.layouts.ListLayout;
import net.lintfordlib.screenmanager.screens.LoadingScreen;

public class TimeTrialLandingScreen extends MenuScreen implements IListBoxItemSelected, IListBoxItemDoubleClick {

	private class SceneMenuListItem extends LabelValueListBoxItem {

		private static final long serialVersionUID = -8020079885315963287L;

		private final SceneHeader mSceneHeader;

		public SceneHeader sceneHeader() {
			return mSceneHeader;
		}

		public SceneMenuListItem(ScreenManager screenManager, MenuListBox parentListBox, SceneHeader sceneHeader, int entityGroupUid) {
			super(screenManager, parentListBox, entityGroupUid);

			mSceneHeader = sceneHeader;
			mH = 40;
		}

		@Override
		public void draw(LintfordCore core, Screen screen, SpriteBatch spriteBatch, SpriteSheetDefinition coreDef, FontUnit fontUnit, float zDepth, boolean isActiveSelection, boolean isHighlighted) {
			// super.draw(core, screen, spriteBatch, coreDef, fontUnit, zDepth, isActiveSelection, isHighlighted);
			mH = 35;
			if (isHighlighted) {
				spriteBatch.setColorWhite();
				renderHighlight(core, screen, spriteBatch, coreDef, zDepth - .01f);
			}

			if (isActiveSelection) {
				spriteBatch.setColorWhite();
				renderSelectionBar(core, screen, spriteBatch, coreDef, zDepth - .01f);
			}

			final var transitionOffset = screen.screenPositionOffset();

			spriteBatch.setColor(entryColor);
			spriteBatch.draw(coreDef, CoreTextureNames.TEXTURE_WHITE, transitionOffset.x + mX, transitionOffset.y + mY, mW, mH, zDepth);

			if (mLabelValue != null && mLabelValue.length() > 0) {

				final var textScale = mScreenManager.UiStructureController().uiTextScaleFactor();
				final var font = mParentListBox.parentScreen().font();

				var detailsTextScale = 0.7f;

				font.begin(core.HUD());
				font.setTextColor(textColor);
				font.drawText("Fastest Time: 1:41:890", transitionOffset.x + mX + 5.f, transitionOffset.y + mY + mH - font.fontHeight() * detailsTextScale, zDepth, detailsTextScale, -1);
				var maxWidth = mW - font.getStringWidth("Name:", textScale);
				var sceneName = mSceneHeader.sceneName() != null ? mSceneHeader.sceneName() : "<no name>";

				font.setWrapType(WrapType.WORD_WRAP_TRIM);
				font.drawText(sceneName, transitionOffset.x + mX + 5.f, transitionOffset.y + mY, zDepth, textScale, maxWidth);

				var sceneFolderName = mSceneHeader.sceneParentDirectory();

				var sceneFolderNameWidth = font.getStringWidth(sceneFolderName, detailsTextScale);
				font.drawText(sceneFolderName, transitionOffset.x + mX + mW - 2 - sceneFolderNameWidth, transitionOffset.y + mY + mH - font.fontHeight() * detailsTextScale, zDepth, detailsTextScale, -1);

				var folderName = mSceneHeader.sceneFolderName();
				var folderNameWidth = font.getStringWidth(folderName, detailsTextScale);
				font.drawText(folderName, transitionOffset.x + mX + mW - 2 - folderNameWidth, transitionOffset.y + mY + mH - font.fontHeight() * 2 * detailsTextScale, zDepth, detailsTextScale, -1);

				font.end();
			}

			if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
				Debug.debugManager().drawers().drawRectImmediate(core.HUD(), this);
			}

		}

	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String TITLE = "Time Trail Mode";

	private static final int BUTTON_START = 11;
	private static final int BUTTON_BACK = 12;
	private static final int BUTTON_CUSTOM = 13;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ReplayController mReplayController;
	private ListLayout mLayoutOptions;
	private ListLayout mLayoutSelection;

	private MenuToggleEntry mGhostEnabled;
	private MenuInputEntry mGhostFatestTime;
	private MenuToggleEntry mCustomMapsEnabled;

	private SceneManager mSceneManager;

	private MenuListBox mSceneFilenameEntries;

	private MenuEntry mStartEntry;
	private MenuEntry mBackEntry;

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public TimeTrialLandingScreen(ScreenManager pScreenManager) {
		super(pScreenManager, TITLE);

		mLayoutOptions = new ListLayout(this);
		mLayoutOptions.layoutWidth(LAYOUT_WIDTH.HALF);
		mLayoutOptions.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);
		mLayoutOptions.setDrawBackground(true, new Color(0.02f, 0.12f, 0.15f, 0.8f));

		mLayoutSelection = new ListLayout(this);
		mLayoutSelection.layoutWidth(LAYOUT_WIDTH.HALF);
		mLayoutSelection.setDrawBackground(true, new Color(0.02f, 0.12f, 0.15f, 0.8f));

		mGhostEnabled = new MenuToggleEntry(pScreenManager, this, "Ghost Racer");
		mGhostEnabled.showInfoButton(true);
		mGhostEnabled.setToolTip("The ghost ship replays the actions of the fastest time, but doesn't interfere with the gameplay");

		mGhostFatestTime = new MenuInputEntry(pScreenManager, this);
		mGhostFatestTime.label("Fastest Time");
		mGhostFatestTime.horizontalFillType(FILLTYPE.THIRD_PARENT);
		mGhostFatestTime.readOnly(false);
		mGhostFatestTime.singleLine(true);

		mCustomMapsEnabled = new MenuToggleEntry(pScreenManager, this, "Custom Scenes");
		mCustomMapsEnabled.registerClickListener(this, BUTTON_CUSTOM);

		mLayoutOptions.addMenuEntry(mGhostEnabled);
		mLayoutOptions.addMenuEntry(mGhostFatestTime);
		mLayoutOptions.addMenuEntry(mCustomMapsEnabled);

		mSceneFilenameEntries = new MenuListBox(screenManager, this);
		mSceneFilenameEntries.setItemSelectedListener(this);
		mSceneFilenameEntries.setItemDoubleClickListener(this);
		mSceneFilenameEntries.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		mSceneFilenameEntries.marginLeft(10);
		mSceneFilenameEntries.marginRight(10);

		final var horizontalButtonLayout = new HorizontalEntryGroup(screenManager, this);
		horizontalButtonLayout.horizontalFillType(FILLTYPE.THREEQUARTER_PARENT);

		mBackEntry = new MenuEntry(screenManager, this, "Back");
		mBackEntry.registerClickListener(this, BUTTON_BACK);
		mBackEntry.desiredWidth(200);
		mBackEntry.horizontalFillType(FILLTYPE.TAKE_DESIRED_SIZE);

		mStartEntry = new MenuEntry(screenManager, this, "Start");
		mStartEntry.registerClickListener(this, BUTTON_START);
		mStartEntry.setToolTip("You need ot harvest and deliver food from each of the farms. Fastest time wins.");
		mStartEntry.desiredWidth(200);
		mStartEntry.horizontalFillType(FILLTYPE.TAKE_DESIRED_SIZE);

		horizontalButtonLayout.addEntry(mBackEntry);
		horizontalButtonLayout.addEntry(mStartEntry);

		mLayoutSelection.addMenuEntry(mSceneFilenameEntries);
		mLayoutSelection.addMenuEntry(horizontalButtonLayout);

		mLayouts.add(mLayoutOptions);
		mLayouts.add(mLayoutSelection);

		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 6;

		mScreenPaddingTop = 40.f;

		mLayoutAlignment = LAYOUT_ALIGNMENT.CENTER;
		// mLayoutPaddingHorizontal = 50.f;

		mIsPopup = false;
		mShowBackgroundScreens = true;

		mLayoutOptions.paddingTop(MenuStyles.paddingTop);
		mLayoutOptions.paddingBottom(MenuStyles.paddingBottom);
		mLayoutOptions.marginBottom(MenuStyles.marginBottom);

		mLayoutSelection.paddingTop(MenuStyles.paddingTop);
		mLayoutSelection.paddingBottom(MenuStyles.paddingBottom);

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

			mGhostEnabled.isChecked(true);

			mGhostFatestTime.inputString(lTotalMinutes + ":" + lTotalSeconds + " s");
			mGhostFatestTime.canHaveFocus(false);

		} else {
			mGhostEnabled.enabled(false);
			mGhostEnabled.isChecked(false);

			mGhostFatestTime.inputString(null);
		}

		final var gameResourcePaths = screenManager.core().config().resourcePaths();
		final var campaignScenesDirectory = gameResourcePaths.getKeyValue(SceneManager.SCENE_MAIN_DIRECTORY, ConstantsGame.CAMPAIGN_SCENES_DIRECTORY);

		// TODO: Need a way to play custom maps ..

		populateDropDownListWithSceneFilenames(mSceneFilenameEntries, campaignScenesDirectory);
	}

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_START: {

			final var selectedTrackItem = (SceneMenuListItem) mSceneFilenameEntries.getSelectedItem();
			final var sceneHeader = selectedTrackItem.sceneHeader();

			onLoadScene(sceneHeader);
			break;
		}

		case BUTTON_BACK:

			exitScreen();
			break;

		}
	}

	private void onLoadScene(SceneHeader sceneHeader) {
		if (sceneHeader == null)
			return;

		// TODO: Default player is created automatically - and will be controlled by the player.
		final var playerManager = new PlayerManager();
		playerManager.getPlayer(PlayerManager.DEFAULT_PLAYER_SESSION_UID).setRecorder("player.lmp");
		playerManager.getPlayer(PlayerManager.DEFAULT_PLAYER_SESSION_UID).setPlayerControlled(true);

		if (mGhostEnabled.isChecked()) {
			final var lReplayManager = mReplayController.replayManager();
			if (lReplayManager.isRecordedGameAvailable()) {
				final var ghostPlayer = playerManager.addNewPlayer();
				ghostPlayer.setPlayback(ReplayManager.RecordedGameFilename);
				ghostPlayer.setPlayerControlled(false);
				ghostPlayer.isGhostMode(true);
			}
		}

		final var timeTrialGameScreen = new TimeTrialGameScreen(screenManager, sceneHeader, playerManager);
		screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, true, true, timeTrialGameScreen));

	}

	protected void populateDropDownListWithSceneFilenames(MenuListBox sceneList, String scenesDirectory) {
		sceneList.clearListBox();

		final var listOfTracks = FileUtils.getListOfFilesInSubDirectories(scenesDirectory, ".hdr");
		final var trackCount = listOfTracks.size();

		for (var i = 0; i < trackCount; i++) {
			final var sceneHeaderFileName = listOfTracks.get(i);
			final var sceneHeaderFile = new File(sceneHeaderFileName.toString());

			if (!sceneHeaderFile.exists()) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "/-/-/-/-/-/-/-/-/-/-/-/-");
				continue;
			}

			// need the load the fucker so we know its valid
			final var sceneFolderName = sceneHeaderFileName.getParent().getFileName().toString();
			final var sceneHeader = SceneHeader.loadSceneHeaderFileFromFilepath(sceneFolderName, scenesDirectory);

			if (sceneHeader == null) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Failed to load scene header: " + sceneHeaderFile.toString());
				continue;
			}

			final var sceneName = sceneHeaderFileName.getFileName().toString();
			final var newItem = new SceneMenuListItem(screenManager, sceneList, sceneHeader, entityGroupUid());
			newItem.labelValue("scene");
			newItem.textValue(sceneName);

			sceneList.addEntry(newItem);
		}

		if (trackCount > 0)
			sceneList.selectedIndex(0);
	}

	@Override
	public void onListItemDoubleClicked(MenuListBoxItem listboxItem) {

		// TODO: log all errors

		if (listboxItem == null)
			return;

		if (!(listboxItem instanceof SceneMenuListItem selectedMenuItem)) {
			return;
		}

		if (selectedMenuItem.sceneHeader() == null)
			return;

		onLoadScene(selectedMenuItem.sceneHeader());

	}

	@Override
	public void onListBoxItemSelected(MenuListBoxItem item, int itemIndex) {
		// ignore
	}

}
