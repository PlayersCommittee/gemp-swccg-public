package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Subtype: Immediate
 * Title: Enter The Bureaucrat
 */
public class Card12_133 extends AbstractImmediateEffect {
    public Card12_133() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Enter The Bureaucrat", Uniqueness.RESTRICTED_3, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("Even the most effective of operations can be easily mired under the Republic's plethora of procedural red tape.");
        setGameText("If you just lost more than 2 Force to a Force drain, deploy on opponent's location. You lose no more than 1 Force from Force drains here. At any time, you may place Immediate Effect in Used Pile to draw two cards from top of Reserve Deck.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLostMoreThanXForceFromForceDrain(game, effectResult, playerId, 2)) {

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.and(Filters.opponents(self), Filters.location), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.here(self), 1, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
        action.setText("Place in Used Pile");
        action.setActionMsg("Draw two cards from Reserve Deck");
        // Pay cost(s)
        action.appendCost(
                new PlaceCardInUsedPileFromTableEffect(action, self));
        // Perform result(s)
        action.appendEffect(
                new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 2));
        return Collections.singletonList(action);
    }
}