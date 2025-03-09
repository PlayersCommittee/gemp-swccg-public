package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Bounty
 */
public class Card5_113 extends AbstractNormalEffect {
    public Card5_113() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Bounty, Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("One of the most profitable occupations in the galaxy is hunting down and capturing wanted beings. The more notable the quarry, the more profitable the venture.");
        setGameText("Deploy on an opponent's non-droid character. If subsequently captured, seized by a bounty hunter, and then transferred to a prison, retrieve Force equal to character's forfeit -2 and place Effect in Used Pile. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.BOUNTY, Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.non_droid_character);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.non_droid_character;
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
        if (TriggerConditions.captiveDeliveredToPrison(game, effectResult, Filters.hasAttached(self), Filters.any)
                && GameConditions.cardHasWhileInPlayDataSet(self)) {
            DeliveredCaptiveToPrisonResult result = (DeliveredCaptiveToPrisonResult) effectResult;
            float forfeitValue = result.getForfeitValue();
            final PhysicalCard escort = result.getEscort();
            final PhysicalCard captive = result.getCaptive();
            float amountToRetrieve = Math.max(0, game.getModifiersQuerying().getForceToRetrieveForBounty(game.getGameState(), playerId, escort, forfeitValue - 2));

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve " + GuiUtils.formatAsString(amountToRetrieve) + " Force");
            action.setActionMsg("Have " + playerId + " retrieve " + GuiUtils.formatAsString(amountToRetrieve) + " Force");
            // Perform result(s)
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
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

}