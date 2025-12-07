package lintfordpickle.harvest.data.assets;

import lintfordpickle.harvest.data.GridCollisionTypes;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.geometry.partitioning.GridEntity;
import net.lintfordlib.core.geometry.partitioning.SpatialHashGrid;
import net.lintfordlib.core.graphics.sprites.SpriteInstance;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public class SceneSpriteInstance extends GridEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -4978987772107082534L;

	public static final int STATUS_UNLOADED = 0;
	public static final int STATUS_LOADED = 1;
	public static final int STATUS_FAILED = 2;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String definitionName;
	public transient SceneSpriteDefinition definition;

	public final Rectangle destRect = new Rectangle();

	// TODO: This is used in a few places, make a texturestatus enum and cache object (loaded, unloaded, failed)
	public transient int spriteSheetStatus;
	public transient SpriteSheetDefinition spriteSheetDefinition;
	public transient int spriteInstStatus;
	public transient SpriteInstance spriteInstance;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SceneSpriteInstance(int entityUid) {
		super(entityUid, GridCollisionTypes.COLLISION_TYPE_NONE);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(SceneSpriteDefinition assetDefinition, float x, float y, float width, float height, float rotation, float radius) {
		if (assetDefinition == null) {
			definitionName = null;
			definition = null;
			return;
		}

		definitionName = assetDefinition.definitionName();
		definition = assetDefinition;

		destRect.setPosition(x, y);
		destRect.setDimensions(width, height);

	}

	@Override
	public void fillEntityBounds(SpatialHashGrid<?> grid) {
		minX = grid.getCellIndexX((int) destRect.left());
		minY = grid.getCellIndexY((int) destRect.top());

		maxX = grid.getCellIndexX((int) destRect.right());
		maxY = grid.getCellIndexY((int) destRect.bottom());
	}

	@Override
	public boolean isGridCacheOld(SpatialHashGrid<?> grid) {
		final float newMinX = grid.getCellIndexX((int) destRect.left());
		final float newMinY = grid.getCellIndexY((int) destRect.top());

		final float newMaxX = grid.getCellIndexX((int) destRect.right());
		final float newMaxY = grid.getCellIndexY((int) destRect.bottom());

		if (newMinX == minX && newMinY == minY && newMaxX == maxX && newMaxY == maxY)
			return false; // early out

		return true;
	}
}
