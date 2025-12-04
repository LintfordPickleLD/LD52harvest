package lintfordpickle.harvest;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

import org.lwjgl.opengl.GL11;

import lintfordpickle.harvest.controllers.replays.ReplayController;
import lintfordpickle.harvest.data.players.PlayerManager;
import lintfordpickle.harvest.data.players.ReplayManager;
import lintfordpickle.harvest.screens.MainMenu;
import lintfordpickle.harvest.screens.editor.EditorSceneSelectionScreen;
import lintfordpickle.harvest.screens.game.TimeTrialGameScreen;
import lintfordpickle.harvest.screens.menu.MenuBackgroundScreen;
import net.lintfordlib.GameInfo;
import net.lintfordlib.GameVersion;
import net.lintfordlib.assets.ResourceLoader;
import net.lintfordlib.controllers.music.MusicController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug.DebugLogLevel;
import net.lintfordlib.core.graphics.fonts.BitmapFontManager;
import net.lintfordlib.core.input.ActionManager;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.data.scene.SceneManager;
import net.lintfordlib.options.ResourcePathsConfig;
import net.lintfordlib.screenmanager.IMenuScreenCallbacks;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.screens.TimedIntroScreen;
import net.lintfordlib.screenmanager.toast.ToastManager;

public class GameWindow extends LintfordCore {

	private final int APP_VERSION_MAJ = 0;
	private final int APP_VERSION_MIN = 1;
	private final int APP_VERSION_BUILD = 1;
	private final String APP_POSTFIX = "10042023";

	// ---------------------------------------------
	// Entry Point
	// ---------------------------------------------

	public static void main(String[] args) {

		final var lGameInfo = new GameInfo() {
			@Override
			public DebugLogLevel debugLogLevel() {
				return DebugLogLevel.off;
			}

			@Override
			public String applicationName() {
				return ConstantsGame.APPLICATION_NAME;
			}

			@Override
			public String windowTitle() {
				return ConstantsGame.WINDOW_TITLE;
			}

			@Override
			public int minimumWindowWidth() {
				return ConstantsGame.GAME_CANVAS_WIDTH;
			}

			@Override
			public int minimumWindowHeight() {
				return ConstantsGame.GAME_CANVAS_HEIGHT;
			}

			@Override
			public int gameCanvasResolutionWidth() {
				return ConstantsGame.GAME_CANVAS_WIDTH;
			}

			@Override
			public int gameCanvasResolutionHeight() {
				return ConstantsGame.GAME_CANVAS_HEIGHT;
			}

			@Override
			public boolean stretchGameResolution() {
				return true;
			}

			@Override
			public boolean windowResizeable() {
				return true;
			}
		};

		final var lClient = new GameWindow(lGameInfo, args);
		lClient.createWindow();
	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected int mEntityGroupID;

	protected ResourceLoader mGameResourceLoader;
	protected ScreenManager mScreenManager;
	protected SceneManager mSceneManager;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ScreenManager screenManager() {
		return mScreenManager;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameWindow(GameInfo pGameInfo, String[] pArgs) {
		super(pGameInfo, pArgs, false);

		setGameVersion();

		mEntityGroupID = RandomNumbers.RANDOM.nextInt();
		mIsFixedTimeStep = true;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected void showStartUpLogo(long pWindowHandle) {
		// glClearColor(0f, 0f, 0f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		glfwSwapBuffers(pWindowHandle);
	}

	@Override
	protected void onInitializeBitmapFontSources(BitmapFontManager fontManager) {
		super.onInitializeBitmapFontSources(fontManager);

		ScreenManager.ScreenManagerFonts.AddOrUpdate(ScreenManager.FONT_MENU_TOOLTIP_NAME, "res/fonts/fontNulshock12.json");
		ScreenManager.ScreenManagerFonts.AddOrUpdate(ScreenManager.FONT_MENU_ENTRY_NAME, "res/fonts/fontNulshock16.json");
		ScreenManager.ScreenManagerFonts.AddOrUpdate(ScreenManager.FONT_MENU_BOLD_ENTRY_NAME, "res/fonts/fontNulshock16.json");
		ScreenManager.ScreenManagerFonts.AddOrUpdate(ScreenManager.FONT_MENU_TITLE_NAME, "res/fonts/fontNulshock22.json");

		ScreenManager.ScreenManagerFonts.AddOrUpdate(ToastManager.FONT_TOAST_NAME, "res/fonts/fontNulshock16.json");

		SharedResources.RendererManagerFonts.AddOrUpdate(SharedResources.HUD_FONT_TEXT_BOLD_SMALL_NAME, "res/fonts/fontBarlow14.json");

		SharedResources.RendererManagerFonts.AddOrUpdate(SharedResources.UI_FONT_TEXT_NAME, "res/fonts/fontBarlow14.json");
		SharedResources.RendererManagerFonts.AddOrUpdate(SharedResources.UI_FONT_TEXT_BOLD_NAME, "res/fonts/fontBarlow14.json");
		SharedResources.RendererManagerFonts.AddOrUpdate(SharedResources.UI_FONT_HEADER_NAME, "res/fonts/fontNulshock16.json");
		SharedResources.RendererManagerFonts.AddOrUpdate(SharedResources.UI_FONT_TITLE_NAME, "res/fonts/fontNulshock22.json");
	}

	@Override
	protected void onInitializeApp() {
		super.onInitializeApp();

		mScreenManager = new ScreenManager(this);
		mScreenManager.initialize();

		mSceneManager = new SceneManager(mDataManager, mMasterConfig.resourcePaths(), ConstantsGame.GAME_RESOURCE_GROUP_ID);
	}

	@Override
	protected void onInitializePaths(ResourcePathsConfig pathsConfig) {
		super.onInitializePaths(pathsConfig);

		pathsConfig.insertOrUpdateValue(SceneManager.SCENE_DIRECTORY, "res/def/scenes/");
	}

	@Override
	protected void onInitializeInputActions(ActionManager actionManager) {
		actionManager.addGameActions(new GameActions());

		super.onInitializeInputActions(actionManager);
	}

	@Override
	protected void onLoadResources() {
		super.onLoadResources();

		mGameResourceLoader = new HarvestResourceLoader(mResourceManager, config().display(), ConstantsGame.GAME_RESOURCE_GROUP_ID);

		mGameResourceLoader.loadResources(mResourceManager);
		mGameResourceLoader.setMinimumTimeToShowLogosMs(ConstantsGame.IS_DEBUG_MODE ? 0 : 2000);
		mGameResourceLoader.loadResourcesInBackground(this);

		mResourceManager.audioManager().loadAudioFilesFromMetafile("res/audio/_meta.json");
		mResourceManager.musicManager().loadMusicFromMetaFile("res/music/meta.json");

		var lMusic = new MusicController(mControllerManager, mResourceManager.musicManager(), LintfordCore.CORE_ENTITY_GROUP_ID);
		lMusic.playFromGroup(0, "menu");

		mScreenManager.loadResources(mResourceManager);
	}

	@Override
	protected void finializeAppSetup() {
		final var lBestReplayManager = new ReplayManager();
		final var lReplayController = new ReplayController(mControllerManager, lBestReplayManager, ConstantsGame.GAME_RESOURCE_GROUP_ID);
		lReplayController.initialize(this);

		if (ConstantsGame.QUICK_LAUNCH_EDITOR) {
			mScreenManager.addScreen(new EditorSceneSelectionScreen(screenManager(), config().resourcePaths(), false));
			// mScreenManager.addScreen(new EditorScreen(screenManager()));
			return;
		}

		if (ConstantsGame.QUICK_LAUNCH_GAME) {
			final var lPlayerManager = new PlayerManager();
			final var lGhostPlayer = lPlayerManager.addNewPlayer();
			lGhostPlayer.setPlayback("ghost.lms");

			// TODO: pick a valid scene to load
			final var lSceneHeader = mSceneManager.loadSceneHeader("level1");
			mScreenManager.addScreen(new TimeTrialGameScreen(screenManager(), lSceneHeader, lPlayerManager));
			return;
		}

		final var splashScreen = new TimedIntroScreen(mScreenManager, "res/textures/textureSplashGame.png");
		splashScreen.stretchBackgroundToFit(true);

		splashScreen.setTimerFinishedCallback(new IMenuScreenCallbacks() {
			@Override
			public void TimerFinished(Screen pScreen) {
				mScreenManager.addScreen(new MenuBackgroundScreen(mScreenManager));
				mScreenManager.addScreen(new MainMenu(mScreenManager));
			}
		});

		mScreenManager.addScreen(splashScreen);
	}

	@Override
	protected void onUnloadResources() {
		super.onUnloadResources();

		mScreenManager.unloadResources();
	}

	@Override
	protected void onHandleInput() {
		super.onHandleInput();

		gameCamera().handleInput(this);
		mScreenManager.handleInput(this);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();

		mScreenManager.update(this);
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		mScreenManager.draw(this);
	}

	private void setGameVersion() {
		GameVersion.setGameVersion(APP_VERSION_MAJ, APP_VERSION_MIN, APP_VERSION_BUILD, APP_POSTFIX);
	}

	@Override
	public Class<?> getMainClass() {
		return GameWindow.class;
	}

}
