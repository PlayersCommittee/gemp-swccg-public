package com.gempukku.swccgo.cards.set206.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 6
 * Type: Character
 * Subtype: Rebel
 * Title: Bodhi Rook
 */
public class Card206_001 extends AbstractRebel {
    public Card206_001() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Bodhi, Uniqueness.UNIQUE, ExpansionSet.SET_6, Rarity.V);
        setLore("Gambler and spy.");
        setGameText("[Pilot] 2, 3: Rogue One or a stolen starship. Once per game, during battle, may peek at opponent's hand. If about to be lost, your Rebel or Rebel starship at a related location may make a regular move.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.GAMBLER, Keyword.SPY, Keyword.ROGUE_SQUADRON);
        setMatchingStarshipFilter(Filters.Rogue_One);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.or(Filters.Rogue_One, Filters.and(Filters.stolen, Filters.starship)))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, self)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, self)) {
            Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.Rebel_starship), Filters.at(Filters.relatedLocation(self)), Filters.movableAsRegularMove(playerId, false, 0, false, Filters.any));
            if (GameConditions.canSpot(game, self, filter)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Move a Rebel or Rebel starship");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose Rebel or Rebel starship to move", filter) {
                            @Override
                            protected void cardSelected(PhysicalCard cardToMove) {
                                action.addAnimationGroup(cardToMove);
                                action.setActionMsg("Have " + GameUtils.getCardLink(cardToMove) + " make a regular move");
                                // Perform result(s)
                                action.appendEffect(
                                        new MoveCardAsRegularMoveEffect(action, playerId, cardToMove, false, false, Filters.any));
                            }
                        });

                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BODHI__PEAK_AT_HAND;
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.hasHand(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at opponent's hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtOpponentsHandEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
