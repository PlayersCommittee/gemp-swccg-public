package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Character
 * Subtype: Sith
 * Title: Maul
 */
public class Card213_010 extends AbstractSith {
    public Card213_010() {
        super(Side.DARK, .5F, 4, 4, 6, 8, "Maul", Uniqueness.UNIQUE);
        setLore("Gangster. Crimson Dawn leader.");
        setGameText("Never deploys to a battleground. Once per turn, may add 1 to a just drawn battle or weapon destiny. " +
                "Also, once per turn, may subtract 1 from a just drawn battle or weapon destiny. Opponent may not cancel your destiny draws. Immune to attrition < 5.");
        addPersona(Persona.MAUL);
        addKeywords(Keyword.GANGSTER, Keyword.CRIMSON_DAWN, Keyword.LEADER);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_13);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NeverDeploysToLocationModifier(self, Filters.battleground));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        modifiers.add(new MayNotCancelDestinyDrawsModifier(self, self.getOwner(), game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        boolean mayNotModifyDestinies = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.MAUL__MAY_NOT_MODIFIY_DESTINIES);
        // Check condition(s)
        if ((TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult)
                || TriggerConditions.isBattleDestinyJustDrawn(game, effectResult))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && !mayNotModifyDestinies) {

            OptionalGameTextTriggerAction action1 = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action1.setText("Add 1 to destiny draw");
            action1.appendUsage(
                    new OncePerTurnEffect(action1)
            );
            // Perform result(s)
            action1.appendEffect(
                    new ModifyDestinyEffect(action1, 1));
            actions.add(action1);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if ((TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult)
                || TriggerConditions.isBattleDestinyJustDrawn(game, effectResult))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && !mayNotModifyDestinies) {

            OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action2.setText("Subtract 1 from destiny draw");
            action2.appendUsage(
                    new OncePerTurnEffect(action2)
            );
            // Perform result(s)
            action2.appendEffect(
                    new ModifyDestinyEffect(action2, -1));
            actions.add(action2);
        }
        return actions;
    }
}
