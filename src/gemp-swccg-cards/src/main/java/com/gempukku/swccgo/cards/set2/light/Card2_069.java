package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gold 2
 */
public class Card2_069 extends AbstractStarfighter {
    public Card2_069() {
        super(Side.LIGHT, 2, 1, 2, null, 4, 4, 3, Title.Gold_2, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("Tiree's Y-wing during the Battle of Yavin. Custom high-power lateral thrusters provide enhanced maneuverability, allowing Gold 2 to better draw and evade enemy fire.");
        setGameText("May add 2 pilots or passengers. May forfeit in place of your other starfighter hit in Death Star: Trench, restoring that starfighter to normal.");
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.GOLD_SQUADRON);
        addModelType(ModelType.Y_WING);
        setPilotOrPassengerCapacity(2);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.your(self), Filters.other(self), Filters.starfighter, Filters.here(self));

        // Check condition(s)
        if (TriggerConditions.isAboutToBeHit(game, effectResult, targetFilter)
                && GameConditions.isAtLocation(game, self, Filters.Death_Star_Trench)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose to restore other 'hit' starfighter");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose 'hit' starfighter", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new LoseCardFromTableEffect(action, self, true));
                            // Allow response(s)
                            action.allowResponses("Restore " + GameUtils.getCardLink(cardTargeted) + " to normal",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, cardTargeted));
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
