package com.kamesuta.mc.signpic.image;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import org.lwjgl.util.Timer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.util.ResourceLocation;

public class ImageManager {
	public static final float LoadSpan = .5f;

	public static Deque<Image> lazyloadqueue = new ArrayDeque<Image>();

	protected final HashMap<String, Image> pool = new HashMap<String, Image>();
	protected final ArrayList<Image> processes = new ArrayList<Image>();
	protected int currentprocess = 0;

	protected final Timer timer = new Timer();

	public ImageLocation location;

	public ImageManager(final ImageLocation location) {
		this.location = location;
	}

	public Image get(final String id) {
		if (id.startsWith("!")) {
			return get(new ResourceLocation(id.substring(1)));
		} else {
			Image image = this.pool.get(id);
			if (image == null) {
				image = new RemoteImage(id, this.location);
				this.pool.put(id, image);
				this.processes.add(image);
			}
			image.onImageUsed();
			return image;
		}
	}

	public Image get(final ResourceLocation location) {
		final String id = "!" + location.toString();
		Image image = this.pool.get(id);
		if (image == null) {
			image = new ResourceImage(location);
			this.pool.put(id, image);
			this.processes.add(image);
		}
		image.onImageUsed();
		return image;
	}

	public void delete(final Image image) {
		this.pool.remove(image.id);
		this.processes.remove(image);
		image.delete();
	}

	@SubscribeEvent
	public void renderTickProcess(final TickEvent.RenderTickEvent event) {
		Image textureload;
		if ((textureload = ImageManager.lazyloadqueue.peek()) != null) {
			if (textureload.processTexture()) {
				ImageManager.lazyloadqueue.poll();
			}
		}

		if(this.timer.getTime() > LoadSpan){
			this.timer.set(0);
			final int processsize = this.processes.size();
			if (!this.processes.isEmpty()) {
				this.currentprocess = (this.currentprocess<processsize-1)?this.currentprocess+1:0;
				final Image image = this.processes.get(this.currentprocess);
				image.process();
				if (image.shouldCollect())
					delete(image);
			}
		}

		Timer.tick();
	}
}
