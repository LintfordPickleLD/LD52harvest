package lintfordpickle.harvest.renderers.scene.layers;

import lintfordpickle.harvest.data.scene.layers.LayersManager;
import lintfordpickle.harvest.data.scene.layers.SceneNoiseLayer;
import lintfordpickle.harvest.renderers.scene.NoiseLayerShader;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.geometry.FullScreenTexturedQuad;
import net.lintfordlib.renderers.RendererManagerBase;

public class SceneLayerNoiseRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private FullScreenTexturedQuad mTexturedQuad;
	private NoiseLayerShader mNoiseLayerShader;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneLayerNoiseRenderer(RendererManagerBase rendererManager, LayersManager layerManager, int entityGroupUid) {
		mTexturedQuad = new FullScreenTexturedQuad();
		mNoiseLayerShader = new NoiseLayerShader();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {

		mNoiseLayerShader.loadResources(resourceManager);
		mTexturedQuad.loadResources(resourceManager);
	}

	public void unload() {
		mNoiseLayerShader.unbind();
		mTexturedQuad.unloadResources();
	}

	public void draw(LintfordCore core, SceneNoiseLayer layer) {

		final var lDstX = layer.centerX;
		final var lDstY = layer.centerY;
		final var lDstWidth = layer.width;
		final var lDstHeight = layer.height;

//		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_R, this)) {
//			mNoiseLayerShader.recompile();
//		}

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
