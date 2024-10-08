package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealCardFromOwnHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Lost
 * Title: Shut up, Pogo!
 */
public class Card304_092 extends AbstractLostInterrupt {
    public Card304_092() {
        super(Side.DARK, 4, "Shut Up, Pogo!", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Shut up, Pogo!");
        setGameText("Once per game, cancel the game text of an opponent's non-Jedi character present with your Thran Personal Guard or Thran for remainder of turn.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.SHUT_UP_POGO__CANCEL_GAME_TEXT;

        // Check condition(s)
        Filter darkJediOrInquisitor = Filters.and(Filters.your(self), Filters.or(Filters.THRAN_GUARD, Filters.Thran));
        Filter character = Filters.and(Filters.opponents(self), Filters.non_Jedi_character, Filters.presentWith(self, darkJediOrInquisitor));

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, character)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Cancel game text of a character");
            action.setActionMsg("Cancel the game text of a non-Jedi character present with your Thran Personal Guard or Thran for remainder of turn");

            action.appendUsage(new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character to cancel game text", character) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard characterTargeted) {
                            action.addAnimationGroup(characterTargeted);

                            // Allow response(s)
                            action.allowResponses("Cancel game text of " + GameUtils.getCardLink(characterTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalCharacter));
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