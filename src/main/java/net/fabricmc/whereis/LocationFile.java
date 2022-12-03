package net.fabricmc.whereis;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import net.minecraft.util.math.Vec3d;

public class LocationFile {
  private Logger LOGGER;
  private String path = null;
  private JSONArray locations = null;

  // Constructor
  public LocationFile(String path, Logger logger) throws IOException, ParseException {
    this.LOGGER = logger;
    this.path = path;

    this.load();
  }
  
  // Load the locations file
  public void load() throws IOException, ParseException {
    // Create the file if it doesn't exist
    File file = new File(this.path);
    if (!file.exists()) {
      LOGGER.info("Creating new location file at " + this.path);
      try {
        file.createNewFile();
        this.locations = new JSONArray();
        this.save();
      }
      catch (IOException e) {
        LOGGER.error("Failed to create new location file at " + this.path);
        throw e;
      }
      LOGGER.info("Created new location file at " + this.path);
      return;
    }

    // Load the file
    LOGGER.info("Loading location file at " + this.path);
    try {
      FileReader reader = new FileReader(this.path);
      JSONParser parser = new JSONParser();
      this.locations = (JSONArray) parser.parse(reader);
      reader.close();
    }
    catch (IOException e) {
      LOGGER.error("Failed to load location file at " + this.path);
      throw e;
    }
    catch (ParseException e) {
      LOGGER.error("Failed to parse location file at " + this.path);
      throw e;
    }
  }

  // Save the locations file
  public void save() throws IOException {
    LOGGER.info("Saving location file at " + this.path);

    try {
      FileWriter writer = new FileWriter(this.path);
      writer.write(this.locations.toJSONString());
      writer.flush();
      writer.close();
    }
    catch (IOException e) {
      LOGGER.error("Failed to save location file at " + this.path);
      throw e;
    }

    LOGGER.info("Saved location file at " + this.path);
  }

  // Add location
  public void addLocation(String alias, Vec3d location) throws IOException {
    try {
      JSONObject coords = new JSONObject();
      coords.put("x", location.x);
      coords.put("y", location.y);
      coords.put("z", location.z);

      JSONObject locationObject = new JSONObject();
      locationObject.put("alias", alias);
      locationObject.put("coords", coords);
      // TODO: Add dimension
      // TODO: Add world

      this.locations.add(locationObject);
      this.save();

      LOGGER.info("Added location \"" + alias + "\" to " + this.path);
    }
    catch (IOException e) {
      LOGGER.error("Failed to write to location file at " + this.path);
      throw e;
    }
  }

  // Find locations
  public ArrayList<Location> findLocations(String alias) {
    LOGGER.info("A");
    ArrayList<Location> foundLocations = new ArrayList<Location>();
    LOGGER.info("B");

    for (Object locationObject : this.locations) {
      JSONObject location = (JSONObject) locationObject;
      String locationAlias = (String) location.get("alias");
      if (locationAlias.toLowerCase().contains(alias.toLowerCase())) {
        JSONObject coords = (JSONObject) location.get("coords");
        double x = (double) coords.get("x");
        double y = (double) coords.get("y");
        double z = (double) coords.get("z");
        String foundAlias = (String) location.get("alias");
        foundLocations.add(new Location(foundAlias, new Vec3d(x, y, z)));
      }
    }

    return foundLocations;
  }

  // Get all locations
  public ArrayList<Location> getLocations() {
    ArrayList<Location> foundLocations = new ArrayList<Location>();

    for (Object locationObject : this.locations) {
      JSONObject location = (JSONObject) locationObject;
      JSONObject coords = (JSONObject) location.get("coords");
      double x = (double) coords.get("x");
      double y = (double) coords.get("y");
      double z = (double) coords.get("z");
      String alias = (String) location.get("alias");
      foundLocations.add(new Location(alias, new Vec3d(x, y, z)));
    }

    return foundLocations;
  }
}
