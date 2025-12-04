package lintfordpickle.harvest.renderers.editor;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.editor.EditorPhysicsSettingsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.linebatch.LineBatch;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class EditorPhysicsSettingsRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Editor Physics Settings Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected EditorPhysicsSettingsController mPhysicsSettingsController;
	protected LineBatch mLineBatch;
	private boolean mRenderHashGrid;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean renderHashGrid() {
		return mRenderHashGrid;
	}

	public void renderHashGrid(boolean newValue) {
		mRenderHashGrid = newValue;
	}

	@Override
	public boolean isInitialized() {
		return mPhysicsSettingsController != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorPhysicsSettingsRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mLineBatch = new LineBatch();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();
		mPhysicsSettingsController = (EditorPhysicsSettingsController) lControllerManager.getControllerByNameRequired(EditorPhysicsSettingsController.CONTROLLER_NAME, mEntityGroupUid);
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mLineBatch.loadResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mLineBatch.unloadResources();
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		if (mRenderHashGrid) {
			drawSpatialHashGridGrid(core);
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawSpatialHashGridGrid(LintfordCore core) {
		final var lPhysicsSettings = mPhysicsSettingsController.physicsSettings();
		final var lToPixels = ConstantsPhysics.UnitsToPixels();

		final float mBoundaryWidth = lPhysicsSettings.hashGridWidthInUnits * lToPixels;
		final float mBoundaryHeight = lPhysicsSettings.hashGridHeightInUnits * lToPixels;

		final float lHalfBW = mBoundaryWidth / 2.f;
		final float lHalfBH = mBoundaryHeight / 2.f;

		final var mNumTilesWide = lPhysicsSettings.hashGridCellsWide;
		final var mNumTilesHigh = lPhysicsSettings.hashGridCellsHigh;

		final float lTileSizeW = mBoundaryWidth / (float) mNumTilesWide;
		final float lTileSizeH = mBoundaryHeight / (float) mNumTilesHigh;

		mLineBatch.lineType(GL11.GL_LINES);
		mLineBatch.begin(core.gameCamera());

		for (int xx = 0; xx < mNumTilesWide; xx++) {
			mLineBatch.draw(-lHalfBW + (xx * lTileSizeW), -lHalfBH, -lHalfBW + (xx * lTileSizeW), lHalfBH, -0.01f, 1f, 0f, 0f, .5f);

			for (int yy = 0; yy < mNumTilesHigh; yy++) {
				mLineBatch.draw(-lHalfBW, -lHalfBH + (yy * lTileSizeH), lHalfBW, -lHalfBH + (yy * lTileSizeH), -0.01f, 1f, 1f, 0f, 1.0f);
			}
		}

		mLineBatch.end();
	}
}
