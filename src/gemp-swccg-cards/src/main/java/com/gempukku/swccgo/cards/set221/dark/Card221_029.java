package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Send A Detachment Down (V)
 */
public class Card221_029 extends AbstractNormalEffect {
    public Card221_029() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Send_A_Detachment_Down, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Vader sent Imperial stormtroopers to the surface of Tatooine in search of the stolen Death Star plans. 'There'll be no one to stop us this time.'");
        setGameText("Deploy on table. While you occupy Tatooine system, your Force generation is +1 at Tatooine sites you occupy with a trooper and, during your deploy phase, may exchange a non-[Maintenance] Imperial trooper in hand with one in Reserve Deck; reshuffle. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.Tatooine_site, Filters.occupiesWith(playerId, self, Filters.trooper)), new OccupiesCondition(playerId, Filters.Tatooine_system), 1, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SEND_A_DETACHMENT_DOWN_V__EXCHANGE_TROOPER;

        Filter trooperFilter = Filters.and(Filters.not(Icon.MAINTENANCE), Filters.Imperial, Filters.trooper);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.occupies(game, playerId, Filters.Tatooine_system)
                && GameConditions.hasInHand(game, playerId, trooperFilter)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange for trooper in Reserve Deck");
            action.setActionMsg("Exchange a non-[Maintenance] Imperial trooper from hand with one from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithCardInReserveDeckEffect(action, playerId, trooperFilter, trooperFilter, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}