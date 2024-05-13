package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceAttritionEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: Site
 * Title: Dagobah: Yoda's Hut (V)
 */
public class Card216_026 extends AbstractSite {
    public Card216_026() {
        super(Side.LIGHT, Title.Yodas_Hut, Title.Dagobah, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("If Yoda here or [Set 16] Yoda 'communing,' once per turn, may subtract 2 from attrition against you.");
        setLocationLightSideGameText("If Yoda here or [Set 16] Yoda 'communing,' Broken Concentration is canceled.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredBeforeTriggers(String playerOnDarkSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Broken_Concentration)
                && (GameConditions.isHere(game, self, Filters.Yoda)
                || game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.and(Icon.VIRTUAL_SET_16, Filters.Yoda)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Broken_Concentration, Title.Broken_Concentration);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.Broken_Concentration)
                && (GameConditions.isHere(game, self, Filters.Yoda)
                || game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.and(Icon.VIRTUAL_SET_16, Filters.Yoda)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Broken_Concentration, Title.Broken_Concentration);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(final String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId)
                && (GameConditions.isHere(game, self, Filters.Yoda)
                || game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.and(Icon.VIRTUAL_SET_16, Filters.Yoda)))) {
            final BattleState battleState = game.getGameState().getBattleState();
            if (battleState.hasAttritionTotal(game.getOpponent(playerOnLightSideOfLocation))) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
                action.setText("Reduce attrition");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ReduceAttritionEffect(action, playerOnLightSideOfLocation, 2));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}