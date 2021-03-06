package net.teamfruit.signpic.compat;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.teamfruit.signpic.CoreEvent;

public class CompatEvents {
	public static abstract class CompatHandler {
		public void registerHandler() {
			// FMLCommonHandler.instance().bus().register(this);
			MinecraftForge.EVENT_BUS.register(this);
		}

		@SubscribeEvent
		public void onKeyInput(final @Nonnull InputEvent event) {
			onKeyInput(new CompatInputEvent(event));
		}

		@CoreEvent
		public abstract void onKeyInput(final @Nonnull CompatInputEvent event);

		@SubscribeEvent
		public void onRenderTick(final @Nonnull TickEvent.RenderTickEvent event) {
			onRenderTick(new CompatTickEvent.CompatRenderTickEvent(event));
		}

		@SubscribeEvent
		public void onTick(final @Nonnull TickEvent.ClientTickEvent event) {
			onTick(new CompatTickEvent.CompatClientTickEvent(event));
		}

		@CoreEvent
		public abstract void onTick(final @Nonnull CompatTickEvent.CompatClientTickEvent event);

		@CoreEvent
		public abstract void onRenderTick(final @Nonnull CompatTickEvent.CompatRenderTickEvent event);

		@SubscribeEvent
		public void onSign(final @Nonnull GuiOpenEvent event) {
			onSign(new CompatGuiOpenEvent(event));
		}

		@CoreEvent
		public abstract void onSign(final @Nonnull CompatGuiOpenEvent event);

		@SubscribeEvent
		public void onClick(final @Nonnull MouseEvent event) {
			onClick(new CompatMouseEvent(event));
		}

		@CoreEvent
		public abstract void onClick(final @Nonnull CompatMouseEvent event);

		@SubscribeEvent
		public void onTooltip(final @Nonnull ItemTooltipEvent event) {
			onTooltip(new CompatItemTooltipEvent(event));
		}

		@CoreEvent
		public abstract void onTooltip(final @Nonnull CompatItemTooltipEvent event);

		@SubscribeEvent
		public void onRender(final @Nonnull RenderWorldLastEvent event) {
			onRender(new CompatRenderWorldLastEvent(event));
		}

		@CoreEvent
		public abstract void onRender(final @Nonnull CompatRenderWorldLastEvent event);

		@SubscribeEvent()
		public void onDraw(final @Nonnull RenderGameOverlayEvent.Post event) {
			onDraw(new CompatRenderGameOverlayEvent.CompatPost(event));
		}

		@CoreEvent
		public abstract void onDraw(final @Nonnull CompatRenderGameOverlayEvent.CompatPost event);

		@SubscribeEvent
		public void onText(final @Nonnull RenderGameOverlayEvent.Text event) {
			onText(new CompatRenderGameOverlayEvent.CompatText(event));
		}

		@CoreEvent
		public abstract void onText(final @Nonnull CompatRenderGameOverlayEvent.CompatText event);

		@SubscribeEvent()
		public void onDraw(final @Nonnull GuiScreenEvent.DrawScreenEvent.Post event) {
			onDraw(new CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost(event));
		}

		@CoreEvent
		public abstract void onDraw(final @Nonnull CompatGuiScreenEvent.CompatDrawScreenEvent.CompatPost event);

		@SubscribeEvent
		public void onConfigChanged(final @Nonnull ConfigChangedEvent.OnConfigChangedEvent event) {
			onConfigChanged(new CompatConfigChangedEvent.CompatOnConfigChangedEvent(event));
		}

		@CoreEvent
		public abstract void onConfigChanged(final @Nonnull CompatConfigChangedEvent.CompatOnConfigChangedEvent event);

		@SubscribeEvent
		public void onResourceReloaded(final @Nonnull TextureStitchEvent.Post event) {
			onResourceReloaded(new CompatTextureStitchEvent.CompatPost(event));
		}

		@CoreEvent
		public abstract void onResourceReloaded(final @Nonnull CompatTextureStitchEvent.CompatPost event);

		@CoreEvent
		public abstract void onModelRegistry(final CompatModelRegistryEvent event);

		@SubscribeEvent
		public void onModelBakeEvent(final @Nonnull ModelBakeEvent event) {
			onModelBakeEvent(new CompatModelBakeEvent(event));
		}

		@CoreEvent
		public abstract void onModelBakeEvent(final @Nonnull CompatModelBakeEvent event);
	}

	public static class CompatEvent<T extends Event> {
		protected final T event;

		public CompatEvent(final T event) {
			this.event = event;
		}

		public void setCanceled(final boolean cancel) {
			this.event.setCanceled(cancel);
		}
	}

	public static class CompatInputEvent extends CompatEvent<InputEvent> {
		public CompatInputEvent(final InputEvent event) {
			super(event);
		}
	}

	public static class CompatTickEvent extends CompatEvent<TickEvent> {
		public CompatTickEvent(final TickEvent event) {
			super(event);
		}

		public static class CompatClientTickEvent extends CompatTickEvent {
			public CompatClientTickEvent(final TickEvent event) {
				super(event);
			}
		}

		public static class CompatRenderTickEvent extends CompatTickEvent {
			public CompatRenderTickEvent(final TickEvent event) {
				super(event);
			}
		}

		public CompatPhase getTickPhase() {
			return CompatPhase.getPhase(this.event.phase);
		}

		public enum CompatPhase {
			START,
			END;
			;

			public static CompatPhase getPhase(final TickEvent.Phase phase) {
				if (phase==null)
					return START;
				switch (phase) {
					default:
					case START:
						return START;
					case END:
						return END;
				}
			}
		}
	}

	public static class CompatGuiOpenEvent extends CompatEvent<GuiOpenEvent> {
		public CompatGuiOpenEvent(final GuiOpenEvent event) {
			super(event);
		}

		public GuiScreen getGui() {
			return this.event.getGui();
		}
	}

	public static class CompatMouseEvent extends CompatEvent<MouseEvent> {
		public CompatMouseEvent(final MouseEvent event) {
			super(event);
		}

		public boolean getButtonState() {
			return this.event.isButtonstate();
		}

		public int getButton() {
			return this.event.getButton();
		}
	}

	public static class CompatItemTooltipEvent extends CompatEvent<ItemTooltipEvent> {
		public CompatItemTooltipEvent(final ItemTooltipEvent event) {
			super(event);
		}

		public ItemStack getItemStack() {
			return this.event.getItemStack();
		}

		public List<String> getTooltip() {
			return this.event.getToolTip();
		}
	}

	public static class CompatRenderWorldLastEvent extends CompatEvent<RenderWorldLastEvent> {
		public CompatRenderWorldLastEvent(final RenderWorldLastEvent event) {
			super(event);
		}

		public float getPartialTicks() {
			return this.event.getPartialTicks();
		}
	}

	public static class CompatRenderGameOverlayEvent<T extends RenderGameOverlayEvent> extends CompatEvent<T> {
		public CompatRenderGameOverlayEvent(final T event) {
			super(event);
		}

		public static class CompatPost extends CompatRenderGameOverlayEvent<RenderGameOverlayEvent> {
			public CompatPost(final RenderGameOverlayEvent event) {
				super(event);
			}
		}

		public static class CompatText extends CompatRenderGameOverlayEvent<RenderGameOverlayEvent.Text> {
			public CompatText(final RenderGameOverlayEvent.Text event) {
				super(event);
			}

			public List<String> getLeft() {
				return this.event.getLeft();
			}
		}

		public ScaledResolution getResolution() {
			return this.event.getResolution();
		}

		public CompatElementType getType() {
			return CompatElementType.getType(this.event.getType());
		}

		public float getPartialTicks() {
			return this.event.getPartialTicks();
		}

		public static enum CompatElementType {
			CHAT,
			EXPERIENCE,
			OTHER,
			;

			public static CompatElementType getType(final RenderGameOverlayEvent.ElementType type) {
				if (type==null)
					return CompatElementType.OTHER;
				switch (type) {
					case CHAT:
						return CHAT;
					case EXPERIENCE:
						return EXPERIENCE;
					default:
						return OTHER;
				}
			}
		}
	}

	public static class CompatGuiScreenEvent<T extends GuiScreenEvent> extends CompatEvent<T> {
		public CompatGuiScreenEvent(final T event) {
			super(event);
		}

		public static class CompatDrawScreenEvent extends CompatGuiScreenEvent<GuiScreenEvent.DrawScreenEvent> {
			public CompatDrawScreenEvent(final GuiScreenEvent.DrawScreenEvent event) {
				super(event);
			}

			public GuiScreen getGui() {
				return this.event.getGui();
			}

			public int getMouseX() {
				return this.event.getMouseX();
			}

			public int getMouseY() {
				return this.event.getMouseY();
			}

			public float getRenderPartialTicks() {
				return this.event.getRenderPartialTicks();
			}

			public static class CompatPost extends CompatDrawScreenEvent {
				public CompatPost(final GuiScreenEvent.DrawScreenEvent event) {
					super(event);
				}
			}
		}
	}

	public static class CompatConfigChangedEvent extends CompatEvent<ConfigChangedEvent> {
		public CompatConfigChangedEvent(final ConfigChangedEvent event) {
			super(event);
		}

		public String getModId() {
			return this.event.getModID();
		}

		public static class CompatOnConfigChangedEvent extends CompatConfigChangedEvent {
			public CompatOnConfigChangedEvent(final ConfigChangedEvent event) {
				super(event);
			}
		}
	}

	public static class CompatTextureStitchEvent extends CompatEvent<TextureStitchEvent> {
		public CompatTextureStitchEvent(final TextureStitchEvent event) {
			super(event);
		}

		public static class CompatPost extends CompatTextureStitchEvent {
			public CompatPost(final TextureStitchEvent event) {
				super(event);
			}
		}
	}

	public static class CompatModelBakeEvent extends CompatEvent<ModelBakeEvent> {
		public CompatModelBakeEvent(final ModelBakeEvent event) {
			super(event);
		}

		public IRegistry<ModelResourceLocation, IBakedModel> getModelRegistry() {
			return this.event.getModelRegistry();
		}
	}

	public static class CompatModelRegistryEvent extends CompatEvent<ModelRegistryEvent> {
		public CompatModelRegistryEvent(final ModelRegistryEvent event) {
			super(event);
		}
	}
}
