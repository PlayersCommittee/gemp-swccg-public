package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractStartingEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AfterPlayersTurnNumberCondition;
import com.gempukku.swccgo.cards.effects.usage.FourTimesPerGameEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Effect
 * Subtype: Starting
 * Title: Knowledge And Defense (V)
 */
public class Card601_001 extends AbstractStartingEffect {
    public Card601_001() {
        super(Side.DARK, 4, "Knowledge And Defense");
        setVirtualSuffix(true);
        setLore("'A Jedi uses the Force for knowledge and defense, never for attack.'");
        setGameText("Deploy on table with up to 15 cards from outside your deck face-down under here. Four times per game, may play a card from here (as if from hand). Unless canceling your Interrupt, opponent may not play Recoil In Fear until the end of your first turn.");
        addIcons(Icon.DAGOBAH, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.opponents(self), Filters.Recoil_In_Fear),
                new NotCondition(new AfterPlayersTurnNumberCondition(playerId, 1)), ModifyGameTextType.RECOIL_IN_FEAR__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT));
        return modifiers;
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
                    new StackCardsFromOutsideDeckEffect(action, playerId, 1, 15, self, Filters.Defensive_Shield));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KNOWLEDGE_AND_DEFENSE__PLAY_CARD;
        Filter filter = Filters.playable(self);

        // Check condition(s)
        if (GameConditions.isFourTimesPerGame(game, self, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Play a card");
            // Update usage limit(s)
            action.appendUsage(
                    new FourTimesPerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, self, filter) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlayStackedCardEffect(action, self, selectedCard));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}