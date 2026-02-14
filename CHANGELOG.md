# Release 2.5
- Fix biomes parsing ("minecraft:plains" is now valid)
- Removed "optiboxes" command alias
- Added debug "dump" command which exports the active skyboxes into the "debug_skyboxify" folder
  - The data is the encoded value of the stored data via the CODEC
  - NOTE: If any value is the same as the default value, it won't be encoded