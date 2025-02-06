package com.gempukku.swccgo.cards.set303.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Shadow Academy
 * Type: Character
 * Subtype: Alien
 * Title: Malfearak Asvraal, Herald
 */
public class Card303_019 extends AbstractAlien {
    public Card303_019() {
        super(Side.LIGHT, 1, 4, 4, 5, 5, "Malfearak Asvraal, Herald", Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.R);
        setLore("Experienced Jawa thief. Pilfers equipment and hijacks vehicles from unwary bystanders in Mos Eisley. Het Nkik's ugliest cousin.");
        setGameText("Adds 1 to power of any ship he pilots. During your control phase, may exchange one card in hand for one weapon or device in your Lost Pile OR may lose 1 Force to steal into hand one character weapon or device from opponent's Lost Pile.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
		addKeywords(Keyword.COUNCILOR);
    }

	@Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        return modifiers;
    }

	@Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.MALFEARAK__EXCHANGE_CARD_IN_HAND_WITH_CARD_IN_LOST_PILE_OR_STEAL_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canStealCardsFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Steal from opponent's Lost Pile");
            action.setActionMsg("Steal a character weapon or device into hand from opponent's Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new StealCardIntoHandFromLostPileEffect(action, playerId, Filters.or(Filters.character_weapon, Filters.device)));
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange card in hand for card in Lost Pile");
            action.setActionMsg("Exchange a card in hand for a weapon or device in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.any, Filters.or(Filters.weapon, Filters.device)));
            actions.add(action);
        }

        return actions;
    }
}
