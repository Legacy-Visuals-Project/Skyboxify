# Release 2.8

### General

- Moved from LightConfig to YetAnotherConfigLib as LightConfig is discontinued.
  - You will need to download yacl, as it is not included.
  - Your settings should persist.

### Other/API

- Removed getVersion as I don't know how to handle these changes
- New getConfigHandler and getConfigScreen methods
- SkyboxifyConfig#load is gone -> ``SkyboxifyImpl.getInstance().getConfigHandler().load()``
- SkyboxifyConfig#getConfigScreen is gone -> ``SkyboxifyImpl.getInstance().getConfigScreen(parent)``