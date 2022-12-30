package net.fabricmc.whereis;

import org.json.simple.JSONObject;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec3d;

public class Location extends LocationMeta {
  public Vec3d coords;

  public Location(
    String owner,
    String alias,
    String dimension,
    Vec3d coords
  ) {
    super(owner, alias, dimension);
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
          .withItalic(false)
      )
      .append(" ")
      .append(
        Text
          .literal(this.owner)
          .setStyle(
            Style.EMPTY
              .withColor(TextColor.parse("gray"))
              .withBold(false)
              .withItalic(true)
          )
      )
      .append("\n")
      .append(
        Text
          .literal(this.getCoordsString())
          .setStyle(
            Style.EMPTY
              .withColor(TextColor.parse("green"))
              .withBold(false)
              .withItalic(false)
          )
      )
      .append("\n")
      .append(
        Text
          .literal(this.dimension)
          .setStyle(
            Style.EMPTY
              .withColor(TextColor.parse("yellow"))
              .withBold(false)
              .withItalic(true)
          )
      );
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    json.put("owner", this.owner);
    json.put("alias", this.alias);
    json.put("dimension", this.dimension);
    JSONObject coords = new JSONObject();
    coords.put("x", this.coords.x);
    coords.put("y", this.coords.y);
    coords.put("z", this.coords.z);
    json.put("coords", coords);
    return json;
  }

  public static Location fromJSON(JSONObject json) {
    String owner = (String) json.get("owner");
    String alias = (String) json.get("alias");
    String dimension = (String) json.get("dimension");
    JSONObject coords = (JSONObject) json.get("coords");
    double x = (double) coords.get("x");
    double y = (double) coords.get("y");
    double z = (double) coords.get("z");
    return new Location(
      owner,
      alias,
      dimension,
      new Vec3d(x, y, z)
    );
  }
}
