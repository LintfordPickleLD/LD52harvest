package lintfordpickle.harvest.renderers.scene;

import lintfordpickle.harvest.controllers.LayerController;
import lintfordpickle.harvest.data.scene.layers.SceneNoiseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import lintfordpickle.harvest.data.scene.layers.SceneTextureLayer;
import lintfordpickle.harvest.renderers.scene.layers.SceneLayerNoiseRenderer;
import lintfordpickle.harvest.renderers.scene.layers.SceneLayerSpriteRenderer;
import lintfordpickle.harvest.renderers.scene.layers.SceneLayerTextureRenderer;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class SceneRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Scene Renderer";

	public static final int WORLD_WIDTH_IN_PX = 1024; // TODO: This has to be removed into a data file.

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private LayerController mLayerController;

	private SceneLayerTextureRenderer mSceneLayerTextureRenderer;
	private SceneLayerSpriteRenderer mSceneLayerSpriteRenderer;
	private SceneLayerNoiseRenderer mSceneLayerNoiseRenderer;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mLayerController != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SceneRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		mLayerController = (LayerController) core.controllerManager().getControllerByNameRequired(LayerController.CONTROLLER_NAME, entityGroupUid());

		final var layerManager = mLayerController.layerManager();
		mSceneLayerTextureRenderer = new SceneLayerTextureRenderer(rendererManager(), layerManager, entityGroupUid());
		mSceneLayerSpriteRenderer = new SceneLayerSpriteRenderer(rendererManager(), layerManager, entityGroupUid());
		mSceneLayerNoiseRenderer = new SceneLayerNoiseRenderer(rendererManager(), layerManager, entityGroupUid());
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mSceneLayerSpriteRenderer.loadResources(resourceManager);

		mSceneLayerTextureRenderer.loadResources(resourceManager);
		mSceneLayerSpriteRenderer.loadResources(resourceManager);
		mSceneLayerNoiseRenderer.loadResources(resourceManager);

	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mSceneLayerTextureRenderer.unload();
		mSceneLayerSpriteRenderer.unload();
		mSceneLayerNoiseRenderer.unload();
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		final var layersManager = mLayerController.layerManager();
		final var layers = layersManager.layers();
		final var numLayers = layers.size();
		for (int i = 0; i < numLayers; i++) {
			final var lSceneLayer = layers.get(i);

			switch (lSceneLayer) {
			case SceneNoiseLayer noiseLayer -> mSceneLayerNoiseRenderer.draw(core, (SceneNoiseLayer) noiseLayer);
			case SceneTextureLayer textureLayer -> mSceneLayerTextureRenderer.draw(core, (SceneTextureLayer) textureLayer);
			case SceneSpriteLayer spriteLayer -> mSceneLayerSpriteRenderer.draw(core, (SceneSpriteLayer) spriteLayer);
			default -> {
				/* ignore */ }

			}
		}
	}

}
