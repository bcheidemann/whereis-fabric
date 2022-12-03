package net.fabricmc.whereis;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec3d;

public class Location {
  public String alias;
  public Vec3d coords;

  public Location(String alias, Vec3d coords) {
    this.alias = alias;
    this.coords = coords;
  }

  public String getCoordsString() {
    return String.format(
      "[%d, %d, %d]",
      Math.round(coords.x),
      Math.round(coords.y),
      Math.round(coords.z)
    );
  }

  public MutableText toMutableText() {
    return Text
      .literal(this.alias)
      .setStyle(
        Style.EMPTY
          .withColor(TextColor.parse("white"))
          .withBold(true)
      )
      .append("\n")
      .append(
        Text
          .literal(this.getCoordsString())
          .setStyle(
            Style.EMPTY
              .withColor(TextColor.parse("green"))
              .withBold(false)
          )
      );
  }
}
