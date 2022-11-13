package com.nisovin.magicspells.spells.passive;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;
import com.desolatelands.desolateplugin.event.ProfileSwitchedEvent;

// No trigger variable is currently used
public class ProfileSwitchedListener extends PassiveListener {
    List<PassiveSpell> spells = new ArrayList<>();

    @Override
    public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
        spells.add(spell);
    }

    @OverridePriority
    @EventHandler
    public void onProfileSwitch(ProfileSwitchedEvent event) {
        Player player = event.getPlayer();
        Spellbook spellbook = MagicSpells.getSpellbook(player);
        spells.stream().filter(spellbook::hasSpell).forEachOrdered(spell -> spell.activate(player));
    }
}