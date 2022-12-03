package net.fabricmc.whereis;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
										ArrayList<Location> foundLocations = locationFile.findLocations(alias);

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
											LOGGER.error("Failed to write to location file: " + e.getMessage());
											e.printStackTrace();
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
									.sendError(
										Text.literal("Usage: /hereis <alias>")
									);

								return 1;
							})
					);
		});
	}
}
