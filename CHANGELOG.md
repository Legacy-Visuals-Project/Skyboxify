# Release 2.5

- Fix biomes parsing ("minecraft:plains" is now valid)
- Removed "optiboxes" command alias ("/skyboxify" is the only command now)
- Increased Skybox size
    - View-bobbing seems to affect it less when bigger
    - Fixes random far/near clipping
- 1.21.2/3 & 1.21.4 now use the "custom_skybox" custom core shader for sky rendering
- Config screen now shows current version & commit in top right

### Experimental

- New API
    - Get instance via SkyboxifyImpl#getInstance returns SkyboxifyApi
- SkyboxifyApi#getConfig
    - Returns the Skyboxify config instance
- SkyboxifyApi#getSkyboxManager
    - Returns the Skybox manager class that handles inactive/active skyboxes & handling
- SkyboxifyApi#registerDimensionMapping(int legacyId, Identifier modernId)
    - for other mods to use to map their older dimension ids to their modern counterpart
    - will throw exception if legacy id is already taken or if modern id is already assigned a value
    - world-1 is classified by default as "minecraft:the_nether" dimension
    - world0 is classified by default as "minecraft:overworld" dimension
    - world1 is classified by default as "minecraft:the_end" dimension
    - world4 is classified by default as "aether:the_aether" dimension
    - world7 is classified by default as "twilightforest:twilight_forest" dimension
- SkyboxifyApi#getModernDimension(int legacyId)
    - Returns mapped modern identifier or null if none

### Debug

- "daysLoop" is now always parsed even when "days" is empty (for debugging purposes)
- "axis" is now pretty printed in the debug sky layer info screen
- Added debug "dump" command which exports the active skyboxes into the "debug_skyboxify" folder
    - The data is the encoded value of the stored data via the CODEC
    - NOTE: If any value is the same as the default value, it won’t be encoded
- Debug menu now shows file name instead of source texture path
    - NOTE: Texture is still shown as top line of data