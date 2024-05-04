package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractStartingEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.ThreeTimesPerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Subtype: Starting
 * Title: An Unusual Amount Of Fear
 */
public class Card13_005 extends AbstractStartingEffect {
    public Card13_005() {
        super(Side.LIGHT, 0, "An Unusual Amount Of Fear", ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("The peacekeepers of the galaxy are not to be taken lightly.");
        setGameText("Before any starting cards are revealed, deploy on table with up to 10 cards from outside your deck face-down under here. Cards under here do not count toward your deck limit. Three times per game, may play a Defensive Shield from here, as if from hand.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            // Perform result(s)
            action.appendEffect(
                    new StackCardsFromOutsideDeckEffect(action, playerId, 1, 10, self, Filters.Defensive_Shield));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.AN_UNUSUAL_AMOUNT_OF_FEAR__PLAY_DEFENSIVE_SHIELD;
        Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));

        // Check condition(s)
        if (GameConditions.isThreeTimesPerGame(game, self, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Play a Defensive Shield");
            // Update usage limit(s)
            action.appendUsage(
                    new ThreeTimesPerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, self, filter) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}