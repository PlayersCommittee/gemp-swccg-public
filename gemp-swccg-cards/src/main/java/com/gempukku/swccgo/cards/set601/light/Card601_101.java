package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 1
 * Type: Effect
 * Title: Rycar Ryjerd (V)
 */
public class Card601_101 extends AbstractNormalEffect {
    public Card601_101() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Rycar_Ryjerd, Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("A Bimm trader and smuggler of starship weapons. Trusts no one. Does business with anyone. Teaches smuggler apprentices. Has mastered Jawa language.");
        setGameText("Deploy on table. If your character is about to be lost from table, place all devices and Effects on that character in owner's Used Pile. Twice per game, may take a device (or an Effect) that deploys on a character into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.LEGACY_BLOCK_1);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId  = GameTextActionId.LEGACY__RYCAR_RYJERD_V__UPLOAD_CARD;

        final Filter filter = Filters.and(Filters.or(Filters.device, Filters.Effect), Filters.deploys_on_characters);

        // Check condition(s)
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a device (or an Effect) that deploys on a character into hand from Reserve Deck");

            action.appendUsage(
                    new TwicePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, filter, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter toSave = Filters.and(Filters.canBeTargetedBy(self), Filters.or(Filters.Effect, Filters.device));
        Filter aboutToBeLost = Filters.and(Filters.your(self), Filters.character, Filters.hasAttached(toSave));

        if (TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, aboutToBeLost)
            || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, aboutToBeLost)) {

            PhysicalCard card = ((AboutToLeaveTableResult)effectResult).getCardAboutToLeaveTable();
            if (card != null) {

                Collection<PhysicalCard> toPlaceInUsedPile = Filters.filterActive(game, self, Filters.and(Filters.attachedTo(card), toSave));
                if (!toPlaceInUsedPile.isEmpty()) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Place devices and effects in Used Pile");
                    action.appendEffect(
                            new PlaceCardsInUsedPileFromTableEffect(action, self.getOwner(), toPlaceInUsedPile));

                    return Collections.singletonList(action);

                }
            }
        }

        return null;
    }
}