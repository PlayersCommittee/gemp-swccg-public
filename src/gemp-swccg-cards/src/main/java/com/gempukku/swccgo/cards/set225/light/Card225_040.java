package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Location
 * Subtype: Site
 * Title: Cloud City: Beldon's Corridor
 */
public class Card225_040 extends AbstractSite {
    public Card225_040() {
        super(Side.LIGHT, Title.Beldons_Corridor, Title.Bespin, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLocationDarkSideGameText("Your troopers here are defense value +1. While you occupy, cancels Path Of Least Resistance.");
        setLocationLightSideGameText("While a [Cloud City] Rebel here, Their Fire Has Gone Out Of The Universe flips and may not flip back.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CLOUD_CITY, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_25);
        addKeywords(Keyword.CLOUD_CITY_LOCATION);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter filterYourTroopersHere = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.trooper, Filters.here(self));
        modifiers.add(new DefenseValueModifier(self, filterYourTroopersHere, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredBeforeTriggers(String playerOnDarkSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Path_Of_Least_Resistance)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.occupies(game, playerOnDarkSideOfLocation, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actionList = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isHere(game, self, Filters.and(Icon.CLOUD_CITY, Filters.Rebel))) {
                    
            PhysicalCard theirFireHasGoneOutOfTheUniverse = Filters.findFirstActive(game, self, Filters.Their_Fire_Has_Gone_Out_Of_The_Universe);
            if (theirFireHasGoneOutOfTheUniverse != null
                    && GameConditions.canBeFlipped(game, theirFireHasGoneOutOfTheUniverse)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip " + GameUtils.getFullName(theirFireHasGoneOutOfTheUniverse));
                action.setActionMsg("Flip " + GameUtils.getCardLink(theirFireHasGoneOutOfTheUniverse));
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, theirFireHasGoneOutOfTheUniverse));
                actionList.add(action);
            }
        }
        return actionList;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeFlippedModifier(self, new HereCondition(self, Filters.and(Icon.CLOUD_CITY, Filters.Rebel)), Filters.Hunt_Down_And_Destroy_The_Jedi));
        return modifiers;
    }
}