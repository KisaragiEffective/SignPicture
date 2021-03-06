package net.teamfruit.signpic.compat;

import static org.lwjgl.opengl.GL11.*;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.teamfruit.bnnwidget.compat.OpenGL;
import net.teamfruit.bnnwidget.render.WRenderer;
import net.teamfruit.signpic.compat.Compat.CompatBakedModel;
import net.teamfruit.signpic.compat.Compat.CompatModel;
import net.teamfruit.signpic.compat.CompatEvents.CompatModelBakeEvent;
import net.teamfruit.signpic.compat.CompatEvents.CompatModelRegistryEvent;

@SuppressWarnings("deprecation")
public abstract class CompatItemSignRenderer implements ISmartItemModel, IPerspectiveAwareModel {
	public abstract boolean isSignPicture(@Nullable final ItemStack item);

	public abstract boolean isSeeMode();

	public abstract float getRenderSeeOpacity();

	public void renderSign(@Nullable final ItemStack item) {
	}

	public static enum ItemSignTransformType {
		NONE,
		THIRD_PERSON_LEFT_HAND,
		THIRD_PERSON_RIGHT_HAND,
		FIRST_PERSON_LEFT_HAND,
		FIRST_PERSON_RIGHT_HAND,
		HEAD,
		GUI,
		GROUND,
		FIXED,
		;

		public static @Nonnull ItemSignTransformType fromType(final @Nullable TransformType type) {
			if (type==null)
				return NONE;
			switch (type) {
				case THIRD_PERSON:
					return THIRD_PERSON_RIGHT_HAND;
				case FIRST_PERSON:
					return FIRST_PERSON_RIGHT_HAND;
				case HEAD:
					return HEAD;
				case GUI:
					return GUI;
				case GROUND:
					return GROUND;
				case FIXED:
					return FIXED;
				default:
					return NONE;
			}
		}
	}

	public abstract void renderSignPicture(final @Nonnull ItemSignTransformType type, final @Nonnull CompatVersion version, final @Nullable ItemStack item);

	public final @Nonnull ModelResourceLocation modelResourceLocation = new ModelResourceLocation("minecraft:sign");
	private @Nullable IBakedModel baseModel = null;
	private @Nullable ItemStack itemStack;

	public void setBaseModel(final @Nonnull IBakedModel model) {
		this.baseModel = model;
	}

	@Override
	public @Nullable IBakedModel handleItemState(final @Nullable ItemStack stack) {
		this.itemStack = stack;
		if (isSignPicture(stack))
			return this;
		else
			return this.baseModel;
	}

	@Override
	public @Nullable Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(final @Nullable TransformType cameraTransformType) {
		final ItemStack itemStack = this.itemStack;
		if (itemStack!=null&&cameraTransformType!=null) {
			OpenGL.glPushMatrix();
			if (this.baseModel instanceof IPerspectiveAwareModel) {
				final Pair<? extends IFlexibleBakedModel, Matrix4f> pair = ((IPerspectiveAwareModel) this.baseModel).handlePerspective(cameraTransformType);
				if (pair.getRight()!=null)
					ForgeHooksClient.multiplyCurrentGlMatrix(pair.getRight());
			}
			OpenGL.glDisable(GL11.GL_CULL_FACE);
			renderItem(cameraTransformType, itemStack);
			// TODO
			OpenGL.glEnable(GL11.GL_LIGHTING);
			OpenGL.glEnable(GL11.GL_BLEND);
			OpenGL.glEnable(GL11.GL_TEXTURE_2D);
			OpenGL.glEnable(GL11.GL_CULL_FACE);
			OpenGL.glPopMatrix();
		}
		return Pair.of(this, null);
	}

	public void renderItem(final @Nonnull TransformType type, final @Nullable ItemStack item) {
		OpenGL.glPushMatrix();
		if ((type==TransformType.GROUND||type==TransformType.FIXED)&&isSeeMode()) {
			OpenGL.glPushMatrix();
			WRenderer.startTexture();
			OpenGL.glColor4f(1f, 1f, 1f, getRenderSeeOpacity());
			OpenGL.glTranslatef(-.5f, -.25f, 0.0625f/2f);
			renderSign(item);
			OpenGL.glPopMatrix();
		}
		OpenGL.glPushAttrib();
		OpenGL.glDisable(GL_CULL_FACE);
		renderSignPicture(ItemSignTransformType.fromType(type), CompatVersion.version(), item);
		OpenGL.glPopAttrib();
		OpenGL.glPopMatrix();
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public @Nullable TextureAtlasSprite getParticleTexture() {
		return this.baseModel!=null ? this.baseModel.getParticleTexture() : null;
	}

	@Override
	public @Nullable List<BakedQuad> getFaceQuads(final @Nullable EnumFacing facing) {
		return this.baseModel!=null ? this.baseModel.getFaceQuads(facing) : null;
	}

	@Override
	public @Nullable List<BakedQuad> getGeneralQuads() {
		return ImmutableList.of();
	}

	@Override
	public @Nullable ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return this.baseModel!=null&&this.baseModel.isAmbientOcclusion();
	}

	@Override
	public @Nullable VertexFormat getFormat() {
		return Attributes.DEFAULT_BAKED_FORMAT;
	}

	public void registerModelRegistry(final CompatModelRegistryEvent event, final CompatItemSignModelLoader modelLoader) {
	}

	public void registerModelBakery(final CompatModelBakeEvent event) {
		final IBakedModel object = event.getModelRegistry().getObject(this.modelResourceLocation);
		setBaseModel(object);
		event.getModelRegistry().putObject(this.modelResourceLocation, this);
	}

	public CompatBakedModel injectBakedModel(@Nonnull final CompatBakedModel bakedModel) {
		return new CompatBakedModel();
	}

	public static class CompatModelLoaderRegistry {
		public static CompatModel getMissingModel() {
			return new CompatModel();
		}
	}
}