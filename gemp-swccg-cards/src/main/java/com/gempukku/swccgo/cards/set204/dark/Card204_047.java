package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Set: Set 4
 * Type: Effect
 * Title: Bow To The First Order
 */
public class Card204_047 extends AbstractNormalEffect {
    public Card204_047() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Bow_To_The_First_Order, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setGameText("Deploy on table. Once per game, may [upload] Finalizer. Once per character, when you deploy Hux, Kylo, Phasma, or Snoke to an [Episode VII] battleground, may take any one card from Used Pile into hand, reshuffle. Immune to Alter.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BOW_TO_THE_FIRST_ORDER__UPLOAD_FINALIZER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Finalizer into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Finalizer, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BOW_TO_THE_FIRST_ORDER__UPLOAD_CARD_FROM_USED_PILE;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, playerId, Filters.and(Filters.character, Filters.or(Filters.Hux, Filters.Kylo, Filters.Phasma, Filters.Snoke)), Filters.and(Icon.EPISODE_VII, Filters.battleground))
                && GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {
            Set<String> characterNamesAlreadyUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getTextValues() : null;
            if (characterNamesAlreadyUsed == null) {
                characterNamesAlreadyUsed = new HashSet<String>();
            }
            PhysicalCard cardDeployed = ((PlayCardResult) effectResult).getPlayedCard();
            final List<String> characterNamesToUse = new ArrayList<String>();
            if (Filters.Hux.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Hux")) {
                characterNamesToUse.add("Hux");
            }
            if (Filters.Kylo.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Kylo")) {
                characterNamesToUse.add("Kylo");
            }
            if (Filters.Phasma.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Phasma")) {
                characterNamesToUse.add("Phasma");
            }
            if (Filters.Snoke.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Snoke")) {
                characterNamesToUse.add("Snoke");
            }
            if (!characterNamesToUse.isEmpty()) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card into hand from Used Pile");
                action.setActionMsg("Take any card from Used Pile into hand");
                // Update usage limit(s)
                action.appendUsage(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                Set<String> characterNamesAlreadyUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getTextValues() : null;
                                if (characterNamesAlreadyUsed == null) {
                                    self.setWhileInPlayData(new WhileInPlayData(new HashSet<String>()));
                                    characterNamesAlreadyUsed = self.getWhileInPlayData().getTextValues();
                                }
                                characterNamesAlreadyUsed.addAll(characterNamesToUse);
                            }
                        });
                // Perform result(s)
                action.appendEffect(
                        new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        Set<String> characterNamesAlreadyUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getTextValues() : null;
        if (characterNamesAlreadyUsed != null && !characterNamesAlreadyUsed.isEmpty()) {
            StringBuilder text = new StringBuilder("Characters used: ");
            for (String characterName : characterNamesAlreadyUsed) {
                text.append(characterName).append(", ");
            }
            text.setLength(text.length() - 2);
            return text.toString();
        }
        return null;
    }
}