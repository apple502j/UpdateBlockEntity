# UpdateBlockEntity (Experimental)
Fixes [the bug](https://bugs.mojang.com/browse/MC-241670) that blocks chunks with modded block entities from being updated properly. **This is an experimental mod. Remember to back up worlds before opening, it can corrupt the world!**

Supports 1.18-pre5 (updating to newer versions soon-ish). Licensed under Apache 2.0.

## How does this work?
This mod works by:

1. updating worlds to data version 2841 (before the NBT changes).
2. copying the block entity NBTs to temporary tag.
3. updating worlds to the latest version from version 2841. This will cause ALL block entity NBTs to vanish if the chunk contained any modded block entities - including vanilla ones.
4. copying the block entity NBTs back to the intended location.
