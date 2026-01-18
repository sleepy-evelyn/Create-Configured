package dev.sleepy_evelyn.create_configured.mixin.client;

import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.GuiTaggable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = IconButton.class, remap = false)
public class IconButtonMixin implements GuiTaggable {

    @Unique private @Nullable String cc$tag;

    @Override
    public void cc$setTag(String tag) {
        cc$tag = tag;
    }

    @Override
    public boolean cc$matchesTag(String tag) {
        return tag != null && tag.equals(cc$tag);
    }
}
