package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Resistance
 * Title: Lor San Tekka
 */
public class Card204_007 extends AbstractResistance {
    public Card204_007() {
        super(Side.LIGHT, 3, 3, 1, 4, 5, Title.Lor_San_Tekka, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLore("Information broker. Leader.");
        setGameText("If you just deployed BB-8 or a Resistance leader to same or related location, may draw top card of Reserve Deck. During your control phase, one of your other Resistance characters present may make a regular move.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.INFORMATION_BROKER, Keyword.LEADER);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, playerId, Filters.or(Filters.BB8, Filters.Resistance_leader), Filters.sameOrRelatedLocation(self))
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            Collection<PhysicalCard> otherResistanceCharacters = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.other(self), Filters.Resistance_character, Filters.present(self)));
            if (!otherResistanceCharacters.isEmpty()) {
                List<PhysicalCard> validCharacters = new ArrayList<PhysicalCard>();
                for (PhysicalCard otherResistanceCharacter : otherResistanceCharacters) {
                    if (Filters.movableAsRegularMove(playerId, false, 0, false, Filters.any).accepts(game, otherResistanceCharacter)) {
                        validCharacters.add(otherResistanceCharacter);
                    }
                }
                if (!validCharacters.isEmpty()) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Have a character make a regular move");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose Resistance character to move", Filters.in(validCharacters)) {
                                @Override
                                protected void cardSelected(PhysicalCard character) {
                                    action.addAnimationGroup(character);
                                    action.setActionMsg("Have " + GameUtils.getCardLink(character) + " make a regular move");
                                    // Perform result(s)
                                    action.appendEffect(
                                            new MoveCardAsRegularMoveEffect(action, playerId, character, false, false, Filters.any));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
