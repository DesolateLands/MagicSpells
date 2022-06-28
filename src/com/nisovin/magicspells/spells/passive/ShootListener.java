package com.nisovin.magicspells.spells.passive;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spellbook;
import com.nisovin.magicspells.spells.PassiveSpell;
import com.nisovin.magicspells.util.OverridePriority;

// No trigger variable is used here
public class ShootListener extends PassiveListener {

	Map<PassiveSpell, ShootForce> spells = new HashMap<>();

	@Override
	public void registerSpell(PassiveSpell spell, PassiveTrigger trigger, String var) {
		ShootForce shootForce = new ShootForce();
		if (var != null && !var.isEmpty()) {
			String[] split = var.split("-");

			if (split.length == 2) {
				try {
					shootForce.min = Float.parseFloat(split[0]);
					shootForce.max = Float.parseFloat(split[1]);
				} catch (NumberFormatException ex) {
					throw new IllegalArgumentException("The var " + var + " is not a valid force range for shoot passives!");
				}
			} else {
				try {
					shootForce.max = Float.parseFloat(var);
				} catch (NumberFormatException ex) {
					throw new IllegalArgumentException("The var " + var + " is not a valid force range for shoot passives!");
				}
			}
		}
		this.spells.put(spell, shootForce);
	}
	
	@OverridePriority
	@EventHandler
	public void onShoot(final EntityShootBowEvent event) {
		if (spells.isEmpty()) return;
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player)event.getEntity();
		Spellbook spellbook = MagicSpells.getSpellbook(player);
		for (PassiveSpell spell : spells.keySet()) {
			if (!isCancelStateOk(spell, event.isCancelled())) continue;
			if (!spellbook.hasSpell(spell)) continue;
			if (!spells.get(spell).matches(event.getForce())) continue;
			boolean casted = spell.activate(player, event.getForce());
			if (!PassiveListener.cancelDefaultAction(spell, casted)) continue;
			event.setCancelled(true);
			event.getProjectile().remove();
		}
	}

	class ShootForce {
		float min = 0f;
		float max = 1.01f;

		boolean matches(float force) {
			return force >= min && force < max;
		}
	}
	
}
