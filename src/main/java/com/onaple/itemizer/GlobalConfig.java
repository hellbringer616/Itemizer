package com.onaple.itemizer;

import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;

import java.util.HashMap;
import java.util.Map;

public class GlobalConfig {

    private enum flags {
        Enchantments,
        Attributes_modifiers,
        Unbreakable,
        CanDestroy,
        CanPlaceOn,
        Others
    }

    private boolean descriptionRewrite;
    private int hiddenFlagsValue;
    private Map<EnchantmentType, String> enchantRewrite;
    private Map<String, String> modifierRewrite;
    private String unbreakableRewrite;

    private final HashMap flagsMap = new HashMap<flags, Integer>() {{
        put(flags.Enchantments, 1);
        put(flags.Attributes_modifiers, 2);
        put(flags.Unbreakable, 4);
        put(flags.CanDestroy, 8);
        put(flags.CanPlaceOn, 16);
        put(flags.Others, 32);
    }};

    private Map<String, Boolean> hiddenFlags;

    public Map<EnchantmentType, String> getEnchantRewrite() {
        return enchantRewrite;
    }

    public Map<String, String> getModifierRewrite() {
        return modifierRewrite;
    }

    public GlobalConfig(boolean descriptionRewrite,
                        Map<String, Boolean> flagToHide,
                        Map<EnchantmentType, String> enchantRewrite,
                        Map<String, String> modifierRewrite,
                        String unbreakableRewrite) {

        this.descriptionRewrite = descriptionRewrite;
        int flagsValue = 0;
        for (Map.Entry<String, Boolean> flag : flagToHide.entrySet()) {

            if (flag.getValue()) {
                flagsValue += (int) flagsMap.get(flags.valueOf(flag.getKey()));
            }
        }

        this.unbreakableRewrite = unbreakableRewrite;

        this.hiddenFlagsValue = flagsValue;
        this.hiddenFlags = flagToHide;
        this.enchantRewrite = enchantRewrite;
        this.modifierRewrite = modifierRewrite;


    }

    public String getUnbreakableRewrite() {
        return unbreakableRewrite;
    }

    public int getHiddenFlagsValue() {
        return hiddenFlagsValue;
    }

    public Map<String, Boolean> getHiddenFlags() {
        return hiddenFlags;
    }

    public boolean isDescriptionRewrite() {
        return descriptionRewrite;
    }

}
