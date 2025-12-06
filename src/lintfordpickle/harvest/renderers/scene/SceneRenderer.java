package lintfordpickle.harvest.renderers.scene;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.controllers.SceneController;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import lintfordpickle.harvest.data.scene.layers.SceneNoiseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneTextureLayer;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.geometry.FullScreenTexturedQuad;
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

	protected SceneController mSceneController;

	private FullScreenTexturedQuad mTexturedQuad;
	private NoiseLayerShader mNoiseLayerShader;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mSceneController != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SceneRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mTexturedQuad = new FullScreenTexturedQuad();
		mNoiseLayerShader = new NoiseLayerShader();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		mSceneController = (SceneController) core.controllerManager().getControllerByNameRequired(SceneController.CONTROLLER_NAME, entityGroupUid());
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		final var lLayersManager = mSceneController.sceneData().layersManager();
		final var lLayers = lLayersManager.layers();
		final var lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			final var lSceneLayer = lLayers.get(i);

			if (lSceneLayer instanceof SceneTextureLayer) {
				final var layer = (SceneTextureLayer) lSceneLayer;
				if (layer.textureStatus == SceneTextureLayer.TEXTURE_UNLOADED) {
					layer.texture = resourceManager.textureManager().loadTexture(layer.textureName(), layer.textureFilepath(), entityGroupUid());
					if (resourceManager.textureManager().textureNotFound().equals(layer.texture)) {
						layer.texture = null;

						return;
					}

					if (layer.texture == null) {
						layer.textureStatus = SceneTextureLayer.TEXTURE_FAILED;
					}
				}
			}

			// TODO: animated textures

			mNoiseLayerShader.loadResources(resourceManager);
			mTexturedQuad.loadResources(resourceManager);

		}
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		// TODO: Unload the resources used by the scene

		mNoiseLayerShader.unbind();
		mTexturedQuad.unloadResources();
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		// TODO: Check out the EditorSceneRenderer
//		final var lLayersManager = mSceneController.sceneData().layersManager();
//		final var lLayers = lLayersManager.layers();
//		final var lNumLayers = lLayers.size();
//		for (int i = 0; i < lNumLayers; i++) {
//			final var lSceneLayer = lLayers.get(i);
//			if (lSceneLayer instanceof SceneAnimationLayer) {
//				var lAnimationSceneLayer = (SceneAnimationLayer) lSceneLayer;
//
//				final var lLayerAnimations = lAnimationSceneLayer.spriteAssets();
//				final var lNumAnimations = lLayerAnimations.size();
//				for (int j = 0; j < lNumAnimations; j++) {
//					lLayerAnimations.get(j).update(core);
//				}
//			}
//		}
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		final var layersManager = mSceneController.sceneData().layersManager();
		final var layers = layersManager.layers();
		final var numLayers = layers.size();
		for (int i = 0; i < numLayers; i++) {
			final var lSceneLayer = layers.get(i);

			if (lSceneLayer instanceof SceneTextureLayer) {
				drawTextureLayer(core, (SceneTextureLayer) lSceneLayer);
				continue;
			}

			if (lSceneLayer instanceof SceneSpriteLayer) {
				drawAnimationLayer(core, (SceneSpriteLayer) lSceneLayer);
				continue;
			}

			if (lSceneLayer instanceof SceneNoiseLayer) {
				drawNoiseLayer(core, (SceneNoiseLayer) lSceneLayer);
				continue;
			}
		}
	}

	protected void drawTextureLayer(LintfordCore core, SceneTextureLayer layer) {
		final var spriteBatch = mRendererManager.sharedResources().uiSpriteBatch();

		final var aabb_c = core.gameCamera().boundingRectangle();
		final var cameraPositionX = aabb_c.centerX();
		final var cameraPositionY = aabb_c.centerY();

		if (layer.texture != null) {
			final var lCamOffsetX = -layer.centerX - cameraPositionX * layer.translationSpeedModX;
			final var lCamOffsetY = -layer.centerY - cameraPositionY * layer.translationSpeedModY;

			final var lSrcX = lCamOffsetX;
			final var lSrcY = lCamOffsetY;
			final var lSrcW = layer.texture.getTextureWidth();
			final var lSrcH = layer.texture.getTextureHeight();

			final var lDstX = layer.centerX - layer.width * .5f;
			final var lDstY = layer.centerY - layer.height * .5f;
			final var lDstWidth = layer.width;
			final var lDstHeight = layer.height;

			spriteBatch.setColorWhite();
			spriteBatch.begin(core.gameCamera());
			spriteBatch.draw(layer.texture, lSrcX, lSrcY, lSrcW, lSrcH, lDstX, lDstY, lDstWidth, lDstHeight, 9 - layer.zDepth);
			spriteBatch.end();
			return;
		}
	}

	protected void drawAnimationLayer(LintfordCore core, SceneSpriteLayer layer) {
		final var spriteBatch = mRendererManager.sharedResources().uiSpriteBatch();

		spriteBatch.setColorWhite();
		spriteBatch.begin(core.gameCamera());

		final var lLayerAnimations = layer.spriteAssets();
		final var lNumAnimations = lLayerAnimations.size();
		for (int i = 0; i < lNumAnimations; i++) {
			// final var lSprite = lLayerAnimations.get(i);
			// spriteBatch.draw(mPropsSpritesheetDefintion, lSprite, lSprite, 2.f, -0.01f, ColorConstants.WHITE);
		}

		spriteBatch.end();
	}

	protected void drawNoiseLayer(LintfordCore core, SceneNoiseLayer layer) {

		final var lDstX = layer.centerX;
		final var lDstY = layer.centerY;
		final var lDstWidth = layer.width;
		final var lDstHeight = layer.height;

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_R, this)) {
			mNoiseLayerShader.recompile();
		}

		// TODO: Only needs updating when dirty
		layer.worldMatrix.setIdentity();
		layer.worldMatrix.translate(lDstX, lDstY, 4.5f);
		layer.worldMatrix.scale(lDstWidth, lDstHeight, 1);

		mNoiseLayerShader.modelMatrix(layer.worldMatrix);
		mNoiseLayerShader.viewMatrix(core.gameCamera().view());
		mNoiseLayerShader.projectionMatrix(core.gameCamera().projection());
		mNoiseLayerShader.bind();

		mNoiseLayerShader.update(core);

		mTexturedQuad.draw(core);

		mNoiseLayerShader.unbind();

	}

}
