package com.gempukku.swccgo.cards.set6.dark;

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
 * Set: Jabba's Palace
 * Type: Effect
 * Title: Hutt Bounty
 */
public class Card6_144 extends AbstractNormalEffect {
    public Card6_144() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Hutt_Bounty, Uniqueness.UNIQUE);
        setLore("'Chissaa, picha gawanki Chewbacca. Yupon cogorato kama walpa kyess kashung kawa Wookiee.'");
        setGameText("Deploy on an opponent's smuggler, gambler, or thief. If subsequently captured by a bounty hunter and then transferred to Jabba's Palace Dungeon, retrieve Force equal to character's forfeit (+6 if Han) and lose effect. (Immune to Alter.)");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.BOUNTY, Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.or(Filters.smuggler, Filters.gambler, Filters.thief));
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
        if (TriggerConditions.captiveDeliveredToPrison(game, effectResult, Filters.hasAttached(self), Filters.Dungeon)
                && GameConditions.cardHasWhileInPlayDataSet(self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve Force");
            // Perform result(s)
            DeliveredCaptiveToPrisonResult result = (DeliveredCaptiveToPrisonResult) effectResult;
            float amountToRetrieve = result.getForfeitValue();
            final PhysicalCard captive = result.getCaptive();
            if (Filters.Han.accepts(game.getGameState(), game.getModifiersQuerying(), captive)) {
                amountToRetrieve += 6;
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