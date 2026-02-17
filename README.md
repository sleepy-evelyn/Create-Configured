### Project has moved to [git.gay/Sleepy_Evelyn/create-configured](https://git.gay/Sleepy_Evelyn/create-configured)

# Create Configured
Additional configuration options & improvements for Create & addons.

[![Modrinth Badge](https://raw.githubusercontent.com/intergrav/devins-badges/7f68fe7afdbda525557fb41097594d2edccfda03/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/create-configured)
![Neoforge Badge](https://raw.githubusercontent.com/intergrav/devins-badges/7f68fe7afdbda525557fb41097594d2edccfda03/assets/cozy/supported/neoforge_vector.svg)
[![KoFi Badge](https://raw.githubusercontent.com/intergrav/devins-badges/7f68fe7afdbda525557fb41097594d2edccfda03/assets/cozy/donate/kofi-singular_vector.svg)](https://ko-fi.com/sleepyevelyn)

----

# Create Configured
Additional configuration options & improvements for Create & addons.
- ⏱️ **Increase Max ETA Time** - Increase the estimated time of arrival displayed for Trains from 10 > 60 minutes.

- 🚂💥 **Train collisions** - Disable collisions between trains. Massively improves performance on modded servers.

- 🚰 **Cache unfillable items** - Introduces a cache for items that can't be used in Spout filling recipes. Provides better performance at scale especially if Create is combined with other mods like AE2.

## Train motion profiles
Configurable Top Speed & Acceleration for individual trains which can be changed within a Stations GUI.

- ⛽ **Fuel consumption** - The rate of fuel consumption doubles if a train is given a higher Top Speed / Acceleration. Consumption halves if given a slower Acceleration.

## Train disassembly lock
You can now prevent other players or groups from disassembling a train.

- 🔧 **Unique to each train** - A disassembly lock is unique to each train and can be set whilst a train is at a station
  
- 🧑‍🤝‍🧑 **OPAC support** - Supports Open Parties & Claims. Includes a lock setting which only allows members of the same party to disassemble your train.
  
- ⚠️ **Staff bypasss option** - Staff members with `/op` and groups with permission node `create_configured.bypass_train_disassembly` can bypass any lock.
  
- 🛡️ **SnR & CC:Tweaked integration** - Provides computers and deployers holding a wrench with the context required to disassemble a train safely, respecting the disassembly lock.

## Q&A

<details>
<summary>☑️ Changing config options</summary>
<br>  
<b>All features are enabled by default on dedicated servers.</b> You can disable any feature if desired; note that some options are always disabled in singleplayer. <br/><br/><b>A server must be restarted for configuration changes to take effect.</b>
</details>

<details>
<br>
<summary>⚠️ CC:Tweaked (Computercraft) Disassembly Warning</summary> 
Create Configured version <code>1.2.2+</code> has support for CC:Tweaked. Only Create version <code>6.0.10+</code> will work as there is a bug that causes a crash on disassembly on older versions. 
</details>

## Config File
The config file is located in `/config/create_configured-server.toml`

```toml
# Whether to enable collisions between trains. Improves server performance. (Ignored in Singleplayer)
trainCollisions = false

# Introduces a cache for items that cannot be used in filling recipes.
cacheUnfillableItems = true

# Allows players to lock train disassembly for individual trains. (Ignored in Singleplayer)
lockTrainDisassembly = true

# Increases max ETA time shown for Scheduled Trains on Display Boards from 10 mins to 60 mins
increaseMaxETATime = true

# Train Motion Tweaks
[train_motion_tweaks]

  # Should regular Players be allowed to change a Trains max speed. (Ignored in Singleplayer)
  canPlayerChangeMaxSpeed = false

  # Should regular Players be allowed to change a Trains acceleration. (Ignored in Singleplayer)
  canPlayerChangeAcceleration = true
	
  # Top speed multiplier for slow Trains.
  #  Default: 0.5
  #  Range: 0.0 ~ 1.0
  slowTopSpeedMultiplier = 0.5
	
  # Top speed multiplier for fast Trains.
  #  Default: 1.75
  #  Range: 1.0 ~ 3.4028234663852886E38
  fastTopSpeedMultiplier = 1.75
	
  # Acceleration multiplier for slow Trains.
  #  Default: 0.5
  #  Range: 0.0 ~ 1.0
  slowAccelerationMultiplier = 0.5
	
  # Acceleration multiplier for fast Trains.
  #  Default: 1.25
  #  Range: 1.0 ~ 3.4028234663852886E38
  fastAccelerationMultiplier = 1.25
```
