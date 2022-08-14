package com.gempukku.swccgo.cards.set214.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Interrupt
 * Subtype: Used
 * Title: Our Only Hope (V)
 */
public class Card214_020 extends AbstractUsedInterrupt {
    public Card214_020() {
        super(Side.LIGHT, 4, Title.Our_Only_Hope, Uniqueness.UNIQUE);
        setLore("'The Emperor knew, as I did, if Anakin were to have any offspring, they would be a threat to him.'");
        setGameText("Relocate Prophecy Of The Force to a site. OR If Like My Father Before Me on table, [upload] Yoda's Hut or a Death Star II site.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_14);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OUR_ONLY_HOPE_V__UPLOAD_SITE;

        if (GameConditions.canSpot(game, self, Filters.title(Title.Like_My_Father_Before_Me))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take site into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Yoda's Hut or a Death Star II site into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Yodas_Hut, Filters.Death_Star_II_site), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.canSpot(game, self, Filters.Prophecy_Of_The_Force)) {
            final PhysicalCard prophecyOfTheForce = Filters.findFirstActive(game, self, Filters.Prophecy_Of_The_Force);
            boolean canRelocate = GameConditions.canSpot(game, self, Filters.canRelocateEffectTo(playerId, prophecyOfTheForce));
            Collection<Modifier> modifiers = game.getModifiersQuerying().getModifiersAffecting(game.getGameState(), prophecyOfTheForce);
            for (Modifier m : modifiers) {
                if (m.getModifyGameTextType(game.getGameState(), game.getModifiersQuerying(), prophecyOfTheForce) == ModifyGameTextType.PROPHECY_OF_THE_FORCE__MAY_NOT_BE_RELOCATED)
                    canRelocate = false;
            }

            if (canRelocate) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate " + GameUtils.getCardLink(prophecyOfTheForce) + " to a site");
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose site", Filters.canRelocateEffectTo(playerId, prophecyOfTheForce)) {
                    @Override
                    protected void cardTargeted(int targetGroupId, PhysicalCard site) {

                        final PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupId);
                        action.addAnimationGroup(prophecyOfTheForce);
                        action.addAnimationGroup(finalSite);
                        action.allowResponses(new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new AttachCardFromTableEffect(action, prophecyOfTheForce, finalSite)
                                );
                            }
                        });
                    }
                });

                actions.add(action);
            }
        }
        return actions;
    }
}