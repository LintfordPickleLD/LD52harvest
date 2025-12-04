package lintfordpickle.harvest.renderers.editor;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.controllers.editor.EditorSceneController;
import lintfordpickle.harvest.controllers.layers.EditorNoiseLayerController;
import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneNoiseLayer;
import lintfordpickle.harvest.renderers.scene.NoiseLayerShader;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.geometry.FullScreenTexturedQuad;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.data.editor.EditorLayerBrush;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class EditorNoiseLayerRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Editor Noise Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private EditorSceneController mSceneController;

	private EditorNoiseLayerController mEditorNoiseLayerController;
	private EditorLayerController mEditorLayerController;
	private EditorBrushController mEditorBrushController;

	private FullScreenTexturedQuad mTexturedQuad;
	private NoiseLayerShader mNoiseLayerShader;

	private float mMouseX;
	private float mMouseY;

	private boolean mMouseDownLastFrame;
	private boolean mIsNewLeftClick;
	private float mMouseDownX;
	private float mMouseDownY;

	private boolean mRenderAnimations;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mSceneController != null;

	}

	public boolean renderAnimations() {
		return mRenderAnimations;
	}

	public void renderAnimations(boolean renderingEnabled) {
		mRenderAnimations = renderingEnabled;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorNoiseLayerRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mTexturedQuad = new FullScreenTexturedQuad();
		mNoiseLayerShader = new NoiseLayerShader();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();

		mSceneController = (EditorSceneController) lControllerManager.getControllerByNameRequired(EditorSceneController.CONTROLLER_NAME, entityGroupUid());
		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, entityGroupUid());
		mEditorLayerController = (EditorLayerController) lControllerManager.getControllerByNameRequired(EditorLayerController.CONTROLLER_NAME, entityGroupUid());
		mEditorNoiseLayerController = (EditorNoiseLayerController) lControllerManager.getControllerByNameRequired(EditorNoiseLayerController.CONTROLLER_NAME, entityGroupUid());
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mNoiseLayerShader.loadResources(resourceManager);
		mTexturedQuad.loadResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mNoiseLayerShader.unbind();
		mTexturedQuad.unloadResources();
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		var lInputHandled = super.handleInput(core);

		if (mEditorBrushController.isLayerActive(EditorLayersData.Layer_Noise) == false)
			return lInputHandled;

		final var leftMouseDown = core.input().mouse().tryAcquireMouseLeftClick(hashCode());
		mMouseX = core.gameCamera().getMouseWorldSpaceX();
		mMouseY = core.gameCamera().getMouseWorldSpaceY();

		// --- ACTIONS
		if (mEditorBrushController.brush().isActionSet() == false) {
			final var lSelectedLayer = mEditorLayerController.selectedLayer();
			final var lIsLayerSelected = lSelectedLayer != null;

			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_G, this)) {
				mIsNewLeftClick = false;

				if (lIsLayerSelected) {
					mEditorBrushController.setAction(EditorLayerController.ACTION_OBJECT_TRANSLATE_SELECTED_LAYER, "Translate Layer", hashCode());

					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;
				}
			} else if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_W, this)) {
				mIsNewLeftClick = false;

				if (lIsLayerSelected) {
					mEditorBrushController.setAction(EditorLayerController.ACTION_OBJECT_SCALE_SELECTED_LAYER_X, "Scale Width", hashCode());

					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;
				}
			} else if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_H, this)) {
				mIsNewLeftClick = false;

				if (lIsLayerSelected) {
					mEditorBrushController.setAction(EditorLayerController.ACTION_OBJECT_SCALE_SELECTED_LAYER_Y, "Scale Height", hashCode());

					mMouseDownX = mMouseX;
					mMouseDownY = mMouseY;
				}
			}
		}

		final int lCurrentBrushAction = mEditorBrushController.brush().brushActionUid();
		// --- END ACTIONS

		// ----

		if (leftMouseDown && !mMouseDownLastFrame) {
			mIsNewLeftClick = true;
			mMouseDownLastFrame = true;
		}

		if (lCurrentBrushAction != EditorLayerBrush.NO_ACTION_UID) {

			final var lSelectedLayer = mEditorLayerController.selectedLayer();

			// do something with mouse
			switch (lCurrentBrushAction) {
			case EditorLayerController.ACTION_OBJECT_TRANSLATE_SELECTED_LAYER: {
				final var lTranslationAmtX = mMouseX - mMouseDownX;
				final var lTranslationAmtY = mMouseY - mMouseDownY;

				mMouseDownX = mMouseX;
				mMouseDownY = mMouseY;

				lSelectedLayer.centerX += lTranslationAmtX;
				lSelectedLayer.centerY += lTranslationAmtY;
			}

				break;

			case EditorLayerController.ACTION_OBJECT_SCALE_SELECTED_LAYER_X: {
				final var lTranslationAmtX = mMouseX - mMouseDownX;

				mMouseDownX = mMouseX;
				mMouseDownY = mMouseY;

				lSelectedLayer.width += lTranslationAmtX;
				break;
			}

			case EditorLayerController.ACTION_OBJECT_SCALE_SELECTED_LAYER_Y: {
				final var lTranslationAmtY = mMouseY - mMouseDownY;

				mMouseDownX = mMouseX;
				mMouseDownY = mMouseY;

				lSelectedLayer.height += lTranslationAmtY;
				break;
			}

			}

			if (mIsNewLeftClick) {
				mEditorBrushController.clearAction(hashCode());
			}

		} else if (leftMouseDown) {
			// mEditorLayerController.selectedLayer(null);
		}

		// ----

		if (!leftMouseDown && !mIsNewLeftClick) {
			mMouseDownLastFrame = false;
			mIsNewLeftClick = false;
		}

		return lInputHandled;
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		final var lLayers = mEditorNoiseLayerController.noiseLayers();
		final var lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			final var lSceneLayer = lLayers.get(i);

			drawNoiseLayer(core, lSceneLayer);
		}

		final var lSelectedLayer = mEditorLayerController.selectedLayer();
		if (lSelectedLayer != null) {
			drawSelectedLayerDebug(core, lSelectedLayer);
		}
	}

	protected void drawNoiseLayer(LintfordCore core, SceneNoiseLayer layer) {

		// TODO: Camera offset scrolling ?

		final var lDstX = layer.centerX;
		final var lDstY = layer.centerY;
		final var lDstWidth = layer.width;
		final var lDstHeight = layer.height;

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_R, this)) {
			mNoiseLayerShader.recompile();
		}

		// TODO: Only needs updating when dirty
		layer.worldMatrix.setIdentity();
		layer.worldMatrix.translate(lDstX, lDstY, .0f);
		layer.worldMatrix.scale(lDstWidth, lDstHeight, 1.f);

		mNoiseLayerShader.modelMatrix(layer.worldMatrix);
		mNoiseLayerShader.viewMatrix(core.gameCamera().view());
		mNoiseLayerShader.projectionMatrix(core.gameCamera().projection());
		mNoiseLayerShader.bind();

		mNoiseLayerShader.update(core);

		mTexturedQuad.draw(core);

		mNoiseLayerShader.unbind();

		return;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawSelectedLayerDebug(LintfordCore core, SceneBaseLayer layer) {
		final var x = layer.centerX - layer.width * .5f;
		final var y = layer.centerY - layer.height * .5f;

		Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), x, y, layer.width, layer.height, 1f, 1f, 0f);
	}

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}
}
