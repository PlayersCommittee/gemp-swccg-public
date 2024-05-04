package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableUsingForfeitValueEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 7
 * Type: Character
 * Subtype: Jedi Master
 * Title: Depa Billaba (V)
 */
public class Card207_003 extends AbstractJediMaster {
    public Card207_003() {
        super(Side.LIGHT, 2, 4, 4, 7, 6, "Depa Billaba", Uniqueness.UNIQUE, ExpansionSet.SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("Chalactan Jedi Master who was rescued by Mace Windu from space pirates when she was six months old. Jedi Council member who is renowned for her insights.");
        setGameText("If at least two [LS] icons here, once during your control phase may draw top card of Reserve Deck. If at least two [DS] icons here, may forfeit in place of your hit non-Jedi character present with Depa (forfeit = 0), restoring that character to normal.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_7);
        addKeywords(Keyword.FEMALE, Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.getNumForceIconsHere(game, self, false, true) >= 2
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        Filter targetFilter = Filters.and(Filters.your(playerId), Filters.hit, Filters.non_Jedi_character, Filters.presentWith(self));

        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.getNumForceIconsHere(game, self, true, false) >= 2
                && GameConditions.canTarget(game, self, targetFilter)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Forfeit to restore 'hit' character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose 'hit' character", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new ForfeitCardFromTableUsingForfeitValueEffect(action, self, 0));
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
