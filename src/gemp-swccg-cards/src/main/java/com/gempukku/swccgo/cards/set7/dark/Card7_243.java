package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Tarkin's Bounty
 */
public class Card7_243 extends AbstractNormalEffect {
    public Card7_243() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Tarkin's Bounty", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("'You don't know how hard I found it signing the order to terminate your life.'");
        setGameText("Deploy on opponent's spy or Rebel leader. If subsequently captured by a bounty hunter or Imperial and then transferred to Detention Block Corridor, retrieve Force equal to character's forfeit (+4 if Leia) and lose Effect. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.BOUNTY, Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    public Map<InactiveReason, Boolean> getDeployTargetSpotOverride(PlayCardOptionId playCardOptionId) {
        return SpotOverride.INCLUDE_UNDERCOVER;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.or(Filters.spy, Filters.Rebel_leader));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.or(Filters.spy, Filters.Rebel);
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
        if (TriggerConditions.capturedBy(game, effectResult, Filters.hasAttached(self), Filters.or(Filters.bounty_hunter, Filters.Imperial))) {
            self.setWhileInPlayData(new WhileInPlayData());
            return null;
        }
        // Check condition(s)
        if (TriggerConditions.captiveDeliveredToPrison(game, effectResult, Filters.hasAttached(self), Filters.Detention_Block_Corridor)
                && GameConditions.cardHasWhileInPlayDataSet(self)) {
            DeliveredCaptiveToPrisonResult result = (DeliveredCaptiveToPrisonResult) effectResult;
            float amountToRetrieve = result.getForfeitValue();
            final PhysicalCard captive = result.getCaptive();
            if (Filters.Leia.accepts(game.getGameState(), game.getModifiersQuerying(), captive)) {
                amountToRetrieve += 4;
            }
            final PhysicalCard escort = result.getEscort();
            amountToRetrieve = game.getModifiersQuerying().getForceToRetrieveForBounty(game.getGameState(), playerId, escort, amountToRetrieve);

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
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}