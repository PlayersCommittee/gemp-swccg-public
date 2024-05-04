package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Location
 * Subtype: Site
 * Title: Kef Bir: Oceanic Ruins
 */
public class Card218_022 extends AbstractSite {
    public Card218_022() {
        super(Side.LIGHT, "Kef Bir: Oceanic Ruins", Title.Kef_Bir, Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        setLocationDarkSideGameText("When deployed, may play a Defensive Shield from under your Starting Effect as if from hand.");
        setLocationLightSideGameText("If you occupy with a Jedi, you lose no more than 1 Force to That Thing's Operational.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_VII, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new LimitForceLossFromCardModifier(self, Filters.That_Things_Operational, new OccupiesWithCondition(playerOnLightSideOfLocation, self, Filters.Jedi), 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        // When deployed, may play a Defensive Shield from under your Starting Effect as if from hand.

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Starting_Effect));
            if (startingEffect != null) {
                Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
                if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
                    action.setText("Play a Defensive Shield");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseStackedCardEffect(action, playerOnDarkSideOfLocation, startingEffect, filter) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }

        return null;
    }

}