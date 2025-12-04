package lintfordpickle.harvest.data.scene.physics;

import lintfordpickle.harvest.ConstantsGame;
import lintfordpickle.harvest.data.GridCollisionTypes;
import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.dynamics.RigidBody.BodyType;
import net.lintfordlib.core.physics.dynamics.RigidBodyEntity;
import net.lintfordlib.core.physics.shapes.PolygonShape;

public class ScenePhysicsObjectInstance extends RigidBodyEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 5571115715609894253L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScenePhysicsObjectInstance(int uid) {
		super(uid, GridCollisionTypes.COLLISION_TYPE_CARGO);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(float worldCenterX, float worldCenterY, float rotation) {
		final var lDensity = 5.0f;
		final var lRestitution = .5f;
		final var lStaticFriction = .8f;
		final var lDynamicFriction = .3f;

		final var unitCenterX = ConstantsPhysics.toUnits(worldCenterX);
		final var unitCenterY = ConstantsPhysics.toUnits(worldCenterY);

		body = new RigidBody(BodyType.Static);
		body.addShape(PolygonShape.createEmptyPolygonShape(lDensity, lRestitution, lStaticFriction, lDynamicFriction));
		body.moveTo(unitCenterX, unitCenterY);
		body.angle(rotation);

		body.userData("floor");

		body.categoryBits(ConstantsGame.PHYSICS_WORLD_MASK_WALL);
		body.maskBits(ConstantsGame.PHYSICS_WORLD_MASK_SHIP);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setPolygonVertices(Vector2f newA, Vector2f newB, Vector2f newC, Vector2f newD) {
		body.shape().setLocalVertices(newA, newB, newC, newD);

	}

}
