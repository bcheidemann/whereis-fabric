package net.fabricmc.whereis;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec3d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.StringArgumentType;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

import static net.minecraft.server.command.CommandManager.*;

public class WhereIs implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("whereis");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		LocationFile locationFile;

		try {
			locationFile = new LocationFile("whereis.locations.json", LOGGER);
		}
		catch (IOException e) {
			LOGGER.error("Failed to create or load the location file");
			return;
		}
		catch (ParseException e) {
			LOGGER.error("Failed to parse location file");
			return;
		}

		Style MessageStyle = Style.EMPTY.withColor(TextColor.parse("aqua")).withItalic(true);

		CommandRegistrationCallback.EVENT
			.register(
				(dispatcher, registryAccess, environment) -> {
					dispatcher.register(
						literal("whereis")
							.then(
								argument("alias", StringArgumentType.greedyString())
									.executes(context -> {
										ServerCommandSource source = context.getSource();
										String alias = StringArgumentType.getString(context, "alias");

										LOGGER.info("Looking up location " + alias);

										ArrayList<Location> foundLocations = locationFile.findLocations(alias);

										LOGGER.info("Done");

										if (foundLocations.size() == 0) {
											source.sendError(Text.literal("No locations found"));
											return 0;
										}

										source.sendFeedback(
											Text
												.literal("Found " + foundLocations.size() + " location(s)...")
												.setStyle(MessageStyle),
											false
										);
										for (Location location : foundLocations) {
											source.sendFeedback(location.toMutableText(), false);
										}

										return 1;
									})
							)
							.executes(context -> {
								ServerCommandSource source = context.getSource();
								ArrayList<Location> locations = locationFile.getLocations();

								if (locations.size() == 0) {
									source.sendError(Text.literal("No locations. Run \"/hereis [Location Name]\" to add a location."));
									return 0;
								}

								source.sendFeedback(
									Text
										.literal("Found " + locations.size() + " location(s)...")
										.setStyle(MessageStyle),
								false
								);
								for (Location location : locations) {
									source.sendFeedback(location.toMutableText(), false);
								}

								return 1;
							})
					);

					// TODO: Add remove command

					dispatcher.register(
						literal("hereis")
							.then(
								argument("alias", StringArgumentType.greedyString())
									.executes(context -> {
										String alias = StringArgumentType.getString(context, "alias");
										ServerCommandSource source = context.getSource();
										Vec3d position = source.getPosition();

										String savingMessage =
											"Saving location \""
											+ alias
											+ "\" at x="
											+ Math.round(position.x)
											+ ", y="
											+ Math.round(position.y)
											+ ", z="
											+ Math.round(position.z);
										source.sendFeedback(Text.literal(savingMessage), false);

										try {
											locationFile.addLocation(alias, position);
										}
										catch (IOException e) {
											LOGGER.error("Failed to write to location file");
											String errorMessage = "Failed to save location \"" + alias + "\"";
											source.sendError(Text.literal(errorMessage));
											return -1;
										}

										return 1;
									})
							)
							.executes(context -> {
								context
									.getSource()
									.sendFeedback(
										Text
											.literal("Usage: /hereis <alias>")
											.setStyle(MessageStyle),
										false
									);

								return 1;
							})
					);
		});
	}
}
