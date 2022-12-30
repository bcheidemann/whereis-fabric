package net.fabricmc.whereis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import static net.minecraft.server.command.CommandManager.*;

import java.util.ArrayList;

public class WhereIsCommand {
	public static final Logger LOGGER = LoggerFactory.getLogger("whereis");

  static Integer whereIsImplementation(
    LocationFile locationFile,
    CommandContext<ServerCommandSource> context,
    String owner,
    String alias
  ) {
    ServerCommandSource source = context.getSource();
    String dimension = source.getWorld().getDimensionKey().getValue().toString();
    ArrayList<Location> foundLocations = locationFile.findLocations(
      new LocationMeta(
        owner,
        alias,
        dimension
      ),
      FindLocationMethod.FUZZY
    );

    if (foundLocations.size() == 0) {
      source.sendError(Text.literal("No locations found"));
      return 0;
    }

    source.sendFeedback(
      Text
        .literal("Found " + foundLocations.size() + " locations(s)...")
        .setStyle(
          Style.EMPTY.withColor(TextColor.parse("aqua")).withItalic(true)
        ),
      false
    );

    for (Location location : foundLocations) {
      source.sendFeedback(location.toMutableText(), false);
    }

    return 0;
  }

  static void register(
    LocationFile locationFile,
    CommandDispatcher<ServerCommandSource> dispatcher
  ) {
    dispatcher.register(
      literal("whereis")
        .executes(
          context -> WhereIsCommand.whereIsImplementation(
            locationFile,
            context,
            "*",
            "*"
          )
        )
        .then(
          argument("alias", StringArgumentType.greedyString())
            .executes(
              context -> {
                String alias = StringArgumentType.getString(context, "alias");
                return WhereIsCommand.whereIsImplementation(
                  locationFile,
                  context,
                  "*",
                  alias
                );
              }
            )
        )
        .then(
          literal("my")
            .executes(
              context -> {
                String owner = context.getSource().getName();
                return WhereIsCommand.whereIsImplementation(
                  locationFile,
                  context,
                  owner,
                  "*"
                );
              }
            )
            .then(
              argument("alias", StringArgumentType.greedyString())
                .executes(
                  context -> {
                    String owner = context.getSource().getName();
                    String alias = StringArgumentType.getString(
                      context,
                      "alias"
                    );
                    return WhereIsCommand.whereIsImplementation(
                      locationFile,
                      context,
                      owner,
                      alias
                    );
                  }
                )
            )
        )
        .then(
          literal("the")
            .then(
              argument("owner", StringArgumentType.word())
                .executes(
                  context -> {
                    String owner = StringArgumentType.getString(
                      context,
                      "owner"
                    );
                    return WhereIsCommand.whereIsImplementation(
                      locationFile,
                      context,
                      owner,
                      null
                    );
                  }
                )
                .then(
                  argument("alias", StringArgumentType.greedyString())
                    .executes(
                      context -> {
                        String owner = StringArgumentType.getString(
                          context,
                          "owner"
                        );
                        String alias = StringArgumentType.getString(
                          context,
                          "alias"
                        );
                        return WhereIsCommand.whereIsImplementation(
                          locationFile,
                          context,
                          owner,
                          alias
                        );
                      }
                    )
                )
            )
        )
    );
  }
}
