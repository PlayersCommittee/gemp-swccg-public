package com.gempukku.swccgo.cards.set214.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromBottomOfForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * SubType: First Order / Dark Jedi Master
 * Title: Palpatine, Emperor Returned
 */
public class Card214_008 extends AbstractDarkJediMasterFirstOrder {
    public Card214_008() {
        super(Side.DARK, 4, 4, 2, 7, 9, "Palpatine, Emperor Returned", Uniqueness.UNIQUE);
        setLore("Leader.");
        setGameText("Never deploys or moves (even if carried) to a location with a [Light Side] icon. Once per turn, may draw bottom card of your Force Pile. Once per game, if about to be lost, may take him into hand. Immune to attrition.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_14);
        addPersona(Persona.EMPEROR);
        addKeywords(Keyword.LEADER);
        setTestingText("Palpatine, Emperor Returned");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.icon(Icon.LIGHT_FORCE)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.or(self, Filters.hasAttachedWithRecursiveChecking(self)), Filters.icon(Icon.LIGHT_FORCE)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasForcePile(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw bottom card of Force Pile");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new DrawCardIntoHandFromBottomOfForcePileEffect(action, playerId)
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.EMPEROR_RETURNED__RETURN_TO_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Return to hand");
            action.setActionMsg("Return " + GameUtils.getCardLink(self) + " to hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(
                    new ReturnCardToHandFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
