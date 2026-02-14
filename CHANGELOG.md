# Release 2.5
- Fix biomes parsing ("minecraft:plains" is now valid)
- Removed "optiboxes" command alias
- 1.21.2/3 & 1.21.4 now use the "custom_skybox" custom core shader for sky rendering
- Added debug "dump" command which exports the active skyboxes into the "debug_skyboxify" folder
  - The data is the encoded value of the stored data via the CODEC
  - NOTE: If any value is the same as the default value, it won't be encoded
- Debug menu now shows file name instead of source texture path
  - NOTE: Texture is still shown as top line of data