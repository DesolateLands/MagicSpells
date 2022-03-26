package com.nisovin.magicspells.spelleffects.collections;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.HashMap;

import com.nisovin.magicspells.spelleffects.EffectPosition;
import com.nisovin.magicspells.spelleffects.SpellEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import com.nisovin.magicspells.castmodifiers.ModifierSet;

public class EffectCollection {

    private ModifierSet modifiers = null;
    private EnumMap<EffectPosition, List<SpellEffect>> effects = new EnumMap<>(EffectPosition.class);
    private List<EffectCollection> effectCollections;

    public EffectCollection(ConfigurationSection section) {
        if (section.isConfigurationSection("effects")) {
            for (String key : section.getConfigurationSection("effects").getKeys(false)) {
                ConfigurationSection effConf = section.getConfigurationSection("effects." + key);
                EffectPosition pos = EffectPosition.getPositionFromString(effConf.getString("position", ""));
                if (pos != null) {
                    SpellEffect effect = SpellEffect.createNewEffectByName(effConf.getString("effect", ""));
                    if (effect != null) {
                        effect.loadFromConfiguration(effConf);
                        List<SpellEffect> e = this.effects.computeIfAbsent(pos, p -> new ArrayList<>());
                        e.add(effect);
                    }
                }
            }
        }
        if (section.isConfigurationSection("effect-collections")) {
            effectCollections = new ArrayList<>();
            for (String key : section.getConfigurationSection("effect-collections").getKeys(false)) {
                effectCollections
                        .add(new EffectCollection(section.getConfigurationSection("effect-collections." + key)));
            }
        }

        List<String> list = section.getStringList("modifiers");
        if (list != null) modifiers = new ModifierSet(list);
    }

    public List<SpellEffect> getEffects(EffectPosition pos, Entity entity) {
        if (checkModifiers(entity)) {
            if (effectCollections != null && !effectCollections.isEmpty()) {
                List<SpellEffect> spellEffects = new ArrayList<>();
                for (EffectCollection collection : effectCollections) {
                    spellEffects.addAll(collection.getEffects(pos, entity));
                }
                if (this.effects.containsKey(pos)) {
                    spellEffects.addAll(this.effects.get(pos));
                }
                return spellEffects;
            } else if (effects.containsKey(pos)) {
                return effects.get(pos);
            }
        }
        return new ArrayList<>();
    }

    public boolean checkModifiers(Entity entity) {
        if (modifiers != null && entity instanceof Player) {
            return modifiers.check((Player) entity);
        }
        return modifiers == null;
    }

}