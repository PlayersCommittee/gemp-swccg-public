package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.*;

/**
 * Set: Set 18
 * Type: Effect
 * Title: I Must Be Allowed To Speak & Smuggler's Blues
 */
public class Card218_021 extends AbstractNormalEffect {
    public Card218_021() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "I Must Be Allowed To Speak & Smuggler's Blues", Uniqueness.UNIQUE);
        addComboCardTitles("I Must Be Allowed To Speak", "Smuggler's Blues");
        setGameText("If Watch Your Step on table, deploy on table. Corran is a smuggler. Once per character, when you deploy Corran, Mara, Mirax, or Talon Karrde to a Tatooine location, may take any one card into hand from Used Pile; reshuffle. Once per game, may [download] a [Reflections II] location. Opponent may not cancel or modify Force drains at battlegrounds where you have two smugglers. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_18);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.Watch_Your_Step);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.I_MUST_BE_ALLOWED_TO_SPEAK_SMUGGLERS_BLUES__UPLOAD_CARD_FROM_USED_PILE;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, playerId, Filters.and(Filters.character, Filters.or(Filters.Corran_Horn, Filters.Mara_Jade, Filters.Mirax, Filters.title("Talon Karrde"))), Filters.Tatooine_location)
                && GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {
            Set<String> characterNamesAlreadyUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getTextValues() : null;
            if (characterNamesAlreadyUsed == null) {
                characterNamesAlreadyUsed = new HashSet<>();
            }
            PhysicalCard cardDeployed = ((PlayCardResult) effectResult).getPlayedCard();
            boolean isActive = Filters.canSpot(game, self, cardDeployed);
            final Set<String> characterNamesToUse = new HashSet<String>();
            if (isActive && Filters.Corran_Horn.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Corran")) {
                characterNamesToUse.add("Corran");
            }
            if (isActive && Filters.Mara_Jade.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Mara")) {
                characterNamesToUse.add("Mara");
            }
            if (isActive && Filters.Mirax.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Mirax")) {
                characterNamesToUse.add("Mirax");
            }
            if (isActive && Filters.title("Talon Karrde").accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Talon Karrde")) {
                characterNamesToUse.add("Talon Karrde");
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

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.I_MUST_BE_ALLOWED_TO_SPEAK_SMUGGLERS_BLUES__DOWNLOAD_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy location from Reserve Deck");
            action.setActionMsg("Deploy a [Reflections II] location from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.REFLECTIONS_II, Filters.location), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.Corran_Horn, Filters.character), Keyword.SMUGGLER));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.and(Filters.smuggler, Filters.with(self, Filters.smuggler)))), opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.and(Filters.smuggler, Filters.with(self, Filters.smuggler)))), opponent, playerId));
        return modifiers;
    }
}