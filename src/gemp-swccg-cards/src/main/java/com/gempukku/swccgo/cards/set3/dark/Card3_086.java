package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Droid
 * Title: FX-10 (Effex-ten)
 */
public class Card3_086 extends AbstractDroid {
    public Card3_086() {
        super(Side.DARK, 2, 2, 1, 3, "FX-10 (Effex-ten)", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C2);
        setLore("Standard medical droid used by Imperial field units. Enhanced programming allows FX-10 to treat a wide variety of battle wounds.");
        setGameText("Once per turn, one of your 'hit' non-droid characters at same or adjacent site may go to your Used Pile rather than your Lost Pile.");
        addIcons(Icon.HOTH);
        addModelType(ModelType.MEDICAL);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.your(self), Filters.hit, Filters.non_droid_character, Filters.atSameOrAdjacentSite(self));

        // Check condition(s)
        if (TriggerConditions.isAboutToBeLost(game, effectResult, filter)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardToBeLost();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(cardToBeLost) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(cardToBeLost) + " in Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, result.getCardToBeLost()));
                        }
                    });
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, filter)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;
            final PhysicalCard cardToBeForfeited = result.getCardToBeForfeited();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(cardToBeForfeited) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(cardToBeForfeited) + " in Used Pile when forfeited");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getForfeitCardEffect().setForfeitToUsedPile();
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
