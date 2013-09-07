package net.aufdemrand.denizen.objects.properties;


import net.aufdemrand.denizen.objects.dColor;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.tags.Attribute;
import org.bukkit.Color;
import org.bukkit.material.Colorable;

public class ItemColor implements Property {

    public static boolean describes(dItem item) {
        if (item.getItemStack() instanceof Colorable)
             return true;
        else return false;
    }

    public static ItemColor getFrom(dItem item) {
        if (!describes(item)) return null;

        else return new ItemColor(item);
    }




    private ItemColor(dItem item) {
        colorable = item;
    }

    dItem colorable;
    dColor color;

    public dColor getColor() {
        return new dColor(Color.RED);

    }

    @Override
    public String getAttributes(Attribute attribute) {

        if (attribute == null) return "null";

        return color.getAttribute(attribute.fulfill(1));
    }


    @Override
    public String getPropertyString() {
        return getPropertyId() + '=' + color.identify() + ';';
    }

    @Override
    public String getPropertyId() {
        return "color";
    }
}
