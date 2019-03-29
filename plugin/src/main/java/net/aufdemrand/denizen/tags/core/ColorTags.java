package net.aufdemrand.denizen.tags.core;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.objects.dColor;
import net.aufdemrand.denizencore.objects.TagRunnable;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.ReplaceableTagEvent;
import net.aufdemrand.denizencore.tags.TagManager;
import net.aufdemrand.denizencore.utilities.CoreUtilities;

public class ColorTags {

    public ColorTags(Denizen denizen) {
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                colorTags(event);
            }
        }, "color");
    }

    public void colorTags(ReplaceableTagEvent event) {

        if (!event.matches("color") || event.replaced()) {
            return;
        }

        dColor color = null;

        if (event.hasNameContext()) {
            color = dColor.valueOf(event.getNameContext(), event.getAttributes().context);
        }

        if (color == null) {
            return;
        }

        Attribute attribute = event.getAttributes();
        event.setReplacedObject(CoreUtilities.autoAttrib(color, attribute.fulfill(1)));

    }
}
