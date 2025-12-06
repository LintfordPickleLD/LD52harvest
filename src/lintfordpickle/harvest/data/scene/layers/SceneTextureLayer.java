package lintfordpickle.harvest.data.scene.layers;

import lintfordpickle.harvest.data.scene.layers.savedefinitions.BaseSceneLayerSaveDefinition;
import lintfordpickle.harvest.data.scene.layers.savedefinitions.SceneTextureLayerSaveDefinition;
import net.lintfordlib.core.graphics.textures.Texture;

public class SceneTextureLayer extends SceneBaseLayer {

	public static final int TEXTURE_UNLOADED = 0;
	public static final int TEXTURE_LOADED = 1;
	public static final int TEXTURE_FAILED = 2;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public Texture texture;
	private String mTextureName;
	private String mTexturePath;

	private boolean mIsScreenSpace;

	// 0 unloaded, 1 = loaded, 2 = faild to load
	public int textureStatus;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String textureName() {
		return mTextureName;
	}

	public String textureFilepath() {
		return mTexturePath;
	}

	public boolean isScreenSpace() {
		return mIsScreenSpace;
	}

	public void isScreenSpace(boolean isScreenSpace) {
		mIsScreenSpace = isScreenSpace;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneTextureLayer(int uid) {
		super(uid);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTextureName(String name) {
		if (name == null || name.equals(mTextureName))
			return;

		mTextureName = name;
		texture = null;

		textureStatus = TEXTURE_UNLOADED;
	}

	public void setTextureFilepath(String filepath) {
		if (filepath == null || filepath.equals(mTexturePath))
			return;

		mTexturePath = filepath;
		texture = null;

		textureStatus = TEXTURE_UNLOADED;
	}

	@Override
	public BaseSceneLayerSaveDefinition getSaveDefinition() {
		final var saveDefinition = new SceneTextureLayerSaveDefinition();

		fillBaseSceneLayerInfo(saveDefinition);

		saveDefinition.layerName = name;
		saveDefinition.textureName = mTextureName;
		saveDefinition.texturePath = mTexturePath;

		saveDefinition.translationSpeedModX = translationSpeedModX;
		saveDefinition.translationSpeedModY = translationSpeedModY;

		return saveDefinition;
	}

}
