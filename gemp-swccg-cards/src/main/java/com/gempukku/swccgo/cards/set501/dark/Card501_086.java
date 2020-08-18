package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title:A Lawless Time
 */
public class Card501_086 extends AbstractUsedInterrupt {
    public Card501_086() {
        super(Side.DARK, 4, "A Lawless Time", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Take a blaster into hand from Reserve Deck; reshuffle. OR If you just fired Black Sun Blaster or" +
                "Crimson Dawn Blaster, add one battle destiny. OR During any control phase, if Maul or Vos at a battleground site, flip Shadow Collective (or You Know Who I Answer To).");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("A Lawless Time");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.A_LAWLESS_TIME__UPLOAD_BLASTER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take a blaster into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a blaster into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.blaster, true));
                        }
                    }
            );
            actions.add(action);
        }


        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.CONTROL)
                && GameConditions.canSpot(game, self, Filters.or(Filters.title(Title.Shadow_Collective), Filters.title(Title.You_Know_Who_I_Answer_To)))
                && GameConditions.canSpot(game, self, Filters.and(Filters.or(Filters.Maul, Filters.Vos), Filters.at(Filters.battleground_site)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Flip Objective");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Objective", Filters.or(Filters.title(Title.Shadow_Collective), Filters.title(Title.You_Know_Who_I_Answer_To))) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            // Allow response(s)
                            action.allowResponses("Flip " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new FlipCardEffect(action, targetedCard));
                                        }
                                    }
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
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.weaponJustFired(game, effectResult, Filters.or(Filters.title(Title.Black_Sun_Blaster), Filters.title(Title.Crimson_Dawn_Blaster)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}