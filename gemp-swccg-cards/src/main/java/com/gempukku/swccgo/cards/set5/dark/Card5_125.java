package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Vader's Bounty
 */
public class Card5_125 extends AbstractNormalEffect {
    public Card5_125() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Vader's Bounty", Uniqueness.UNIQUE);
        setLore("'We would be honored if you would join us.'");
        setGameText("Deploy on a Rebel of ability > 2. If subsequently captured by a bounty hunter and then transferred to a prison where Vader is present, retrieve Force equal to character's forfeit (+4 if Luke) and lose Effect. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.BOUNTY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Rebel, Filters.abilityMoreThan(2));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Rebel;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    private List<RequiredGameTextTriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.capturedBy(game, effectResult, Filters.hasAttached(self), Filters.bounty_hunter)) {
            self.setWhileInPlayData(new WhileInPlayData());
            return null;
        }

        // Check condition(s)
        if (TriggerConditions.captiveDeliveredToPrison(game, effectResult, Filters.hasAttached(self), Filters.wherePresent(self, Filters.Vader))
                && GameConditions.cardHasWhileInPlayDataSet(self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve Force");
            // Perform result(s)
            DeliveredCaptiveToPrisonResult result = (DeliveredCaptiveToPrisonResult) effectResult;
            float amountToRetrieve = result.getForfeitValue();
            final PhysicalCard captive = result.getCaptive();
            if (Filters.Luke.accepts(game.getGameState(), game.getModifiersQuerying(), captive)) {
                amountToRetrieve += 4;
            }
            final PhysicalCard escort = result.getEscort();
            amountToRetrieve = game.getModifiersQuerying().getForceToRetrieveForBounty(game.getGameState(), playerId, escort, amountToRetrieve);
            if (amountToRetrieve > 0) {
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, amountToRetrieve) {
                            @Override
                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                return Arrays.asList(escort, captive);
                            }
                        });
            }
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        return null;
    }
}