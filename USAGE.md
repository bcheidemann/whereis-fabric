# Commands

## `/whereis`

There `/whereis` command is used to find locations. Locations can be set using the `/hereis` command which is covered in the [following section](#hereis).

The basic format of the `/whereis` command is:

```
/whereis <name>
```

For example, if a location called `World Spawn` exists, the following command would return it's coordinates:

```
/whereis spawn
```

Note that you do not need to remember the full name of the location - the command will return any partial matches. In fact, you can even omit the location name entirely to get a full list of locations in the current dimension.

```
/whereis
```

It's important to remember that `/whereis` will only search for locations in your current dimension.

### Searching Locations by Owner

Every location has an owner associated with it. The default location owner is `*`, which means the location does not have a named owner. However, locations may have any one word owner (e.g. a player or group name).

If you want to search for only locations owned by your player (i.e. the owner associated with the location is your player name) you can use the following command:

```
/whereis my <name>
```

For example, if you have a location named `Megabase` and another named `Starter Base`, you can find the locations of both by using the command:

```
/whereis my base
```

Again, note that this will return the location of partial matches, hence it will return the coordinates of both base locations. Furthermore, the `<name>` argument can be omitted to return all locations associated with your player name.

```
/whereis my
```

You can also search for locations with a specific named owner.

```
/whereis the <owner> <name>
```

Unlike the `<name>` argument, the `<owner>` argument must exactly match the owners name (though it is not case sensitive).

For example, if you want to find the location of `DippyBlether`s `Base`, you could use the following command:

```
/whereis the dippyblether base
```

As with the `/whereis my` sub-command, you can omit the `<name>` argument to return a list of all locations associated with a given owner. For instance, the following command would return all locations associated with the owner `DippyBlether`:

```
/whereis the dippyblether
```

Note that the `<name>` argument doesn't need to be a player name; it can be any one word name. For example, if you and some other players have a group named the "Bacon Briggade", you may want to save the location of shared projects using the owner name `BaconBriggade`. Then you could find all locations associated with the Bacon Briggade using the following command:

```
/whereis the baconbriggade
```

Or, if you want to find the location of the Bacon Briggade `Secret Base`, you might use the command:

```
/whereis the baconbriggade secret base
```

Of course, your secret base won't be very secret, since any player can run this command!

## `/hereis`

The `/hereis` command is used to set a location so it can be found later by the [`/whereis` command](#whereis).

The basic format of the `/hereis` command is:

```
/hereis <name>
```

For example, if you want to store a location called `World Spawn`, the following command would store it's coordinates:

```
/hereis World Spawn
```

Note that using the command like this will associate it with the `*` owner. The `*` owner is a special owner name which indicates that a location does not have a specific owner.

### Setting Locations by Owner

As previously discussed, every location has an owner associated with it. The default location owner is `*`, which means the location does not have a named owner. However, locations may have any one word owner (e.g. a player or group name).

If you want to set a locations owned by your player (i.e. the owner associated with the location is your player name) you can use the following command:

```
/hereis my <name>
```

For example, if you want to store a location named `Starter Base`, you can use the command:

```
/hereis my Starter Base
```

You can also store locations with arbitrary owners.

```
/hereis the <owner> <name>
```

For example, if you want store the location of `DippyBlether`s `Base`, you could use the following command:

```
/hereis the DippyBlether Base
```

Note that the `<name>` argument doesn't need to be a player name; it can be any one word name. For example, if you and some other players have a group named the "Bacon Briggade", you may want to save the location of shared projects using the owner name `BaconBriggade`. Then you could set the location of the Bacon Briggade `Secret Base` as follows:

```
/hereis the BaconBriggade Secret Base
```

Of course, your secret base won't be very secret, since any player can find it using the [`/whereis` command](#whereis)!

## `/relocate`

The `/relocate` command is used to move an already set location to your current coordinates. The format of this command mirrors that of the `/hereis` command.

## `/forget`

The `/forget` command is used to remove an already set location. The format of this command mirrors that of the `/hereis` command.

# Information for Admins

## Storage

The locations are stored in a JSON file at the root server directory called `whereis.locations.json`. Changes made to this file while the server is running will not be picked up until the server is restarted, and may be lost. If editing this file manually, it is recommended to make the changes on a copy, stop the server, switch the modified copy for the original, and then start the server again.

## Backups

The `whereis.locations.json` file should be included in server backups. If this file is lost, it will not be possible to restore the locations list.

## Permissions

This mod does not require any priveleges or permissions. Additionally, it is important to know that any player can execute the command without restriction. This means that a malicious player could remove locations set by another player. If this is not acceptable, you are free to edit the source as required, raise a [GitHub issue](https://github.com/bcheidemann/whereis-fabric/issues) or contact the author who will be happy to make any required changes (within reason).

## Bugs & Feature Requests

If you find any bugs or have a feature request, please raise a [GitHub issue](https://github.com/bcheidemann/whereis-fabric/issues) or contact the author.
