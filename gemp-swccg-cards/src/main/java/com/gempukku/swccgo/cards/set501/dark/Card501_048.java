package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NeverDeploysToLocationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Character
 * Subtype: Sith
 * Title: Maul
 */
public class Card501_048 extends AbstractSith {
    public Card501_048() {
        super(Side.DARK, .5F, 4, 4, 6, 8, "Maul", Uniqueness.UNIQUE);
        setLore("Gangster. Crimson Dawn leader.");
        setGameText("Never deploys to a battleground. While no other Dark Jedi on table your characters deploy -1 to the same location as your gangsters and if alone, once per turn may add or subtract 1 from a just drawn weapon or battle destiny. Immune to attrition < 5.");
        addPersona(Persona.MAUL);
        addKeywords(Keyword.GANGSTER, Keyword.CRIMSON_DAWN, Keyword.LEADER);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        setTestingText("Maul");
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
        modifiers.add(new DeployCostToLocationModifier(self,
                Filters.and(Filters.your(self.getOwner()), Filters.character),
                new CantSpotCondition(self, Filters.and(Filters.Dark_Jedi, Filters.not(self))),
                -1,
                Filters.sameLocationAs(self, Filters.gangster)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if ((TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult)
                || TriggerConditions.isBattleDestinyJustDrawn(game, effectResult))
                && GameConditions.isAlone(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.setText("Add 1 to destiny draw");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 1));
            actions.add(action);

            action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.setText("Subtract 1 from destiny draw");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            actions.add(action);
        }
        return actions;
    }
}
