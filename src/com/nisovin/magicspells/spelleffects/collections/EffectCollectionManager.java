package com.nisovin.magicspells.spelleffects.collections;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class EffectCollectionManager {

    Map<String, EffectCollection> collections = new HashMap<>();

    public EffectCollectionManager(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            collections.put(key,
                    new EffectCollection(section.getConfigurationSection(key)));
        }
    }

    public int count() {
        return collections.size();
    }

    public EffectCollection getCollection(String key) {
        if (collections.containsKey(key)) {
            return collections.get(key);
        }
        return null;
    }

}