package lintfordpickle.harvest.data.scene.layers.savedefinitions;

import lintfordpickle.harvest.data.scene.layers.SceneBaseLayer;
import lintfordpickle.harvest.data.scene.layers.SceneTextureLayer;
import net.lintfordlib.core.graphics.textures.Texture;

public class SceneTextureLayerSaveDefinition extends BaseSceneLayerSaveDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7569238335511621943L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public Texture texture;
	public String textureName;
	public String texturePath;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneTextureLayerSaveDefinition() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public SceneBaseLayer getSceneLayer() {
		final var textureLayer = new SceneTextureLayer(layerUid);
		textureLayer.zInvDepth = layerZInvDepth;
		textureLayer.name = layerName;

		textureLayer.setTextureName(textureName);
		textureLayer.setTextureFilepath(texturePath);

		textureLayer.translationSpeedModX = translationSpeedModX;
		textureLayer.translationSpeedModY = translationSpeedModY;

		textureLayer.centerX = centerX;
		textureLayer.centerY = centerY;

		textureLayer.width = width;
		textureLayer.height = height;

		// ensure some sane defaults:
		if (textureLayer.width <= 0.f)
			textureLayer.width = 32.f;

		if (textureLayer.height <= 0.f)
			textureLayer.height = 32.f;

		return textureLayer;
	}
}
