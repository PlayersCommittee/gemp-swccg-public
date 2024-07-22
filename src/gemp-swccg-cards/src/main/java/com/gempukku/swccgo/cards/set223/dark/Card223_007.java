package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.NumCopiesOfCardAtLocationEvaluator;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToPlayInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Defensive Shield
 * Title: A Useless Gesture & Death Star Sentry
 */
public class Card223_007 extends AbstractDefensiveShield {
    public Card223_007() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Useless Gesture & Death Star Sentry", ExpansionSet.SET_23, Rarity.V);
        addComboCardTitles(Title.A_Useless_Gesture, Title.Death_Star_Sentry);
        setGameText("Plays on table. In order to play an Interrupt from Lost Pile, opponent must first stack it here (if possible) and use +1 Force for each card here, even if Interrupt is normally free. For opponent to deploy a character, starship, or vehicle for free (except by that card's own game text), opponent must first use 2 Force. Opponent must first use X Force to deploy a non-unique card (except a Jawa) to a location, where X = the number of copies of that card at that location.");
        addIcons(Icon.GRABBER, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToPlayInterruptModifier(self, Filters.and(Filters.Interrupt, Filters.inLostPile(opponent),
                Filters.not(Filters.sameTitleAsStackedOn(self, Filters.and(Filters.grabber, Filters.not(self))))),
                new AddEvaluator(new StackedEvaluator(self), 1)));
        modifiers.add(new ExtraForceCostToDeployCardToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.non_unique, Filters.except(Filters.Jawa)),
                new NumCopiesOfCardAtLocationEvaluator(self)));
        modifiers.add(new ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle)), 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isPlayingCardFromLostPile(game, effect, opponent, Filters.and(Filters.Interrupt,
                Filters.not(Filters.sameTitleAsStackedOn(self, Filters.and(Filters.grabber, Filters.not(self))))))) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.canBeGrabbed(game, self, cardBeingPlayed)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("'Grab' " + GameUtils.getFullName(cardBeingPlayed));
                action.setActionMsg("'Grab' " + GameUtils.getCardLink(cardBeingPlayed));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromVoidEffect(action, cardBeingPlayed, self));
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}
