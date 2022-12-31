package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Starship
 * Subtype: Starfighter
 * Title: Stinger (V)
 */
public class Card200_137 extends AbstractStarfighter {
    public Card200_137() {
        super(Side.DARK, 3, 2, 3, null, 4, 5, 4, "Stinger", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Constructed by a secretive Surronian hive craftguild. Equipped with H2-1 hyperdrive system and emergency braking jets. Guri's personal starship. Gift from Prince Xizor.");
        setGameText("May add 1 pilot. Guri deploys -3 aboard. While Guri piloting, during any control phase may draw top card of Reserve Deck (if Chewie or Leia on table, Force Pile instead) and immune to attrition < 5.");
        addIcons(Icon.REFLECTIONS_II, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_0);
        addModelType(ModelType.SURRONIAN_CONQUEROR);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Guri);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Guri, -3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Guri, -3, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasPiloting(game, self, Filters.Guri)) {
            if (GameConditions.canSpot(game, self, Filters.or(Filters.Chewie, Filters.Leia))) {
                if (GameConditions.hasForcePile(game, playerId)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Draw top card of Force Pile");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new DrawCardIntoHandFromForcePileEffect(action, playerId));
                    return Collections.singletonList(action);
                }
            }
            else {
                if (GameConditions.hasReserveDeck(game, playerId)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Draw top card of Reserve Deck");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Guri), 5));
        return modifiers;
    }
}
