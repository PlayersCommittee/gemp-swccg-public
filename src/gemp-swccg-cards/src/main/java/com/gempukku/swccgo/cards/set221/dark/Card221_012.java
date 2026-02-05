package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardUsingLandspeedEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Praji (V)
 */
public class Card221_012 extends AbstractImperial {
    public Card221_012() {
        super(Side.DARK, 2, 2, 2, 2, 4, "Commander Praji", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Vader's aide on the Devastator. Personally supervised search for Death Star plans on Tatooine by Vader's order. Was graduated with honors from Imperial Navy Academy on Carida.");
        setGameText("[Pilot] 2. While aboard Devastator, adds 1 to armor and hyperspeed and, once during your deploy phase, your trooper at a related site may make a regular move using landspeed.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.COMMANDER);
        addPersona(Persona.PRAJI);
        setMatchingStarshipFilter(Filters.Devastator);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ArmorModifier(self, Filters.and(Filters.Devastator, Filters.hasAboard(self)), 1));
        modifiers.add(new HyperspeedModifier(self, Filters.and(Filters.Devastator, Filters.hasAboard(self)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter trooperFilter = Filters.and(Filters.your(self), Filters.trooper, Filters.at(Filters.relatedSite(self)), Filters.canMoveUsingLandspeed(playerId, false, false, false, 0));

        // Check condition(s)
        if (GameConditions.isAboard(game, self, Filters.Devastator)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canTarget(game, self, trooperFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Move your trooper using landspeed");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose trooper to move using landspeed", trooperFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId_trooper, PhysicalCard targetedCard) {
                            action.allowResponses(new RespondableEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    PhysicalCard trooper = action.getPrimaryTargetCard(targetGroupId_trooper);
                                    Filter locationFilter = Filters.canMoveToUsingLandspeed(playerId, trooper, false, false, false,0, null);

                                    action.appendEffect(
                                            new MoveCardUsingLandspeedEffect(action, playerId, trooper, false, locationFilter));
                                }
                            });
                        }
                    }
            );

            actions.add(action);
        }

        return actions;
    }
}
