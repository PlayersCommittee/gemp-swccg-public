package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddToBlownAwayForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToLimitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Location
 * Subtype: Site
 * Title: Death Star: War Room (V)
 */
public class Card208_048 extends AbstractSite {
    public Card208_048() {
        super(Side.DARK, "Death Star: War Room", Title.Death_Star, Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Krennic deploys free here. Opponent may not limit Force generation at Death Star locations.");
        setLocationLightSideGameText("If a system was just 'blown away' by Superlaser, you lose 3 Force.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.WAR_ROOM);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Krennic, self));
        modifiers.add(new ForceGenerationImmuneToLimitModifier(self, Filters.Death_Star_location, Filters.opponents(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBlownAwayBySuperlaserCalculateForceLossStep(game, effectResult, Filters.system)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            // Perform result(s)
            action.appendEffect(
                    new AddToBlownAwayForceLossEffect(action, playerOnLightSideOfLocation, 3));
            return Collections.singletonList(action);
        }
        return null;
    }
}