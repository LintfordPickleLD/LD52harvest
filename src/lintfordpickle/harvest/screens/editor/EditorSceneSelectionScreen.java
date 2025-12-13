package lintfordpickle.harvest.screens.editor;

import java.io.File;

import lintfordpickle.harvest.ConstantsGame;
import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.fonts.FontUnit.WrapType;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.data.scene.SceneManager;
import net.lintfordlib.options.ResourcePathsConfig;
import net.lintfordlib.screenmanager.BaseEditorSceneSelectionScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.entries.MenuListBox;
import net.lintfordlib.screenmanager.entries.MenuListBoxItem;
import net.lintfordlib.screenmanager.entries.MenuToggleEntry;
import net.lintfordlib.screenmanager.entries.listboxitems.LabelValueListBoxItem;
import net.lintfordlib.screenmanager.layouts.BaseLayout;
import net.lintfordlib.screenmanager.screens.LoadingScreen;

public class EditorSceneSelectionScreen extends BaseEditorSceneSelectionScreen<SceneHeader> {

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

				font.begin(core.HUD());
				font.setTextColor(textColor);
				font.drawText("Name:", transitionOffset.x + mX + 5.f, transitionOffset.y + mY, zDepth, textScale, -1);

				var maxWidth = mW - font.getStringWidth("Name:", textScale);
				var sceneName = mSceneHeader.sceneName();

				font.setWrapType(WrapType.WORD_WRAP_TRIM);
				font.drawText(sceneName, transitionOffset.x + mX + 64, transitionOffset.y + mY, zDepth, textScale, maxWidth);

				var sceneFolderName = mSceneHeader.sceneParentDirectory();
				var detailsTextScale = 0.7f;
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

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int TOGGLE_ENTRY_CLICKED = 10;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MenuToggleEntry mCampaignMapsToggle;

	@Override
	protected String getInitialScenesDirectory() {
		mCampaignMapsToggle.isChecked(false);
		final var gameResourcePaths = mResourcePathsConfig;
		final var baseScenesPath = gameResourcePaths.getKeyValue(SceneManager.SCENE_CUSTOM_DIRECTORY, ConstantsGame.CUSTOM_SCENES_DIRECTORY);

		return baseScenesPath;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorSceneSelectionScreen(ScreenManager screenManager, ResourcePathsConfig pathsConfig, boolean enableBackButton) {
		super(screenManager, pathsConfig, enableBackButton);

		final var layouts = mLayouts;
		final var numLayouts = layouts.size();
		for (int i = 0; i < numLayouts; i++) {
			final var layout = layouts.get(i);
			layout.setDrawBackground(true, ColorConstants.getColor(.7f, .3f, .7f, .75f));
			layout.layoutWidth(LAYOUT_WIDTH.HALF);
		}

	}

	@Override
	protected void finalizeScreenLayout(BaseLayout layout) {
		super.finalizeScreenLayout(layout);

		mCampaignMapsToggle = new MenuToggleEntry(screenManager, this, "Campaign Levels");
		mCampaignMapsToggle.registerClickListener(this, TOGGLE_ENTRY_CLICKED);
		mCampaignMapsToggle.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		// adjust components of parent screen
		mCreateNewTrack.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		mLoadTrack.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		if (mBackButton != null)
			mBackButton.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		mSceneFilenameEntries.horizontalFillType(FILLTYPE.FILL_CONTAINER);
		mSceneNameInput.horizontalFillType(FILLTYPE.FILL_CONTAINER);

		layout.addMenuEntry(mCampaignMapsToggle);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void onCreateNewScene() {
		final var newSceneName = sceneNameInput();
		if (newSceneName == null || newSceneName.length() == 0)
			return;

		final var sceneDirectory = getSceneDirectory();
		final var gameSceneHeader = SceneHeader.createNewSceneHeader(newSceneName, sceneDirectory);

		final var editorScreen = new EditorScreen(screenManager, gameSceneHeader);
		screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, true, true, editorScreen));

	}

	@Override
	protected void onLoadScene(MenuListBoxItem selectedItem) {
		if (!(selectedItem instanceof SceneMenuListItem selectedMenuItem)) {
			return;
		}

		final var editorScreen = new EditorScreen(screenManager, selectedMenuItem.sceneHeader());
		screenManager.initiateLoadingScreen(new LoadingScreen(screenManager, true, true, editorScreen));

	}

	@Override
	protected void populateDropDownListWithSceneFilenames(MenuListBox listBoxEntry, String scenesDirectory) {

		listBoxEntry.clearListBox();

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
			final var newItem = new SceneMenuListItem(screenManager, listBoxEntry, sceneHeader, entityGroupUid());
			newItem.labelValue("scene");
			newItem.textValue(sceneName);

			listBoxEntry.addEntry(newItem);
		}

		if (trackCount > 0)
			listBoxEntry.selectedIndex(0);

	}

	private String getSceneDirectory() {
		final var campaignMaps = mCampaignMapsToggle.isChecked();
		final var gameResourcePaths = mResourcePathsConfig;
		
		String scenesDirectory;
		if (campaignMaps) {
			scenesDirectory = gameResourcePaths.getKeyValue(SceneManager.SCENE_MAIN_DIRECTORY, ConstantsGame.CAMPAIGN_SCENES_DIRECTORY);

		} else {
			scenesDirectory = gameResourcePaths.getKeyValue(SceneManager.SCENE_CUSTOM_DIRECTORY, ConstantsGame.CUSTOM_SCENES_DIRECTORY);
		}
		return scenesDirectory;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case TOGGLE_ENTRY_CLICKED:
			populateDropDownListWithSceneFilenames(mSceneFilenameEntries, getSceneDirectory());

			// intermediate update to setup the positions before then next draw
			mSceneFilenameEntries.update(screenManager.core(), this);
			return;
		}

		super.handleOnClick();
	}

	@Override
	public void onListBoxItemSelected(MenuListBoxItem item, int itemIndex) {
		// ignore in base

	}

	@Override
	public void onListItemDoubleClicked(MenuListBoxItem listboxItem) {
		if (listboxItem != null)
			onLoadScene(listboxItem);

	}

}
