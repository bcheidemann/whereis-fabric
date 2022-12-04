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
import org.slf4j.LoggerFactory;

public class LocationFile {
  static class LocationExistsError extends Exception {
    public LocationExistsError(String message) {
      super(message);
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger("whereis");
  private String path = null;
  private ArrayList<Location> locations = null;

  // Constructor
  public LocationFile(String path) throws IOException, ParseException {
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
        this.locations = new ArrayList<Location>();
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
      JSONArray locationsArray = (JSONArray) parser.parse(reader);
      reader.close();

      this.locations = new ArrayList<Location>();
      for (Object locationObject : locationsArray) {
        Location location = Location.fromJSON((JSONObject) locationObject);
        this.locations.add(location);
      }
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
      JSONArray locationsArray = new JSONArray();
      for (Location location : this.locations) {
        locationsArray.add(location.toJSON());
      }
      writer.write(locationsArray.toJSONString());
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
  public void addLocation(Location newLocation) throws IOException, LocationExistsError {
    try {
      for (Location location : this.locations) {
        if (
          location.alias.toLowerCase().equals(newLocation.alias.toLowerCase())
          && location.owner.equals(newLocation.owner)
        ) {
          String message = String.format(
            "Location %s already exists for %s in %s",
            newLocation.alias,
            newLocation.owner,
            newLocation.dimension
          );
          LOGGER.error(message);
          throw new LocationExistsError(message);
        }
      }

      this.locations.add(newLocation);
      this.save();

      LOGGER.info("Added location \"" + newLocation.alias + "\" to " + this.path);
    }
    catch (IOException e) {
      LOGGER.error("Failed to write to location file at " + this.path);
      throw e;
    }
  }

  // Move location
  public void moveLocation(Location newLocation) throws IOException {
    try {
      Boolean moved = false;
      Integer removed = 0;
      for (Location location : this.locations) {
        if (
          location.alias.toLowerCase().equals(newLocation.alias.toLowerCase())
          && location.owner.equals(newLocation.owner)
        ) {
          this.locations.remove(location);
          if (!moved) {
            this.locations.add(newLocation);
            moved = true;
          }
          else {
            removed++;
          }
        }
      }

      if (!moved) {
        this.locations.add(newLocation);
      }

      this.save();

      LOGGER.info("Moved location \"" + newLocation.alias + "\"");
      if (removed > 0) {
        LOGGER.warn("Removed " + removed + " duplicate locations");
      }
    }
    catch (IOException e) {
      LOGGER.error("Failed to write to location file at " + this.path);
      throw e;
    }
  }

  // Find locations
  public ArrayList<Location> findLocations(String alias, String dimension) {
    ArrayList<Location> foundLocations = new ArrayList<Location>();

    for (Location location : this.locations) {
      if (
        location.alias.toLowerCase().contains(alias.toLowerCase())
        && location.dimension.equals(dimension)
      ) {
        foundLocations.add(location);
      }
    }

    return foundLocations;
  }

  // Get all locations
  public ArrayList<Location> getLocations(String dimesnion) {
    ArrayList<Location> foundLocations = new ArrayList<Location>();

    for (Location location : this.locations) {
      if (location.dimension.equals(dimesnion)) {
        foundLocations.add(location);
      }
    }

    return foundLocations;
  }

  // Remove location
  public void removeLocation(String owner, String alias, String dimension) throws IOException {
    try {
      Integer removed = 0;
      for (Location location : this.locations) {
        if (
          location.alias.toLowerCase().equals(alias.toLowerCase())
          && location.owner.equals(owner)
        ) {
          this.locations.remove(location);
          removed++;
        }
      }

      this.save();

      LOGGER.info("Removed location \"" + alias + "\"");
      if (removed > 1) {
        LOGGER.warn("Removed " + (removed - 1) + " duplicate locations");
      }
    }
    catch (IOException e) {
      LOGGER.error("Failed to write to location file at " + this.path);
      throw e;
    }
  }
}
