package com.kamesuta.mc.signpic;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.Session;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(final @Nonnull FMLPreInitializationEvent event) {
		super.preInit(event);

		Log.log = event.getModLog();
		Config.init(event.getSuggestedConfigurationFile());

		// Setup stencil clip
		// StencilClip.init();

		// Setup location
		Client.initLocation(new Locations(event.getSourceFile(), getDataDirectory()));

		// Get Id
		final String id = Client.mc.getSession().getPlayerID();
		try {
			final Object o = UUIDTypeAdapter.fromString(id);
			if (o!=null) {
				Client.id = id;
				final Session s = Client.mc.getSession();
				Client.name = s.getUsername();
				Client.token = s.getToken();
			}
		} catch (final IllegalArgumentException e) {
		}
	}

	private @Nonnull File getDataDirectory() {
		final File file = Client.mc.mcDataDir;
		try {
			return file.getCanonicalFile();
		} catch (final IOException e) {
			Log.dev.error("Could not canonize path!", e);
		}
		return file;
	}

	@Override
	public void init(final @Nonnull FMLInitializationEvent event) {
		super.init(event);

		// Replace Sign Renderer
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySign.class, Client.renderer);

		// Event Register
		Client.handler.init();
		ClientCommandHandler.instance.registerCommand(Client.rootCommand);
	}

	@Override
	public void postInit(final @Nonnull FMLPostInitializationEvent event) {
		super.postInit(event);

		Config.getConfig().save();
	}
}