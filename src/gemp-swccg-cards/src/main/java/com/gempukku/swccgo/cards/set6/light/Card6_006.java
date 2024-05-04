package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Baragwin
 */
public class Card6_006 extends AbstractAlien {
    public Card6_006() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Baragwin", Uniqueness.RESTRICTED_3, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Hunchbacked scavengers. Found in separate groups throughout the galaxy. Good at finding missing items. Make excellent gun runners. Some work with the rebellion.");
        setGameText("Once per turn, if the top card of your Lost Pile is a weapon, device or transport vehicle, may use 1 Force to retrieve it. During your control phase, may exchange one card in hand for one weapon or device in your Lost Pile.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeywords(Keyword.SCAVENGER);
        setSpecies(Species.BARAGWIN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isTopCardOfLostPileMatchTo(game, playerId, Filters.or(Filters.weapon, Filters.device, Filters.transport_vehicle))
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve top card of Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.BARAGWIN__EXCHANGE_CARD_IN_HAND_WITH_CARD_IN_LOST_PILE;

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
