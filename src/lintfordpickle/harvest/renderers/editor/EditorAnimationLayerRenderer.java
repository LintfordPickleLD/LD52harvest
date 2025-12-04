package lintfordpickle.harvest.renderers.editor;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.controllers.editor.EditorSceneController;
import lintfordpickle.harvest.controllers.layers.EditorAnimationLayerController;
import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.scene.layers.SceneAnimationLayer;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.data.editor.EditorLayerBrush;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManagerBase;

public class EditorAnimationLayerRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Editor Animation Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private EditorSceneController mSceneController;
	private ResourceManager mResourceManager;

	private EditorAnimationLayerController mEditorAnimationLayerController;
	private EditorLayerController mEditorLayerController;
	private EditorBrushController mEditorBrushController;

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

	public EditorAnimationLayerRenderer(RendererManagerBase rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

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
		mEditorAnimationLayerController = (EditorAnimationLayerController) lControllerManager.getControllerByNameRequired(EditorAnimationLayerController.CONTROLLER_NAME, entityGroupUid());
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mResourceManager = resourceManager;
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mResourceManager = null;
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		var lInputHandled = super.handleInput(core);

		if (mEditorBrushController.isLayerActive(EditorLayersData.Layer_Animation) == false)
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
	public void update(LintfordCore core) {
		super.update(core);

		final var lLayersManager = mSceneController.sceneData().layersManager();
		final var lLayers = lLayersManager.layers();
		final var lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			final var lSceneLayer = lLayers.get(i);
			if (lSceneLayer instanceof SceneAnimationLayer) {
				var lAnimationSceneLayer = (SceneAnimationLayer) lSceneLayer;

				final var lLayerAnimations = lAnimationSceneLayer.spriteAssets();
				final var lNumAnimations = lLayerAnimations.size();
				for (int j = 0; j < lNumAnimations; j++) {
					final var lAsset = lLayerAnimations.get(j);

					// TODO: rework this
					if (lAsset.spriteInstance == null) {
						if (lAsset.spriteStatus < 2) {
							final var lAssetDef = lAsset.definition;
//							final var lSpritesheetDef = core.resources().spriteSheetManager().getSpriteSheet(lAssetDef.spritesheetDefinitionName, mEntityGroupUid);
//							if (lSpritesheetDef != null) {
//								lAsset.spriteInstance = lSpritesheetDef.getSpriteInstance(lAssetDef.spriteName);
//							} else {
//								lAsset.spriteStatus = SceneAssetInstance.TEXTURE_FAILED;
//							}
						}

						continue;
					}

					lAsset.spriteInstance.update(core);
				}
			}
		}
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		final var lLayers = mEditorAnimationLayerController.animationLayers();

		final var lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			final var lSceneLayer = lLayers.get(i);

			drawAnimationLayer(core, (SceneAnimationLayer) lSceneLayer);
		}

		final var lSelectedLayer = mEditorLayerController.selectedLayer();
		if (lSelectedLayer != null) {
			drawSelectedLayerDebug(core, lSelectedLayer);
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	protected void drawAnimationLayer(LintfordCore core, SceneAnimationLayer layer) {
		final var lSpriteBatch = core.sharedResources().uiSpriteBatch();

		lSpriteBatch.begin(core.gameCamera());

		final var lLayerAnimations = layer.spriteAssets();
		final var lNumAnimations = lLayerAnimations.size();
		for (int i = 0; i < lNumAnimations; i++) {
			final var lAssetInstance = lLayerAnimations.get(i);

			lSpriteBatch.begin(core.gameCamera());
			Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), lAssetInstance.destRect);
			// lSpriteBatch.draw(layer.texture, lSrcX, lSrcY, lSrcW, lSrcH, lDstX, lDstY, lDstWidth, lDstHeight, -.01f, ColorConstants.WHITE);
			lSpriteBatch.end();

			// lSpriteBatch.draw(mPropsSpritesheetDefintion, lSprite, lSprite, 2.f, -0.01f, ColorConstants.WHITE);
		}

		lSpriteBatch.end();
	}

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
