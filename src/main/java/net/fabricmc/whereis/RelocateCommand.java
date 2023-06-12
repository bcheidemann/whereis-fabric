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
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.*;

import java.io.IOException;

public class RelocateCommand {
	public static final Logger LOGGER = LoggerFactory.getLogger("whereis");

  static Integer relocateImplementation(
    LocationFile locationFile,
    CommandContext<ServerCommandSource> context,
    String owner,
    String alias
  ) {
    ServerCommandSource source = context.getSource();
    Vec3d position = source.getPosition();
    String dimension = source.getWorld().getDimensionKey().getValue().toString();

    String savingMessage =
      "Moving location \""
      + alias
      + "\" to x="
      + Math.round(position.x)
      + ", y="
      + Math.round(position.y)
      + ", z="
      + Math.round(position.z)
      + " in dimension "
      + dimension;
    source.sendFeedback(() -> Text.literal(savingMessage), false);

    int countMoved;

    try {
      countMoved = locationFile.moveLocation(
        new LocationMeta(
          owner,
          alias,
          dimension
        ),
        new Location(
          owner,
          alias,
          dimension,
          position
        )
      );
    }
    catch (IOException e) {
      LOGGER.error("Failed to write to location file: " + e.getMessage());
      e.printStackTrace();
      String errorMessage = "Failed to move location \"" + alias + "\"";
      source.sendError(Text.literal(errorMessage));
      return -1;
    }

    source.sendFeedback(
      () -> Text
        .literal("Moved " + countMoved + " location(s)...")
        .setStyle(
          Style.EMPTY.withColor(TextColor.parse("aqua")).withItalic(true)
        ),
      false
    );

    return 0;
  }

  static void register(
    LocationFile locationFile,
    CommandDispatcher<ServerCommandSource> dispatcher
  ) {
    dispatcher.register(
      literal("relocate")
        .then(
          argument("alias", StringArgumentType.greedyString())
            .executes(context -> {
              String alias = StringArgumentType.getString(context, "alias");
              return RelocateCommand.relocateImplementation(locationFile, context, "*", alias);
            })
        )
        .then(
          literal("my")
            .then(
              argument("alias", StringArgumentType.greedyString())
                .executes(context -> {
                  String owner = context.getSource().getName();
                  String alias = StringArgumentType.getString(context, "alias");
                  return RelocateCommand.relocateImplementation(locationFile, context, owner, alias);
                })
            )
        )
        .then(
          literal("the")
            .then(
              argument("owner", StringArgumentType.word())
                .then(
                  argument("alias", StringArgumentType.greedyString())
                    .executes(context -> {
                      String owner = StringArgumentType.getString(context, "owner");
                      String alias = StringArgumentType.getString(context, "alias");
                      return RelocateCommand.relocateImplementation(locationFile, context, owner, alias);
                    })
                )
            )
        )
    );
  }
}
