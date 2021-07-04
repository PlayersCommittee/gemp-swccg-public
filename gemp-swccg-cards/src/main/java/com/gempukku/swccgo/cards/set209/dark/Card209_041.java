package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Effect
 * Title: An Inkling Of Its Destructive Potential
 */
public class Card209_041 extends AbstractNormalEffect {
    public Card209_041() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.An_Inkling_Of_Its_Destructive_Potential, Uniqueness.UNIQUE);
        setGameText("Deploy on table. Once per game, may [upload] a [Set 9] Epic Event. During your deploy phase, if Krennic on Death Star, may flip Set Your Course For Alderaan (flip back at end of turn). (Immune to Alter.)");
        addIcons(Icon.VIRTUAL_SET_9);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Card action 1
        GameTextActionId gameTextActionId1 = GameTextActionId.AN_INKLING_OF_ITS_DESTRUCTIVE_POWER__UPLOAD_SET_NINE_EPIC_EVENT;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);
            action.setText("Take A [Set 9] Epic Event Into Hand");
            action.setActionMsg("Take A [Set 9] Epic Event Into Hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Epic_Event, Icon.VIRTUAL_SET_9), true));
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.AN_INKLING_OF_ITS_DESTRUCTIVE_POWER__FLIP_SYCFA;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, Filters.Set_Your_Course_For_Alderaan)) {
            final Filter krennicAtDeathStarLocation = Filters.and(Filters.Krennic, Filters.at(Filters.Death_Star_location));
            if (GameConditions.canSpot(game, self, krennicAtDeathStarLocation)) {
                PhysicalCard sycfa = Filters.findFirstActive(game, self, Filters.Set_Your_Course_For_Alderaan);

                if (sycfa != null && GameConditions.canBeFlipped(game, sycfa)) {
                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
                    action.setText("Flip " + GameUtils.getCardLink(sycfa));
                    action.setActionMsg("Flip " + GameUtils.getCardLink(sycfa));
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    action.appendEffect(
                            new FlipCardEffect(action, sycfa));
                    action.appendEffect(
                            new SetWhileInPlayDataEffect(action, self, new WhileInPlayData())
                    );
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String selfOwner = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);

        if (TriggerConditions.isStartOfYourTurn(game, effectResult, selfOwner)) {
            self.setWhileInPlayData(null);
        }

        if (TriggerConditions.isEndOfYourTurn(game, effectResult, selfOwner)
                && GameConditions.cardHasWhileInPlayDataSet(self)
                && GameConditions.canSpot(game, self, Filters.The_Ultimate_Power_In_The_Universe)) {
            PhysicalCard ultimatePower = Filters.findFirstActive(game, self, Filters.The_Ultimate_Power_In_The_Universe);
            if (ultimatePower != null && GameConditions.canBeFlipped(game, ultimatePower)) {
                action.setText("Flip " + GameUtils.getCardLink(ultimatePower));
                action.setActionMsg("Flip " + GameUtils.getCardLink(ultimatePower));
                action.appendEffect(
                        new FlipCardEffect(action, ultimatePower));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

}