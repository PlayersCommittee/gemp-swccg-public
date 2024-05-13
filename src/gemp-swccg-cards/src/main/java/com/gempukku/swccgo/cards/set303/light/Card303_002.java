package com.gempukku.swccgo.cards.set303.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceAttritionEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.common.Keyword;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Shadow Academy
 * Type: Location
 * Subtype: Site
 * Title: Arx: The Shadow Academy
 */
public class Card303_002 extends AbstractSite {
    public Card303_002() {
        super(Side.LIGHT, Title.Shadow_Academy, Title.Arx, Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.V);
        setLocationDarkSideGameText("If you occupy, opponent's The Shadow Academy game text is canceled.");
        setLocationLightSideGameText("If Alethia here, once per turn, may subtract 2 from attrition against you at another location.");
        addIcon(Icon.LIGHT_FORCE, 2);
		addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET);
		addKeywords(Keyword.SHADOW_ACADEMY_LOCATION);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Shadow_Academy,
                new OccupiesCondition(playerOnDarkSideOfLocation, self), game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(final String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.not(self))
                && GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId)
                && GameConditions.isHere(game, self, Filters.ALETHIA)) {
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