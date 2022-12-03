package net.fabricmc.whereis;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;

import static net.minecraft.server.command.CommandManager.*;

public class WhereIs implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		CommandRegistrationCallback.EVENT
			.register(
				(dispatcher, registryAccess, environment) -> {
					// dispatcher
					// 	.register(
					// 		literal("hereis")
					// 		.then(
					// 			argument("alias", StringArgumentType.greedyString())
					// 				.executes(context -> {
					// 					// For versions below 1.19, replace "Text.literal" with "new LiteralText".
					// 					context.getSource().getEntity().sendMessage(Text.literal("Called /foo with no arguments =)"));
										
					// 					String alias = StringArgumentType.getString(context, "alias");
			
					// 					context.getSource().getEntity().sendMessage(Text.literal(alias));
						
					// 					return 1;
					// 				})
					// 		)
					dispatcher.register(
						literal("whereis")
							.then(
								literal("bar")
									.executes(context -> {
											System.out.println("Foo Bar");
											return 1;
									})
									.then(
										argument("alias", StringArgumentType.greedyString())
											.executes(context -> {
												String alias = StringArgumentType.getString(context, "alias");
					
												context.getSource().getEntity().sendMessage(Text.literal(alias));

												return 1;
											})
									)
							)
							.executes(c -> {
								System.out.println("Called foo with no arguments");
								return 1;
							})
					);

					dispatcher.register(
						literal("hereis")
							.then(
								argument("alias", StringArgumentType.greedyString())
									.executes(context -> {
										String alias = StringArgumentType.getString(context, "alias");

										Vec3d position = context.getSource().getPosition();
			
										context.getSource().getEntity().sendMessage(Text.literal(alias + " - " + position.toString()));

										return 1;
									})
							)
							.executes(context -> {
								context
									.getSource()
									.getEntity()
									.sendMessage(
										Text.literal("Usage: /hereis <alias>")
									);

								return 1;
							})
					);
		});
	}
}
