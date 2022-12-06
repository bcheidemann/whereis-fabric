package net.fabricmc.whereis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.whereis.LocationFile.LocationExistsError;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.*;

import java.io.IOException;

public class HereIsCommand {
	public static final Logger LOGGER = LoggerFactory.getLogger("whereis");

  static Integer hereIsImplementation(
    LocationFile locationFile,
    CommandContext<ServerCommandSource> context,
    String owner,
    String alias
  ) {
    ServerCommandSource source = context.getSource();
    Vec3d position = source.getPosition();
    String dimension = source.getWorld().getDimensionKey().getValue().toString();

    String savingMessage =
      "Saving location \""
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
      String errorMessage = "Failed to save location \"" + alias + "\"";
      source.sendError(Text.literal(errorMessage));
      return -1;
    } catch (LocationExistsError e) {
      source.sendError(Text.literal(e.getMessage()));
      source.sendFeedback(
        // TODO: Send command suggestion
        Text.literal("Location already exists. Did you mean to move the marker to your current location?"),
        false
      );
      LOGGER.info("Location not saved: " + alias);
      return -1;
    }

    return 0;
  }

  static void register(
    LocationFile locationFile,
    CommandDispatcher<ServerCommandSource> dispatcher
  ) {
    dispatcher.register(
      literal("hereis")
        .then(
          argument("alias", StringArgumentType.greedyString())
            .executes(context -> {
              String alias = StringArgumentType.getString(context, "alias");
              return HereIsCommand.hereIsImplementation(locationFile, context, "*", alias);
            })
        )
        .then(
          literal("my")
            .then(
              argument("alias", StringArgumentType.greedyString())
                .executes(context -> {
                  String owner = context.getSource().getName();
                  String alias = StringArgumentType.getString(context, "alias");
                  return HereIsCommand.hereIsImplementation(locationFile, context, owner, alias);
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
                      return HereIsCommand.hereIsImplementation(locationFile, context, owner, alias);
                    })
                )
            )
        )
    );
  }
}
