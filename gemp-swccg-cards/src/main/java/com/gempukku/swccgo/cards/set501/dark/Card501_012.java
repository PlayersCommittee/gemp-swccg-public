package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Epic Event
 * Title: Epic Duel (v)
 */
public class Card501_012 extends AbstractEpicEventDeployable {
    public Card501_012() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Epic_Duel);
        setGameText("Deploy on table. Players only lose Force to Visage Of The Emperor at the end of their own turn. You may not deploy characters except Imperials and Bounty Hunters. Choose one:\n" +
                "Master: Once per turn may deploy Carbonite Chamber or Chasm Walkway. While Their Fire Has Gone Out Of The Universe on table, opponent's Force drain bonuses are canceled.\n" +
                "Apprentice: Once per turn you may deploy a Malachor location. Inquisitors are destiny +2. Your total battle destiny is +1 for each Inquisitor or Hatred card in battle.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
        setTestingText("Epic Duel (v)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();


        modifiers.add(new ModifyGameTextModifier(self, Filters.Visage_Of_The_Emperor, ModifyGameTextType.VIAGE_OF_THE_EMPEROR__TRIGGERS_ONLY_AT_END_PLAYERS_TURN));
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.not(Filters.Imperial), Filters.not(Filters.bounty_hunter)), self.getOwner()));

        //Master
        Condition playCardOptionId1 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1);
        Condition theirFireHasGoneOutOfTheUniverseOnTableCondition = new OnTableCondition(self, Filters.Their_Fire_Has_Gone_Out_Of_The_Universe);
        modifiers.add(new CancelOpponentsForceDrainBonusesModifier(self, new AndCondition(playCardOptionId1, theirFireHasGoneOutOfTheUniverseOnTableCondition)));

        //Apprentice
        Condition playCardOptionId2 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_2);
        Evaluator inquisitorsAndHatredCardsInBattleEvaluator = new InBattleEvaluator(self, Filters.or(Filters.inquisitor, Filters.hatredCard));
        modifiers.add(new DestinyModifier(self, Filters.inquisitor, playCardOptionId2,2));
        modifiers.add(new TotalBattleDestinyModifier(self, playCardOptionId2, inquisitorsAndHatredCardsInBattleEvaluator, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Master"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Apprentice"));
        return playCardOptions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        //Master
        if(GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
            && GameConditions.isPlayCardOption(game, self, PlayCardOptionId.PLAY_CARD_OPTION_1)){
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a site from Reserve Deck");
            action.setActionMsg("Deploy a site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Carbonite_Chamber, Filters.Chasm_Walkway), true));
            return Collections.singletonList(action);
        }

        //Apprentice
        if(GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isPlayCardOption(game, self, PlayCardOptionId.PLAY_CARD_OPTION_2)){
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Malachor site from Reserve Deck");
            action.setActionMsg("Deploy a Malachor site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Malachor_location, true));
            return Collections.singletonList(action);
        }

        return actions;
    }
}