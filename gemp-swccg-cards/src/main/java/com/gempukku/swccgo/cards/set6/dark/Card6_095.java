package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractCharacter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Bane Malar
 */
public class Card6_095 extends AbstractAlien {
    public Card6_095() {
        super(Side.DARK, 1, 4, 1, 4, 3, "Bane Malar", Uniqueness.UNIQUE);
        setLore("Mysterious bounty hunter of an unknown species. Rumored to be somewhat telepathic. Infrequent member of Jabba's court. Plotting to kill Jabba.");
        setGameText("At the start of a battle, may use 1 Force to 'mindscan' one opponent's non-droid character of lesser ability present. Adds that character's power and game text to his own for remainder of battle. Immune to attrition < 3.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeyword(Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.not(Filters.droid), Filters.abilityLessThan(game.getModifiersQuerying().getAbility(game.getGameState(), self)), Filters.present(self));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.wherePresent(self))
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, filter)) {

            GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_7;

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Mindscan a character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Mindscan " + GameUtils.getCardLink(cardTargeted),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard mindscannedCharacter = targetingAction.getPrimaryTargetCard(targetGroupId);


                                            // Perform result(s)

                                            // add the mindscanned character's power to Bane Malar
                                            action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                                    new PowerModifier(self, self, game.getModifiersQuerying().getPower(game.getGameState(), mindscannedCharacter)),
                                                    null)
                                            );


                                            action.appendEffect(new MindscanCharacterUntilEndOfBattleEffect(action,
                                                    new MindscannedCharacterModifier(self, mindscannedCharacter, mindscannedCharacter.isGameTextCanceled()),
                                                    "Mindscanned "+GameUtils.getCardLink(mindscannedCharacter),
                                                    self,
                                                    mindscannedCharacter)
                                            );

                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }

        for(OptionalGameTextTriggerAction a:(List<OptionalGameTextTriggerAction>) getMindScannedActions(playerId, game, self, effectResult, null, MINDSCAN_ACTION_TYPE.GAME_TEXT_OPTIONAL_AFTER_TRIGGERS)) {
            actions.add(a);
        }

        return actions;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return (List<TopLevelGameTextAction>) getMindScannedActions(playerId, game, self, null, null, MINDSCAN_ACTION_TYPE.GAME_TEXT_TOP_LEVEL_ACTIONS);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return (List<OptionalGameTextTriggerAction>) getMindScannedActions(playerId, game, self, effectResult, null, MINDSCAN_ACTION_TYPE.GAME_TEXT_LEAVES_TABLE_OPTIONAL_TRIGGERS);
    }

    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return (List<RequiredGameTextTriggerAction>) getMindScannedActions(null, game, self, null, effect, MINDSCAN_ACTION_TYPE.GAME_TEXT_REQUIRED_BEFORE_TRIGGERS);
    }

    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggersWhenInactiveInPlay(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return (List<OptionalGameTextTriggerAction>) getMindScannedActions(playerId, game, self, null, effect, MINDSCAN_ACTION_TYPE.GAME_TEXT_OPTIONAL_BEFORE_TRIGGERS);
    }


    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggersWhenInactiveInPlay(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return (List<OptionalGameTextTriggerAction>) getMindScannedActions(playerId, game, self, null, effect, MINDSCAN_ACTION_TYPE.OPPONENTS_CARD_GAME_TEXT_OPTIONAL_BEFORE_TRIGGERS);
    }


    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalBeforeTriggersWhenInactiveInPlay(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }


    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return (List<RequiredGameTextTriggerAction>) getMindScannedActions(null, game, self, effectResult, null, MINDSCAN_ACTION_TYPE.GAME_TEXT_REQUIRED_AFTER_TRIGGERS);
    }


    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }


    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return (List<TopLevelGameTextAction>) getMindScannedActions(playerId, game, self, null, null, MINDSCAN_ACTION_TYPE.OPPONENTS_CARD_GAME_TEXT_TOP_LEVEL_ACTIONS);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return (List<OptionalGameTextTriggerAction>) getMindScannedActions(playerId, game, self, effectResult, null, MINDSCAN_ACTION_TYPE.OPPONENTS_CARD_GAME_TEXT_OPTIONAL_AFTER_TRIGGERS);
    }

    private enum MINDSCAN_ACTION_TYPE {
        OPTIONAL_AFTER_ACTIONS,
        OPTIONAL_BEFORE_ACTIONS,
        OPPONENTS_CARD_GAME_TEXT_OPTIONAL_AFTER_TRIGGERS,
        OPPONENTS_CARD_GAME_TEXT_TOP_LEVEL_ACTIONS,
        GAME_TEXT_REQUIRED_AFTER_TRIGGERS,
        GAME_TEXT_OPTIONAL_AFTER_TRIGGERS,
        GAME_TEXT_TOP_LEVEL_ACTIONS,
        GAME_TEXT_LEAVES_TABLE_OPTIONAL_TRIGGERS,
        GAME_TEXT_OPTIONAL_BEFORE_TRIGGERS,
        OPPONENTS_CARD_GAME_TEXT_OPTIONAL_BEFORE_TRIGGERS,
        GAME_TEXT_REQUIRED_BEFORE_TRIGGERS
    };

    private List getMindScannedActions(String playerId, SwccgGame game, PhysicalCard self, EffectResult effectResult, Effect effect, MINDSCAN_ACTION_TYPE actionType){
        List actions = new ArrayList();
        if(game.getModifiersQuerying().hasMindscannedCharacter(game.getGameState(), self) && !game.getModifiersQuerying().mindscannedCharacterGameTextWasCanceled(game.getGameState(), self)) {
            SwccgCardBlueprint blueprint = game.getModifiersQuerying().getMindscannedCharacterBlueprint(game.getGameState(), self);
            switch(actionType) {
                case OPTIONAL_AFTER_ACTIONS:
                    actions = blueprint.getOptionalAfterActions(playerId, game, effectResult, self);
                    break;
                case OPTIONAL_BEFORE_ACTIONS:
                    actions = blueprint.getOptionalBeforeActions(playerId, game, effect, self);
                    break;
                case OPPONENTS_CARD_GAME_TEXT_OPTIONAL_AFTER_TRIGGERS:
                    actions = blueprint.getOpponentsCardOptionalAfterTriggers(playerId, game, effectResult, self);
                    break;
                case OPPONENTS_CARD_GAME_TEXT_TOP_LEVEL_ACTIONS:
                    actions = blueprint.getOpponentsCardTopLevelActions(playerId, game, self);
                    break;
                case GAME_TEXT_REQUIRED_AFTER_TRIGGERS:
                    actions = blueprint.getRequiredAfterTriggers(game, effectResult, self);
                    break;
                case GAME_TEXT_OPTIONAL_AFTER_TRIGGERS:
                    actions = blueprint.getOptionalAfterTriggers(playerId, game, effectResult, self);
                    break;
                case GAME_TEXT_TOP_LEVEL_ACTIONS:
                    actions = blueprint.getTopLevelActions(self.getOwner(), game, self);
                    break;
                case GAME_TEXT_LEAVES_TABLE_OPTIONAL_TRIGGERS:
                    actions = blueprint.getOptionalAfterTriggers(playerId, game, effectResult, self);
                    break;
                case GAME_TEXT_OPTIONAL_BEFORE_TRIGGERS:
                    actions = blueprint.getOptionalBeforeTriggers(playerId, game, effect, self);
                    break;
                case OPPONENTS_CARD_GAME_TEXT_OPTIONAL_BEFORE_TRIGGERS:
                    actions = blueprint.getOpponentsCardOptionalBeforeTriggers(playerId, game, effect, self);
                    break;
                case GAME_TEXT_REQUIRED_BEFORE_TRIGGERS:
                    actions = blueprint.getRequiredBeforeTriggers(game, effect, self);
                    break;
            }
        }
        return actions;
    }
}
