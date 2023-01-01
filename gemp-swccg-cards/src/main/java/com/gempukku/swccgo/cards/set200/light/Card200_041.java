package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Set: Set 0
 * Type: Effect
 * Title: I Must Be Allowed To Speak (V)
 */
public class Card200_041 extends AbstractNormalEffect {
    public Card200_041() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "I Must Be Allowed To Speak", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Jedi mod spienko eek.'");
        setGameText("Deploy on table. Once per character, when you deploy Chewie, Lando, Leia, or Luke to a Tatooine site, may take any one card from Used Pile into hand; reshuffle. While Han is frozen, Rebels are immune to None Shall Pass. Once per game, may [download] a farm. [Immune to Alter]");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.I_MUST_BE_ALLOWED_TO_SPEAK__UPLOAD_CARD_FROM_USED_PILE;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, playerId, Filters.and(Filters.character, Filters.or(Filters.Chewie, Filters.Lando, Filters.Leia, Filters.Luke)), Filters.Tatooine_site)
                && GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {
            Set<String> characterNamesAlreadyUsed = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getTextValues() : null;
            if (characterNamesAlreadyUsed == null) {
                characterNamesAlreadyUsed = new HashSet<String>();
            }
            PhysicalCard cardDeployed = ((PlayCardResult) effectResult).getPlayedCard();
            boolean isActive = Filters.canSpot(game, self, cardDeployed);
            final Set<String> characterNamesToUse = new HashSet<String>();
            if (isActive && Filters.Chewie.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Chewie")) {
                characterNamesToUse.add("Chewie");
            }
            if (isActive && Filters.Lando.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Lando")) {
                characterNamesToUse.add("Lando");
            }
            if (isActive && Filters.Leia.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Leia")) {
                characterNamesToUse.add("Leia");
            }
            if (isActive && Filters.Luke.accepts(game, cardDeployed) && !characterNamesAlreadyUsed.contains("Luke")) {
                characterNamesToUse.add("Luke");
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
        GameTextActionId gameTextActionId = GameTextActionId.I_MUST_BE_ALLOWED_TO_SPEAK__DOWNLOAD_FARM;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a farm from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.farm, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition hanIsFrozen = new OnTableCondition(self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Han, Filters.frozenCaptive));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Rebel, hanIsFrozen, Title.None_Shall_Pass));
        return modifiers;
    }
}