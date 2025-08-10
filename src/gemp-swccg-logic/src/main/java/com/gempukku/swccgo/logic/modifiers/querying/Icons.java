package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Icons extends BaseQuery {

	default boolean hasLightAndDarkForceIcons(GameState gameState, PhysicalCard physicalCard, PhysicalCard ignoreForceIconsFromCard) {
		// query() is used as part of checking if a location is a battleground.  We use query() since
		// we want to skip checking if location is rotated since it does not affect whether it is a battleground
		// for not. Also, in case there is a card that affects whether a location is rotated, based on
		// checking the battlegrounds in play, we want to avoid a loop between the two.
		if (!getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_ICONS, physicalCard).isEmpty()) {
			return false;
		}

		int numLightIcons = physicalCard.getBlueprint().getIconCount(Icon.LIGHT_FORCE);
		int numDarkIcons = physicalCard.getBlueprint().getIconCount(Icon.DARK_FORCE);

		//if location is blown away or collapsed ignore the printed force icons but it can still have icons added
		if (physicalCard.isBlownAway() || physicalCard.isCollapsed()) {
			numLightIcons = 0;
			numDarkIcons = 0;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_ICON, physicalCard)) {
			numLightIcons -= modifier.getIconCountModifier(gameState, query(), physicalCard, Icon.LIGHT_FORCE);
			numDarkIcons -= modifier.getIconCountModifier(gameState, query(), physicalCard, Icon.DARK_FORCE);
		}

		if (numLightIcons > 0 && numDarkIcons > 0) {
			return true;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.GIVE_ICON, physicalCard)) {
			boolean skipAddingDarkIcon = false;
			boolean skipAddingLightIcon = false;
			for(Modifier m: getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ADD_ICON, modifier.getSource(gameState))) {
				if (m.getIcon() == Icon.DARK_FORCE)
					skipAddingDarkIcon = true;
				if (m.getIcon() == Icon.LIGHT_FORCE)
					skipAddingLightIcon = true;
			}
			if (ignoreForceIconsFromCard == null
					|| modifier.getSource(gameState).getCardId() != ignoreForceIconsFromCard.getCardId()) {
				if (!skipAddingLightIcon)
					numLightIcons += modifier.getIconCountModifier(gameState, query(), physicalCard, Icon.LIGHT_FORCE);
				if (!skipAddingDarkIcon)
					numDarkIcons += modifier.getIconCountModifier(gameState, query(), physicalCard, Icon.DARK_FORCE);
			}
		}

		// Check if Force icons on location are added until equalized
		if (!getModifiersAffectingCard(gameState, ModifierType.EQUALIZE_FORCE_ICONS, physicalCard).isEmpty()) {
			numLightIcons = numDarkIcons = Math.max(numLightIcons, numDarkIcons);
		}

		return (numLightIcons > 0 && numDarkIcons > 0);
	}

	default boolean hasIcon(GameState gameState, PhysicalCard physicalCard, Icon icon) {
		return getIconCount(gameState, physicalCard, icon) > 0;
	}

	default int getIconCount(GameState gameState, PhysicalCard physicalCard, Icon icon) {
		return getIconCount(gameState, physicalCard, icon, false, new ModifierCollectorImpl());
	}

	default int getIconCount(GameState gameState, PhysicalCard physicalCard, Icon icon, ModifierCollector modifierCollector) {
		return getIconCount(gameState, physicalCard, icon, false, modifierCollector);
	}

	private int getIconCount(GameState gameState, PhysicalCard physicalCard, Icon iconInput, boolean skipEqualizeCheck, ModifierCollector modifierCollector) {
		Icon icon = iconInput;
		if (physicalCard.isCrossedOver()) {
            /* May 2021 rules update
            -Rebel becomes Imperial (or vice versa)
            -Clone Army becomes Separatist (or vice versa)
            -Resistance becomes First Order (or vice versa)
            -Jedi Master becomes Dark Jedi Master (or vice versa)
            -Republic becomes Sith (or vice versa)
             */
			if (icon == Icon.IMPERIAL)
				icon = Icon.REBEL;
			else if (icon == Icon.REBEL)
				icon = Icon.IMPERIAL;
			else if (icon == Icon.SEPARATIST)
				icon = Icon.CLONE_ARMY;
			else if (icon == Icon.CLONE_ARMY)
				icon = Icon.SEPARATIST;
			else if (icon == Icon.FIRST_ORDER)
				icon = Icon.RESISTANCE;
			else if (icon == Icon.RESISTANCE)
				icon = Icon.FIRST_ORDER;
			else if (icon == Icon.DARK_JEDI_MASTER)
				icon = Icon.JEDI_MASTER;
			else if (icon == Icon.JEDI_MASTER)
				icon = Icon.DARK_JEDI_MASTER;
			else if (icon == Icon.SITH)
				icon = Icon.REPUBLIC;
			else if (icon == Icon.REPUBLIC)
				icon = Icon.SITH;
		}

		// Special case for Big One: Asteroid Cave or Space Slug Belly (planet site or creature site)
		if (Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, query(), physicalCard)
				&& Filters.in_play.accepts(gameState, query(), physicalCard)) {
			if (physicalCard.isSpaceSlugBelly() && icon == Icon.PLANET) {
				return 0;
			}
			if (!physicalCard.isSpaceSlugBelly() && icon == Icon.CREATURE_SITE) {
				return 0;
			}
		}

		// If a system is 'blown away' it becomes a space system
		if (physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM && physicalCard.isBlownAway()) {
			if (icon == Icon.SPACE) {
				return 1;
			}
			else if (icon == Icon.MOBILE || icon == Icon.PLANET) {
				return 0;
			}
		}

		// If a site is 'blown away' it becomes an exterior site
		if (physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SITE && physicalCard.isBlownAway()) {
			if (icon == Icon.EXTERIOR_SITE) {
				return 1;
			}
			else if (icon == Icon.INTERIOR_SITE) {
				return 0;
			}
		}

		// Certain icons never get added/removed, so avoid circular checking (e.g. Ithorian), skip checking if these icons were added/removed
		if (icon != Icon.CREATURE_SITE && icon != Icon.EXTERIOR_SITE && icon != Icon.INTERIOR_SITE && icon != Icon.MOBILE
				&& icon != Icon.PLANET && icon != Icon.PRESENCE && icon != Icon.SPACE && icon != Icon.STARSHIP_SITE
				&& icon != Icon.UNDERGROUND && icon != Icon.UNDERWATER && icon != Icon.VEHICLE_SITE) {

			boolean iconsCanceled = false;
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_ICONS, physicalCard)) {
				if (modifier.getIcon() == icon) {
					modifierCollector.addModifier(modifier);
					iconsCanceled = true;
				}
			}
			if (iconsCanceled) {
				return 0;
			}
		}

		int result;
		if (icon == Icon.LIGHT_FORCE || icon == Icon.DARK_FORCE) {

			// Light and Dark Force only exist on locations
			if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION) {
				return 0;
			}

			boolean iconsCanceled = false;
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_ICONS, physicalCard)) {
				if (modifier.isForPlayer(icon == Icon.LIGHT_FORCE ? gameState.getLightPlayer() : gameState.getDarkPlayer())) {
					modifierCollector.addModifier(modifier);
					iconsCanceled = true;
				}
			}
			if (iconsCanceled) {
				return 0;
			}

			if (physicalCard.isBlownAway() || physicalCard.isCollapsed()) {
				result = 0;
			} else if (isRotatedLocation(gameState, physicalCard)) {
				if (icon == Icon.LIGHT_FORCE)
					result = physicalCard.getBlueprint().getIconCount(Icon.DARK_FORCE);
				else
					result = physicalCard.getBlueprint().getIconCount(Icon.LIGHT_FORCE);
			}
			else {
				result = physicalCard.getBlueprint().getIconCount(icon);
			}
		}
		else {
			result = physicalCard.getBlueprint().getIconCount(icon);
		}

		// Certain icons never get added/removed, so avoid circular checking (e.g. Ithorian), skip checking if these icons were added/removed
		if (icon != Icon.CREATURE_SITE && icon != Icon.EXTERIOR_SITE && icon != Icon.INTERIOR_SITE && icon != Icon.MOBILE
				&& icon != Icon.PLANET && icon != Icon.PRESENCE && icon != Icon.SPACE && icon != Icon.STARSHIP_SITE
				&& icon != Icon.UNDERGROUND && icon != Icon.UNDERWATER && icon != Icon.VEHICLE_SITE) {

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_ICON, physicalCard)) {
				if (modifier.getIcon() == icon) {
					modifierCollector.addModifier(modifier);
					result -= modifier.getIconCountModifier(gameState, query(), physicalCard, icon);
				}
			}

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.GIVE_ICON, physicalCard)) {
				if (modifier.getIcon() == icon) {
					boolean skipAddingIcon = false;
					for(Modifier m: getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ADD_ICON, modifier.getSource(gameState))) {
						if (m.getIcon() == icon)
							skipAddingIcon = true;
					}
					if (!skipAddingIcon) {
						modifierCollector.addModifier(modifier);
						result += modifier.getIconCountModifier(gameState, query(), physicalCard, icon);
					}
				}
			}
		}

		// Check if Force icons on location are added until equalized
		if (!skipEqualizeCheck
				&& physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION
				&& (icon == Icon.LIGHT_FORCE || icon == Icon.DARK_FORCE)) {
			boolean equalize = false;
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EQUALIZE_FORCE_ICONS, physicalCard)) {
				modifierCollector.addModifier(modifier);
				equalize = true;
			}
			if (equalize) {
				result = Math.max(result, getIconCount(gameState, physicalCard, (icon == Icon.LIGHT_FORCE ? Icon.DARK_FORCE : Icon.LIGHT_FORCE), true, modifierCollector));
			}
		}

		return Math.max(0, result);
	}

	default boolean isRotatedLocation(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return false;

		return ((getModifiersAffectingCard(gameState, ModifierType.ROTATE_LOCATION, physicalCard).size() % 2) != 0);
	}
}
