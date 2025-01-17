package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromOutsideTheGameEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Balanced Attack & Darklighter Spin
 */
public class Card224_009 extends AbstractUsedOrLostInterrupt {
    public Card224_009() {
        super(Side.LIGHT, 5, "Balanced Attack & Darklighter Spin", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        addComboCardTitles("Balanced Attack", Title.Darklighter_Spin);
        setGameText("USED: [Upload] a unique (•) unpiloted starfighter. " +
                "LOST: During battle at a system or sector, if you are about to draw a card for battle destiny, target your participating starship to instead use the ability number of its highest-ability pilot. " +
                "OR Once per game, if Alderaan 'blown away' (or if opponent has deployed two battleground systems and no battleground sites), deploy a non-unique Corellian Corvette from outside your deck.");
        addIcon(Icon.VIRTUAL_SET_24);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.BALANCED_ATTACK__UPLOAD_UNIQUE_STARFIGHTER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a unique unpiloted starfighter into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.unique, Filters.unpiloted, Filters.starfighter), true));
                        }
                    }
            );
            actions.add(action);
        }

        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId1 = GameTextActionId.BALANCED_ATTACK__DOWNLOAD_CORVETTE;

        // Check condition(s)
        if (((GameConditions.hasDeployedAtLeastXCardsThisGame(game, opponent, 2, Filters.system)
                && !GameConditions.hasDeployedAtLeastXCardsThisGame(game, opponent, 1, Filters.battleground_site))
                || GameConditions.isBlownAway(game, Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Alderaan, true))))
                && GameConditions.isOncePerGame(game, self, gameTextActionId1)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId1, CardSubtype.LOST);
            action.setText("Deploy card from outside your deck");
            // Append Usage
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Allow response(s)
            action.allowResponses("deploy a non-unique Corellian Corvette from outside your deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromOutsideTheGameEffect(action, Filters.and(Filters.non_unique, Filters.Corellian_corvette), 0)
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final Filter yourStarshipInBattle = Filters.and(Filters.your(self), Filters.starship, Filters.participatingInBattle,
                Filters.or(Filters.hasPiloting(self, Filters.abilityMoreThan(0)), Filters.hasAbilityOrHasPermanentPilotWithAbility));

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.isDuringBattleAt(game, Filters.system_or_sector)
                && GameConditions.canTarget(game, self, yourStarshipInBattle)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Substitute destiny");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", yourStarshipInBattle) {
                        @Override
                        protected void cardTargeted(final int starshipTargetGroupId, PhysicalCard starship) {
                            final float highestAbilityPiloting = game.getModifiersQuerying().getHighestAbilityPiloting(game.getGameState(), starship, false, false);
                            float highestAbilityCharacterPiloting = game.getModifiersQuerying().getHighestAbilityPiloting(game.getGameState(), starship, false, true);
                            float highestAbilityPermanentPilotPiloting = game.getModifiersQuerying().getHighestAbilityPiloting(game.getGameState(), starship, true, false);

                            Filter targetFilter;
                            String text;

                            if (highestAbilityCharacterPiloting == highestAbilityPermanentPilotPiloting) {
                                targetFilter = Filters.or(starship, Filters.and(Filters.piloting(starship), Filters.abilityEqualTo(highestAbilityCharacterPiloting)));
                                text = "starship (permanent pilot) or pilot character aboard";
                            } else if (highestAbilityCharacterPiloting > highestAbilityPermanentPilotPiloting) {
                                targetFilter = Filters.and(Filters.piloting(starship), Filters.abilityEqualTo(highestAbilityCharacterPiloting));
                                text = "pilot character aboard";
                            } else {
                                targetFilter = Filters.sameCardId(starship);
                                text = "starship (permanent pilot)";
                            }

                            action.setText("Target " + text);
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose " + text, targetFilter) {
                                        @Override
                                        protected void cardTargeted(int pilotTargetGroupId, PhysicalCard pilot) {
                                            action.addAnimationGroup(pilot);
                                            // Allow response(s)
                                            action.allowResponses("Substitute " + GameUtils.getCardLink(pilot) + "'s ability number of " + GuiUtils.formatAsString(highestAbilityPiloting) + " for battle destiny",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(starshipTargetGroupId);
                                                            final float finalAbility = game.getModifiersQuerying().getHighestAbilityPiloting(game.getGameState(), finalTarget, false, false);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new SubstituteDestinyEffect(action, finalAbility));
                                                        }
                                                    }
                                            );

                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
