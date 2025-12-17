package lintfordpickle.harvest.data.assets;

import java.io.File;

import com.google.gson.GsonBuilder;

import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.entities.definitions.DefinitionManager;

public class SceneSpriteManager {

	private static final String META_FILENAME = "res/def/assets/_meta.json";

	// ---------------------------------------------
	// Inner-Classes
	// ---------------------------------------------

	public class SpriteDefinitionManager extends DefinitionManager<SceneSpriteDefinition> {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public SpriteDefinitionManager() {
			final var metaDataFile = new File(META_FILENAME);
			loadDefinitionsFromMetaFile(metaDataFile);

			// for each sprite defintion, populate them with meta data (width/height) about the sprites ...

		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		@Override
		public void loadDefinitionsFromFolderWatcher(EntityLocationProvider entityLocationProvider) {
			final var gson = new GsonBuilder().create();
			loadDefinitionsFromFolderWatcherItems(entityLocationProvider, gson, SceneSpriteDefinition.class);
		}

		@Override
		public void loadDefinitionsFromMetaFile(File file) {
			final var gson = new GsonBuilder().create();
			loadDefinitionsFromMetaFileItems(file, gson, SceneSpriteDefinition.class);
		}

		@Override
		public SceneSpriteDefinition loadDefinitionFromFile(File file) {
			final var gson = new GsonBuilder().create();
			return loadDefinitionFromFile(file, gson, SceneSpriteDefinition.class);
		}

	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SpriteDefinitionManager mDefinitionManager = new SpriteDefinitionManager();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public SpriteDefinitionManager definitionManager() {
		return mDefinitionManager;
	}

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public SceneSpriteManager() {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public SceneSpriteInstance createAssetInstanceFromDefinitionName(String definitionName, float worldX, float worldY) {
		final var assetDefinition = mDefinitionManager.getByName(definitionName);

		if (assetDefinition == null)
			return null;

		final var assetInstance = createNewAsset();

		// values coming from json?
		assetInstance.initialize(assetDefinition, worldX, worldY, 32, 32, 0, 16);

		return assetInstance;
	}

	private SceneSpriteInstance createNewAsset() {
		return new SceneSpriteInstance(0);
	}

}
