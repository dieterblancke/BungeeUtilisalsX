package com.dbsoftwares.bungeeutilisals.api.experimental.item;

import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.google.common.collect.Maps;

import java.util.Map;

public enum Enchantment {

	PROTECTION_ENVIRONMENTAL(0),
	PROTECTION_FIRE(1),
	PROTECTION_FALL(2),
	PROTECTION_EXPLOSIONS(3),
    PROTECTION_PROJECTILE(4),
	OXYGEN(5),
	WATER_WORKER(6),
	THORNS(7),
	DEPTH_STRIDER(8),
    FROST_WALKER(9),
	BINDING_CURSE(10),
    DAMAGE_ALL(16),
    DAMAGE_UNDEAD(17),
    DAMAGE_ARTHROPODS(18),
    KNOCKBACK(19),
    FIRE_ASPECT(20),
    LOOT_BONUS_MOBS(21),
    SWEEPING_EDGE(22),
    DIG_SPEED(32),
    SILK_TOUCH(33),
    DURABILITY(34),
    LOOT_BONUS_BLOCKS(35),
    ARROW_DAMAGE(48),
    ARROW_KNOCKBACK(49),
    ARROW_FIRE(50),
    ARROW_INFINITE(51),
    LUCK(61),
    LURE(62),
    MENDING(70),
    VANISHING_CURSE(71),
    FAKE_GLOW(1000);

	private static final Map<Integer, Enchantment> byId = Maps.newHashMap();
	private static final Map<String, Enchantment> byName = Maps.newHashMap();
	private final int id;

	Enchantment(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
	    return Utils.capitalizeWords(toString().toLowerCase().replace("_", " "));
    }

	public static Enchantment getById(int id) {
		for (Enchantment enchantment : values()) {
		    if (enchantment.getId() == id) {
		        return enchantment;
            }
        }
        return null;
	}

	public static Enchantment getByName(String name) {
		for (Enchantment enchantment : values()) {
		    if (enchantment.getName().equals(name) || enchantment.toString().equals(name)) {
		        return enchantment;
            }
        }
        return null;
	}
}