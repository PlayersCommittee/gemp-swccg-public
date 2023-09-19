package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceAtLocationFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DuelTotalModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: Ben Kenobi
 */
public class Card7_004 extends AbstractRebel {
    public Card7_004() {
        super(Side.LIGHT, 1, 5, 5, 6, 9, "Ben Kenobi", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Served Bail Organa during the Clone Wars. Saved Anakin's lightsaber until he was able to give it to Luke. Hasn't gone by the name Obi-Wan for a long time.");
        setGameText("Deploys only on Tatooine. When in a duel, adds 2 to your total. Once per turn, if a battle just ended, may 'revive' (place here from Lost Pile) your character forfeited from same site this turn. Immune to attrition < 5.");
        addPersona(Persona.OBIWAN);
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DuelTotalModifier(self, self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BEN_KENOBI__REVIVE_CHARACTER;

        // Check condition(s)
        if (TriggerConditions.battleEndedAt(game, effectResult, Filters.sameSite(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)
                && GameConditions.canReviveCharacters(game)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("'Revive' a forfeited character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlaceAtLocationFromLostPileEffect(action, playerId, Filters.and(Filters.your(self), Filters.character,
                            Filters.forfeitedFromLocationThisTurn(Filters.sameSite(self))), Filters.sameSite(self), false, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
