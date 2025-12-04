package lintfordpickle.harvest.screens.menu;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuHelpScreen extends MenuScreen {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Texture mHelpTexture;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public MenuHelpScreen(ScreenManager screenManager) {
		super(screenManager, "");

		mShowContextualFooterBar = false;

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mHelpTexture = resourceManager.textureManager().loadTexture("TEXTURE_MENU_HELP", "res/textures/textureHelpOverlay.png", entityGroupUid());
	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);

		GL11.glClearColor(0.06f, 0.18f, 0.11f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		final var hudBoundingBox = core.HUD().boundingRectangle();
		final var spriteBatch = core.sharedResources().uiSpriteBatch();

		spriteBatch.setColorWhite();
		spriteBatch.begin(core.HUD());
		spriteBatch.draw(mHelpTexture, 0, 0, 960, 540, hudBoundingBox.left(), hudBoundingBox.top(), hudBoundingBox.width(), hudBoundingBox.height(), .01f);
		spriteBatch.end();
	}

	@Override
	protected void handleOnClick() {
		// TODO Auto-generated method stub

	}
}
