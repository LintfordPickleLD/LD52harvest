package lintfordpickle.harvest.renderers.trails;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStats;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.core.maths.Matrix4f;

public class TrailBatchRenderer {

	private class VertexDefinition {

		public static final int elementBytes = 4;

		public static final int positionElementCount = 4;
		public static final int colorElementCount = 4;
		public static final int textureElementCount = 2;

		public static final int elementCount = positionElementCount + colorElementCount + textureElementCount;

		public static final int positionBytesCount = positionElementCount * elementBytes;
		public static final int colorBytesCount = colorElementCount * elementBytes;
		public static final int textureBytesCount = textureElementCount * elementBytes;

		public static final int positionByteOffset = 0;
		public static final int colorByteOffset = positionByteOffset + positionBytesCount;
		public static final int textureByteOffset = colorByteOffset + colorBytesCount;

		public static final int stride = positionBytesCount + colorBytesCount + textureBytesCount;
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_SEGMENTS = 1600;
	public static final int NUM_VERTS_PER_SEGMENT = 4;
	public static final int NUM_INDICES_PER_SEGMENT = 6;

	private static IntBuffer mIndexBuffer;

	private static IntBuffer getIndexBuffer() {
		if (mIndexBuffer == null) {
			mIndexBuffer = MemoryUtil.memAllocInt(MAX_SEGMENTS * NUM_INDICES_PER_SEGMENT);

			mIndexBuffer.clear();
			for (int i = 0; i < MAX_SEGMENTS; i++) {
				mIndexBuffer.put((i * NUM_VERTS_PER_SEGMENT) + 0);
				mIndexBuffer.put((i * NUM_VERTS_PER_SEGMENT) + 1);
				mIndexBuffer.put((i * NUM_VERTS_PER_SEGMENT) + 2);

				mIndexBuffer.put((i * NUM_VERTS_PER_SEGMENT) + 1);
				mIndexBuffer.put((i * NUM_VERTS_PER_SEGMENT) + 2);
				mIndexBuffer.put((i * NUM_VERTS_PER_SEGMENT) + 3);
			}
			mIndexBuffer.flip();
		}

		return mIndexBuffer;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected TrailShader mTrailShader;
	protected int mVaoId = -1;
	protected int mVioId = -1;
	protected int mVboId = -1;
	protected int mCurrentTexID;
	protected Matrix4f mModelMatrix;
	protected FloatBuffer mBuffer;
	protected boolean mResourcesLoaded;
	protected boolean mAreGlContainersInitialized = false;
	protected int mSegmentCountToDraw;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TrailBatchRenderer() {
		mTrailShader = new TrailShader("Ship Trails");
		mModelMatrix = new Matrix4f();
		mResourcesLoaded = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mTrailShader.loadResources(resourceManager);

		if (mVboId == -1) {
			mVboId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vbo " + mVboId);
		}

		if (mVioId == -1) {
			mVioId = GL15.glGenBuffers();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenBuffers: vio " + mVioId);
		}

		mBuffer = MemoryUtil.memAllocFloat(MAX_SEGMENTS * NUM_VERTS_PER_SEGMENT * VertexDefinition.elementCount);

		mResourcesLoaded = true;

		if (resourceManager.isMainOpenGlThread())
			initializeGlContainers();

	}

	private void initializeGlContainers() {
		if (!mResourcesLoaded) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Cannot create Gl containers until resources have been loaded");
			return;
		}

		if (mAreGlContainersInitialized)
			return;

		if (mVaoId == -1) {
			mVaoId = GL30.glGenVertexArrays();
			Debug.debugManager().logger().v(getClass().getSimpleName(), "[OpenGl] glGenVertexArrays: " + mVaoId);
		}

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MAX_SEGMENTS * NUM_VERTS_PER_SEGMENT * VertexDefinition.stride, GL15.GL_DYNAMIC_DRAW);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL20.glVertexAttribPointer(0, VertexDefinition.positionElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDefinition.colorElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.colorByteOffset);
		GL20.glVertexAttribPointer(2, VertexDefinition.textureElementCount, GL11.GL_FLOAT, false, VertexDefinition.stride, VertexDefinition.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mVioId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, getIndexBuffer(), GL15.GL_STATIC_DRAW);

		GL30.glBindVertexArray(0);

		mAreGlContainersInitialized = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mTrailShader.unloadResources();

		if (mVaoId > -1) {
			GL30.glDeleteVertexArrays(mVaoId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading VaoId = " + mVaoId);
			mVaoId = -1;
		}

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading VboId = " + mVboId);
			mVboId = -1;
		}

		if (mVioId > -1) {
			GL15.glDeleteBuffers(mVioId);
			Debug.debugManager().logger().v("OpenGL", "IndexedPolyBatchPCT: Unloading mVioId = " + mVioId);
			mVioId = -1;
		}

		if (mBuffer != null) {
			mBuffer.clear();
			MemoryUtil.memFree(mBuffer);
			mBuffer = null;
		}

		mResourcesLoaded = false;
		mAreGlContainersInitialized = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// ----

	private ICamera mCamera;

	public void update(LintfordCore core) {
		mTrailShader.update(core);
	}

	public void begin(ICamera camera) {
		mCamera = camera;

		mSegmentCountToDraw = 0;
		mBuffer.clear();
	}

	public void draw(Texture texture, TrailVertex[] vertices, int segmentCount) {
		final int lNumNewSegments = segmentCount;
		if (mSegmentCountToDraw + lNumNewSegments > MAX_SEGMENTS) {
			flush();
			begin(mCamera);
		}

		if (mCurrentTexID != texture.getTextureID()) {
			mCurrentTexID = texture.getTextureID();
		}

		final int lVertexCount = lNumNewSegments * NUM_VERTS_PER_SEGMENT;
		for (int i = 0; i < lVertexCount; i++) {
			final var vert = vertices[i];

			mBuffer.put(vert.x).put(vert.y).put(-0.01f).put(1.0f); // pos
			mBuffer.put(vert.r).put(vert.g).put(vert.b).put(vert.a);
			mBuffer.put(vert.u).put(vert.v);
		}

		mSegmentCountToDraw += lNumNewSegments;
	}

	public void end() {
		if (mResourcesLoaded == false)
			return;

		if (mSegmentCountToDraw == 0)
			return;

		flush();
	}

	private void flush() {
		if (!mAreGlContainersInitialized)
			initializeGlContainers();

		mBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, mBuffer);

		if (mCurrentTexID != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mCurrentTexID);
		} else {
			return;
		}

		mTrailShader.projectionMatrix(mCamera.projection());
		mTrailShader.viewMatrix(mCamera.view());
		mTrailShader.modelMatrix(mModelMatrix);

		mTrailShader.bind();

		{
			final int lNumQuads = mSegmentCountToDraw;
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_DRAWCALLS);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_VERTS, lNumQuads * NUM_VERTS_PER_SEGMENT);
			Debug.debugManager().stats().incTag(DebugStats.TAG_ID_TRIS, lNumQuads * 2);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, mSegmentCountToDraw * NUM_INDICES_PER_SEGMENT, GL11.GL_UNSIGNED_INT, 0);

		mTrailShader.unbind();

		GL30.glBindVertexArray(0);
	}
}
