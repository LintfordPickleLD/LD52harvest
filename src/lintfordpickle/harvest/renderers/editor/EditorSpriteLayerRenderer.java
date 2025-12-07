package lintfordpickle.harvest.renderers.editor;

import org.lwjgl.glfw.GLFW;

import lintfordpickle.harvest.controllers.editor.EditorLayerController;
import lintfordpickle.harvest.controllers.editor.EditorSceneController;
import lintfordpickle.harvest.data.assets.SceneSpriteInstance;
import lintfordpickle.harvest.data.editor.EditorLayersData;
import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneSpriteLayer;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.data.editor.EditorLayerBrush;

public class EditorSpriteLayerRenderer implements IInputProcessor {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Editor Sprite Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private EditorSceneController mSceneController;

	private EditorLayerController mEditorLayerController;
	private EditorBrushController mEditorBrushController;

	private ResourceManager mResourceManager;

	private float mMouseX;
	private float mMouseY;

	private boolean mMouseDownLastFrame;
	private boolean mIsNewLeftClick;
	private float mMouseDownX;
	private float mMouseDownY;

	private boolean mRenderSpriteLayer;
	private int mEntityGroupUid;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean renderSpritesLayer() {
		return mRenderSpriteLayer;
	}

	public void renderSpritesLayer(boolean renderingEnabled) {
		mRenderSpriteLayer = renderingEnabled;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorSpriteLayerRenderer(int entityGroupID) {
		mEntityGroupUid = entityGroupID;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();

		mSceneController = (EditorSceneController) lControllerManager.getControllerByNameRequired(EditorSceneController.CONTROLLER_NAME, mEntityGroupUid);
		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, mEntityGroupUid);
		mEditorLayerController = (EditorLayerController) lControllerManager.getControllerByNameRequired(EditorLayerController.CONTROLLER_NAME, mEntityGroupUid);
	}

	public void loadResources(ResourceManager resourceManager) {
		mResourceManager = resourceManager;

	}

	public void unloadResources() {

	}

	public boolean handleInput(LintfordCore core) {
		if (!mEditorBrushController.isLayerActive(EditorLayersData.Layer_Animation))
			return false;

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

		return false;
	}

	public void update(LintfordCore core) {

		final var lLayersManager = mSceneController.sceneData().layersManager();
		final var lLayers = lLayersManager.layers();
		final var lNumLayers = lLayers.size();
		for (int i = 0; i < lNumLayers; i++) {
			final var lSceneLayer = lLayers.get(i);
			if (lSceneLayer instanceof SceneSpriteLayer) {
				var lAnimationSceneLayer = (SceneSpriteLayer) lSceneLayer;

				final var lLayerAnimations = lAnimationSceneLayer.spriteAssets();
				final var lNumAnimations = lLayerAnimations.size();
				for (int j = 0; j < lNumAnimations; j++) {
					final var lAsset = lLayerAnimations.get(j);

					// TODO: rework this
					if (lAsset.spriteInstance == null) {
//						if (lAsset.spriteStatus < 2) {
//							final var lAssetDef = lAsset.definition;
//							final var lSpritesheetDef = core.resources().spriteSheetManager().getSpriteSheet(lAssetDef.spritesheetDefinitionName, mEntityGroupUid);
//							if (lSpritesheetDef != null) {
//								lAsset.spriteInstance = lSpritesheetDef.getSpriteInstance(lAssetDef.spriteName);
//							} else {
//								lAsset.spriteStatus = SceneAssetInstance.TEXTURE_FAILED;
//							}
//						}

						continue;
					}

					lAsset.spriteInstance.update(core);
				}
			}
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void drawSpriteLayer(LintfordCore core, SceneSpriteLayer layer) {
		if (!mRenderSpriteLayer)
			return;

		final var spriteBatch = core.sharedResources().uiSpriteBatch();

		spriteBatch.begin(core.gameCamera());

		final var layerAnimations = layer.spriteAssets();
		final var numAnimations = layerAnimations.size();
		for (int i = 0; i < numAnimations; i++) {
			final var assetInstance = layerAnimations.get(i);

			if (!validateSpriteSheetDefinition(assetInstance))
				continue;

			if (!validateSpriteInstance(assetInstance))
				continue;

			spriteBatch.begin(core.gameCamera());
			Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), assetInstance.destRect);
			spriteBatch.draw(assetInstance.spriteSheetDefinition, assetInstance.spriteInstance, .01f);
			spriteBatch.end();
		}

		spriteBatch.end();
	}

	private boolean validateSpriteSheetDefinition(SceneSpriteInstance assetInstance) {
		if (assetInstance.spriteSheetStatus == SceneSpriteInstance.STATUS_LOADED)
			return true;

		if (assetInstance.spriteSheetStatus == SceneSpriteInstance.STATUS_FAILED)
			return false;

		final var assetDefinition = assetInstance.definition;
		final var spriteAssetDefintion = assetDefinition.sceneSpriteContainer;

		assetInstance.spriteSheetDefinition = mResourceManager.spriteSheetManager().loadSpriteSheet(spriteAssetDefintion.spritesheetDefinitionName, spriteAssetDefintion.spritesheetDefinitionFilename, mEntityGroupUid);
		if (assetInstance.spriteSheetDefinition == null) {
			assetInstance.spriteSheetStatus = SceneSpriteInstance.STATUS_FAILED;
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to resolve SpriteAsset '" + assetDefinition.name + "' - SpriteSheetDefinition: " + spriteAssetDefintion.spritesheetDefinitionFilename);
			return false;
		}

		assetInstance.spriteSheetStatus = SceneSpriteInstance.STATUS_LOADED;
		return true;
	}

	private boolean validateSpriteInstance(SceneSpriteInstance assetInstance) {
		if (assetInstance.spriteInstStatus == SceneSpriteInstance.STATUS_LOADED)
			return true;

		if (assetInstance.spriteInstStatus == SceneSpriteInstance.STATUS_FAILED)
			return false;

		final var assetDefinition = assetInstance.definition;
		final var spriteAssetDefintion = assetDefinition.sceneSpriteContainer;
		final var assetSpriteSheetDefinition = assetInstance.spriteSheetDefinition;

		assetInstance.spriteInstance = assetSpriteSheetDefinition.getSpriteInstance(spriteAssetDefintion.spriteName);
		if (assetInstance.spriteInstance == null) {
			assetInstance.spriteInstStatus = SceneSpriteInstance.STATUS_FAILED;
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to create sprite instance '" + assetDefinition.name + "' - SpriteSheetDefinition: " + spriteAssetDefintion.spriteName);
			return false;
		}

		assetInstance.spriteInstStatus = SceneSpriteInstance.STATUS_LOADED;
		return true;
	}

	public void drawSelectedLayerDebug(LintfordCore core, SceneBaseLayer layer) {
		final var x = layer.centerX - layer.width * .5f;
		final var y = layer.centerY - layer.height * .5f;

		Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), x, y, layer.width, layer.height, 1f, 1f, 0f);
	}

	// ---------------------------------------------
	// Inherited-Methods
	// ---------------------------------------------

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public boolean isCoolDownElapsed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean allowGamepadInput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowMouseInput() {
		// TODO Auto-generated method stub
		return false;
	}

}
