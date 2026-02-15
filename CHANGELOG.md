# Release 2.5
- Fix biomes parsing ("minecraft:plains" is now valid)
- Removed "optiboxes" command alias ("/skyboxify" is the only command now)
- Increased Skybox size
  - View-bobbing seems to affect it less when bigger
  - Fixes random far/near clipping
- 1.21.2/3 & 1.21.4 now use the "custom_skybox" custom core shader for sky rendering
### Experimental
- SkyboxResourceHelper#registerDimensionMapping(int legacyId, Identifier modernId)
  - method is static for other mods to use to map their older dimension ids to their modern counterpart
  - will throw exception if legacy id is already taken or if modern id is already assigned a value
- world-1 is classified as "minecraft:the_nether" dimension by default
- world0 is classified as "minecraft:overworld" dimension by default
- world1 is classified as "minecraft:the_end" dimension by default
- world4 is classified as "aether:the_aether" dimension by default
- world7 is classified as "twilightforest:twilight_forest" dimension by default
### Debug
- "daysLoop" is now always parsed even when "days" is empty (for debugging purposes)
- "axis" is now pretty printed in the debug sky layer info screen
- Added debug "dump" command which exports the active skyboxes into the "debug_skyboxify" folder
  - The data is the encoded value of the stored data via the CODEC
  - NOTE: If any value is the same as the default value, it won’t be encoded
- Debug menu now shows file name instead of source texture path
  - NOTE: Texture is still shown as top line of data