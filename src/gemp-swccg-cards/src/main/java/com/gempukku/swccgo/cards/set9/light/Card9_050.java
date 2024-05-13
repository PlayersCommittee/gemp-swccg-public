package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Used
 * Title: Head Back To The Surface
 */
public class Card9_050 extends AbstractUsedInterrupt {
    public Card9_050() {
        super(Side.LIGHT, 5, "Head Back To The Surface", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLore("'And see if you can get a few of those TIE fighters to follow you.'");
        setGameText("If your piloted unique (•) starfighter is present with opponent's piloted starfighter during your move phase at a non-cloud sector, relocate both to related system. OR Target your starfighter in battle. During this battle, your other starships may not be targeted by weapons.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter yourStarfighterFilter = Filters.and(Filters.your(self), Filters.piloted, Filters.unique, Filters.starfighter, Filters.at(Filters.non_cloud_sector));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {
            List<PhysicalCard> validStarfighters = new LinkedList<PhysicalCard>();
            Collection<PhysicalCard> yourStarfighters = Filters.filterActive(game, self, yourStarfighterFilter);
            for (PhysicalCard yourStarfighter : yourStarfighters) {
                PhysicalCard relatedSystem = Filters.findFirstFromTopLocationsOnTable(game, Filters.relatedSystem(yourStarfighter));
                if (relatedSystem != null) {
                    if (Filters.and(Filters.canBeTargetedBy(self), Filters.canBeRelocatedToLocation(relatedSystem, 0), Filters.presentWith(self, Filters.and(Filters.opponents(self),
                            Filters.piloted, Filters.starfighter, Filters.canBeTargetedBy(self), Filters.canBeRelocatedToLocation(relatedSystem, 0)))).accepts(game, yourStarfighter)) {
                        validStarfighters.add(yourStarfighter);
                    }
                }
            }
            if (!validStarfighters.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate starfighters to related system");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose your starfighter", Filters.in(validStarfighters)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard yourStarfighter) {
                                final PhysicalCard relatedSystem = Filters.findFirstFromTopLocationsOnTable(game, Filters.relatedSystem(yourStarfighter));
                                Filter opponentsStarfighter = Filters.and(Filters.opponents(self), Filters.piloted, Filters.starfighter,
                                        Filters.presentWith(yourStarfighter), Filters.canBeTargetedBy(self), Filters.canBeRelocatedToLocation(relatedSystem, 0));
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose opponent's starfighter", opponentsStarfighter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, final PhysicalCard opponentsStarfighter) {
                                                action.addAnimationGroup(yourStarfighter, opponentsStarfighter);
                                                action.addAnimationGroup(relatedSystem);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, Arrays.asList(yourStarfighter, opponentsStarfighter), relatedSystem, 0));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(yourStarfighter) + " and " + GameUtils.getCardLink(opponentsStarfighter) + " to " + GameUtils.getCardLink(relatedSystem),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                final PhysicalCard finalYourStarfighter = action.getPrimaryTargetCard(targetGroupId1);
                                                                final PhysicalCard finalOpponentsStarfighter = action.getPrimaryTargetCard(targetGroupId2);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, Arrays.asList(finalYourStarfighter, finalOpponentsStarfighter), relatedSystem));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                );
                actions.add(action);
            }
        }

        Filter yourStarfighterInBattle = Filters.and(Filters.your(self), Filters.starfighter, Filters.participatingInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, yourStarfighterInBattle)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Prevent starships from being targeted by weapons");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose your starfighter", yourStarfighterInBattle) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard yourStarfighter) {
                            action.addAnimationGroup(yourStarfighter);
                            // Allow response(s)
                            action.allowResponses("Prevent starships other than " + GameUtils.getCardLink(yourStarfighter) + " from being targeted by weapons",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalYourStarfighter = action.getPrimaryTargetCard(targetGroupId1);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.other(finalYourStarfighter), Filters.starship, Filters.participatingInBattle)),
                                                            "Prevents starships other than " + GameUtils.getCardLink(yourStarfighter) + " from being targeted by weapons"));
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