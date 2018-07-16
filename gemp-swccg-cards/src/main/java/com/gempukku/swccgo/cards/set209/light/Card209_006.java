package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Republic
 * Title: General Kenobi
 */
public class Card209_006 extends AbstractRepublic {
    public Card209_006() {
        super(Side.LIGHT, 1, 6, 6, 6, 9, "General Kenobi", Uniqueness.UNIQUE);
        setLore("Jedi Council member. Leader.");
        setGameText("During battle, if alone or with a clone, may activate 1 Force or add 1 to a just drawn destiny. Once per game, may [upload] a clone or [Reflections III] lightsaber. Immune to attrition < 6.");
        addPersona(Persona.OBIWAN);
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.GENERAL, Keyword.LEADER, Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && (GameConditions.isInBattleWith(game, self, Filters.clone) || GameConditions.isAlone(game, self))
                && GameConditions.canActivateForce(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                     new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                     new ActivateForceEffect(action, playerId, 1));
            actions.add(action);
            }

        gameTextActionId = GameTextActionId.GENERAL_KENOBI__UPLOAD_CLONE_OR_REF_III_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a clone or [Reflections III] lightsaber into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.clone, Filters.and(Icon.REFLECTIONS_III, Filters.lightsaber)), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && (GameConditions.isInBattleWith(game, self, Filters.clone) || GameConditions.isAlone(game, self))) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 1 to destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }

}
