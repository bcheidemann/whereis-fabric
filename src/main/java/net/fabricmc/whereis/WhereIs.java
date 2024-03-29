package net.fabricmc.whereis;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.json.simple.parser.ParseException;

public class WhereIs implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("whereis");

	@Override
	public void onInitialize() {
		LOGGER.info("Starting WhereIs mod...");

		LocationFile locationFile;
		try {
			locationFile = new LocationFile("whereis.locations.json");
		}
		catch (IOException e) {
			LOGGER.error("Failed to load locations from whereis.locations.json: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		catch (ParseException e) {
			LOGGER.error("Failed to load locations from whereis.locations.json: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		CommandRegistrationCallback.EVENT
			.register(
				(dispatcher, registryAccess, environment) -> {
					WhereIsCommand.register(locationFile, dispatcher);
					HereIsCommand.register(locationFile, dispatcher);
					ForgetCommand.register(locationFile, dispatcher);
					RelocateCommand.register(locationFile, dispatcher);
				}
			);
	}
}
