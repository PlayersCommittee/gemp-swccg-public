package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceAttritionEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Dagobah: Yoda's Hut
 */
public class Card4_089 extends AbstractSite {
    public Card4_089() {
        super(Side.LIGHT, Title.Yodas_Hut, Title.Dagobah);
        setLocationDarkSideGameText("If you occupy, opponent's Yoda's Hut game text is canceled.");
        setLocationLightSideGameText("If Yoda here, once per turn, may subtract 2 from attrition against you at another location.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Yodas_Hut,
                new OccupiesCondition(playerOnDarkSideOfLocation, self), game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(final String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.not(self))
                && GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId)
                && GameConditions.isHere(game, self, Filters.Yoda)) {
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