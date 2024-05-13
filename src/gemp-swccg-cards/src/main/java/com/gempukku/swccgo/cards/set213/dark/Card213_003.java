package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Dark Jedi Master
 * Title: Darth Tyranus
 */
public class Card213_003 extends AbstractDarkJediMaster {
    public Card213_003() {
        super(Side.DARK, 1, 7, 5, 7, 8, "Darth Tyranus", Uniqueness.UNIQUE, ExpansionSet.SET_13, Rarity.V);
        setLore("Serennian leader. Trade Federation.");
        setGameText("Deploys -1 to an [Episode I] location. Jedi here are power and immunity to attrition -1. During battle or lightsaber combat involving Dooku, may cancel and redraw an opponent's just drawn destiny. Immune to Sorry About The Mess and attrition < 6.");
        addIcons(Icon.WARRIOR, Icon.PILOT, Icon.SEPARATIST, Icon.EPISODE_I, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.LEADER);
        addPersona(Persona.DOOKU);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.icon(Icon.EPISODE_I)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.icon(Icon.EPISODE_I)));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Jedi, Filters.here(self)), -1));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.Jedi, Filters.here(self)), -1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sorry_About_The_Mess));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && (GameConditions.isInBattle(game, self) || GameConditions.isDuringLightsaberCombatWithParticipant(game, self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel and re-draw destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
