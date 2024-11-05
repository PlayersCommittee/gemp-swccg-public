package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.InsteadOfForceDrainingEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Location
 * Subtype: Site
 * Title: Arx: The Iron Legion
 */
public class Card302_022 extends AbstractSite {
    public Card302_022() {
        super(Side.LIGHT, Title.The_Iron_Legion, Title.Arx, Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLocationDarkSideGameText("Once per game, if you control, may retrieve a weapon or device into hand");
        setLocationLightSideGameText("Add 2 to each of your weapon destiny draws here.");
        addIcon(Icon.LIGHT_FORCE, 2);
		addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

 @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId)
    {
        GameTextActionId gameTextActionId = GameTextActionId.ARX_DARK_LEGION__RETRIEVE_A_WEAPON_OR_DEVICE_INTO_HAND;

        if (GameConditions.controls(game, playerOnDarkSideOfLocation, self)
            && GameConditions.canSearchLostPile(game, playerOnDarkSideOfLocation, self, gameTextActionId)
            && GameConditions.isOncePerGame(game, self, gameTextActionId))
        {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a weapon or device into hand");
            action.setActionMsg("Retrieve a weapon or device into hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new RetrieveCardIntoHandEffect(action, playerOnDarkSideOfLocation, Filters.or(Filters.weapon, Filters.device)));
            return Collections.singletonList(action);
        }
        return null;
    }
	
	    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.weapon, Filters.here(self)), 2));
        return modifiers;
    }
}