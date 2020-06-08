package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
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
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Short Range Fighters & Watch Your Back!
 */
public class Card12_158 extends AbstractUsedOrLostInterrupt {
    public Card12_158() {
        super(Side.DARK, 5, "Short Range Fighters & Watch Your Back!", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Short_Range_Fighters, Title.Watch_Your_Back);
        setGameText("USED: Take one unique (•) unpiloted starfighter into hand from Reserve Deck; reshuffle. LOST: During a battle at a system or sector, if you are about to draw a card for battle destiny, you may instead use the maneuver number of your unique (•) starfighter in that battle.");
        addIcons(Icon.CORUSCANT);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.SHORT_RANGE_FIGHTERS__UPLOAD_UNIQUE_STARFIGHTER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a unique unpiloted starfighter into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.unique, Filters.unpiloted, Filters.starfighter), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final Filter yourStarfighterInBattle = Filters.and(Filters.your(self), Filters.unique, Filters.starfighter, Filters.participatingInBattle, Filters.hasManeuverDefined);

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.isDuringBattleAt(game, Filters.system_or_sector)
                && GameConditions.canTarget(game, self, yourStarfighterInBattle)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Substitute destiny");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", yourStarfighterInBattle) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard starfighter) {
                            action.addAnimationGroup(starfighter);
                            float maneuver = game.getModifiersQuerying().getManeuver(game.getGameState(), starfighter);
                            // Allow response(s)
                            action.allowResponses("Substitute " + GameUtils.getCardLink(starfighter) + "'s maneuver value of " + GuiUtils.formatAsString(maneuver) + " for battle destiny",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            float finalManeuver = game.getModifiersQuerying().getManeuver(game.getGameState(), finalTarget);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new SubstituteDestinyEffect(action, finalManeuver));
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