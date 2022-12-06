package net.fabricmc.whereis;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.whereis.LocationFile.LocationExistsError;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.StringArgumentType;

import java.io.IOException;

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

		CommandRegistrationCallback.EVENT
			.register(
				(dispatcher, registryAccess, environment) -> {
					WhereIsCommand.register(locationFile, dispatcher);

					dispatcher.register(
						literal("hereis")
						.then(
							literal("my")
								.then(
									argument("alias", StringArgumentType.greedyString())
									.executes(context -> {
										ServerCommandSource source = context.getSource();
										String alias = StringArgumentType.getString(context, "alias");
										String dimension = source.getWorld().getDimensionKey().getValue().toString();
										Vec3d position = source.getPosition();

										String savingMessage =
											"Saving marker \""
											+ alias
											+ "\" at x="
											+ Math.round(position.x)
											+ ", y="
											+ Math.round(position.y)
											+ ", z="
											+ Math.round(position.z)
											+ " in dimension "
											+ dimension;
										source.sendFeedback(Text.literal(savingMessage), false);

										try {
											locationFile.addLocation(
												new Location(
													source.getName(),
													alias,
													dimension,
													position
												)
											);
										}
										catch (IOException e) {
											LOGGER.error("Failed to write to location file: " + e.getMessage());
											e.printStackTrace();
											String errorMessage = "Failed to save marker \"" + alias + "\"";
											source.sendError(Text.literal(errorMessage));
											return -1;
										} catch (LocationExistsError e) {
											source.sendError(Text.literal(e.getMessage()));
											source.sendFeedback(
												Text.literal(
													String.format("Use \"/hereis move my %s\" to move the marker to your current location", alias)
												),
												false
											);
											LOGGER.info("Location not saved: " + alias);
											return -1;
										}

										return 1;
									})
								)
						)
						.then(
								literal("remove")
									.then(
										literal("my")
											.then(
												argument("alias", StringArgumentType.greedyString())
													.executes(context -> {
														ServerCommandSource source = context.getSource();
														String alias = StringArgumentType.getString(context, "alias");
														String dimension = source.getWorld().getDimensionKey().getValue().toString();

														String removingMessage =
															"Removing marker \""
															+ alias
															+ "\" in dimension "
															+ dimension;
														source.sendFeedback(Text.literal(removingMessage), false);

														try {
															locationFile.removeLocation(
																source.getName(),
																alias,
																dimension
															);
														}
														catch (IOException e) {
															LOGGER.error("Failed to write to location file: " + e.getMessage());
															e.printStackTrace();
															String errorMessage = "Failed to save marker \"" + alias + "\"";
															source.sendError(Text.literal(errorMessage));
															return -1;
														}

														return 0;
													})
											)
									)
									.then(
										argument("alias", StringArgumentType.greedyString())
											.executes(context -> {
												ServerCommandSource source = context.getSource();
												String alias = StringArgumentType.getString(context, "alias");
												String dimension = source.getWorld().getDimensionKey().getValue().toString();

												String removingMessage =
													"Removing marker \""
													+ alias
													+ "\" in dimension "
													+ dimension;
												source.sendFeedback(Text.literal(removingMessage), false);

												try {
													locationFile.removeLocation(
														"*",
														alias,
														dimension
													);
												}
												catch (IOException e) {
													LOGGER.error("Failed to write to location file: " + e.getMessage());
													e.printStackTrace();
													String errorMessage = "Failed to save marker \"" + alias + "\"";
													source.sendError(Text.literal(errorMessage));
													return -1;
												}

												return 0;
											})
									)
							)
							.then(
								literal("move")
									.then(
										literal("my")
											.then(
												argument("alias", StringArgumentType.greedyString())
													.executes(context -> {
														ServerCommandSource source = context.getSource();
														String alias = StringArgumentType.getString(context, "alias");
														String dimension = source.getWorld().getDimensionKey().getValue().toString();
														Vec3d position = source.getPosition();

														String movingMessage =
															"Moving marker \""
															+ alias
															+ "\" to x="
															+ Math.round(position.x)
															+ ", y="
															+ Math.round(position.y)
															+ ", z="
															+ Math.round(position.z)
															+ " in dimension "
															+ dimension;
														source.sendFeedback(Text.literal(movingMessage), false);

														try {
															locationFile.moveLocation(
																new Location(
																	source.getName(),
																	alias,
																	dimension,
																	position
																)
															);
														}
														catch (IOException e) {
															LOGGER.error("Failed to write to location file: " + e.getMessage());
															e.printStackTrace();
															String errorMessage = "Failed to save marker \"" + alias + "\"";
															source.sendError(Text.literal(errorMessage));
															return -1;
														}

														return 0;
													})
											)
									)
									.then(
										argument("alias", StringArgumentType.greedyString())
											.executes(context -> {
												ServerCommandSource source = context.getSource();
												String alias = StringArgumentType.getString(context, "alias");
												String dimension = source.getWorld().getDimensionKey().getValue().toString();
												Vec3d position = source.getPosition();

												String movingMessage =
													"Moving marker \""
													+ alias
													+ "\" to x="
													+ Math.round(position.x)
													+ ", y="
													+ Math.round(position.y)
													+ ", z="
													+ Math.round(position.z)
													+ " in dimension "
													+ dimension;
												source.sendFeedback(Text.literal(movingMessage), false);

												try {
													locationFile.moveLocation(
														new Location(
															"*",
															alias,
															dimension,
															position
														)
													);
												}
												catch (IOException e) {
													LOGGER.error("Failed to write to location file: " + e.getMessage());
													e.printStackTrace();
													String errorMessage = "Failed to save marker \"" + alias + "\"";
													source.sendError(Text.literal(errorMessage));
													return -1;
												}

												return 0;
											})
									)
							)
							.then(
								argument("alias", StringArgumentType.greedyString())
									.executes(context -> {
										ServerCommandSource source = context.getSource();
										String alias = StringArgumentType.getString(context, "alias");
										String dimension = source.getWorld().getDimensionKey().getValue().toString();
										Vec3d position = source.getPosition();

										String savingMessage =
											"Saving marker \""
											+ alias
											+ "\" at x="
											+ Math.round(position.x)
											+ ", y="
											+ Math.round(position.y)
											+ ", z="
											+ Math.round(position.z)
											+ " in dimension "
											+ dimension;
										source.sendFeedback(Text.literal(savingMessage), false);

										try {
											locationFile.addLocation(
												new Location(
													"*",
													alias,
													dimension,
													position
												)
											);
										}
										catch (IOException e) {
											LOGGER.error("Failed to write to location file: " + e.getMessage());
											e.printStackTrace();
											String errorMessage = "Failed to save marker \"" + alias + "\"";
											source.sendError(Text.literal(errorMessage));
											return -1;
										} catch (LocationExistsError e) {
											source.sendError(Text.literal(e.getMessage()));
											source.sendFeedback(
												Text.literal(
													String.format("Use \"/hereis move %s\" to move the marker to your current location", alias)
												),
												false
											);
											LOGGER.info("Location not saved: " + alias);
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
