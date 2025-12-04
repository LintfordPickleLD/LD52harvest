package lintfordpickle.harvest.screens.editor;

import java.io.File;

import lintfordpickle.harvest.ConstantsGame;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;
import net.lintfordlib.data.scene.SceneHeader;
import net.lintfordlib.data.scene.SceneManager;
import net.lintfordlib.options.ResourcePathsConfig;
import net.lintfordlib.screenmanager.BaseEditorSceneSelectionScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.entries.MenuListBox;
import net.lintfordlib.screenmanager.entries.MenuListBoxItem;
import net.lintfordlib.screenmanager.entries.listboxitems.LabelValueListBoxItem;
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

		}

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private SceneManager mSceneManager;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EditorSceneSelectionScreen(ScreenManager screenManager, ResourcePathsConfig pathsConfig, boolean enableBackButton) {
		super(screenManager, pathsConfig, enableBackButton);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final var dataManager = screenManager.core().dataManager();
		mSceneManager = (SceneManager) dataManager.getDataManagerByName(SceneManager.DATA_MANAGER_NAME, ConstantsGame.GAME_RESOURCE_GROUP_ID);

	}

	@Override
	protected void onCreateNewScene() {
		// TODO: Need to name the level before we start (but we also need to be able to rename them too!)

		final var editorScreen = new EditorScreen(screenManager, mSceneManager.createSceneHeader("unnamed"));
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
	protected void populateDropDownListWithSceneFilenames(MenuListBox listBoxEntry) {
		final var gameResourcePaths = mResourcePathsConfig;

		final var baseScenesPath = gameResourcePaths.getKeyValue(SceneManager.SCENE_DIRECTORY, "res/scenes");

		final var listOfTracks = FileUtils.getListOfFilesInSubDirectories(baseScenesPath, ".hdr");
		final var trackCount = listOfTracks.size();

		for (var i = 0; i < trackCount; i++) {
			final var sceneHeaderFileName = listOfTracks.get(i);
			final var sceneHeaderFile = new File(sceneHeaderFileName.toString());

			if (!sceneHeaderFile.exists()) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "/-/-/-/-/-/-/-/-/-/-/-/-");
				continue;
			}

			// need the load the fucker so we know its valid
			final var sceneHeader = SceneHeader.loadSceneHeaderFileFromFilepath(sceneHeaderFile.toString());

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

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

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
