package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerDuelEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 5
 * Type: Character
 * Subtype: Republic
 * Title: Obi-Wan Kenobi, Jedi Knight (V)
 */
public class Card601_161 extends AbstractRepublic {
    public Card601_161() {
        super(Side.LIGHT, 1, 7, 6, 6, 8, "Obi-Wan Kenobi, Jedi Knight", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Padawan learner promoted to Jedi Knight after his encounter with Darth Maul. Has sworn to train Anakin Skywalker, even if the Jedi Council forbids it.");
        setGameText("Deploys -1 to [Episode I] sites. During a battle (or duel) with a Dark Jedi, unless Obi-Wan's Journal on table, may lose 1 Force to cancel a just drawn duel or weapon destiny. Immune to attrition < 5.");
        addPersona(Persona.OBIWAN);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.and(Icon.EPISODE_I, Filters.site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattleWith(game, self, Filters.Dark_Jedi)
                && GameConditions.canCancelDestiny(game, playerId)
                && !GameConditions.canSpot(game, self, Filters.ObiWans_Journal)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel opponent's destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));

            actions.add(action);
        }

        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.isOncePerDuel(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringDuelWithParticipant(game, self)
                && GameConditions.isDuringDuelWithParticipant(game, Filters.Dark_Jedi)
                && GameConditions.canCancelDestiny(game, playerId)
                && !GameConditions.canSpot(game, self, Filters.ObiWans_Journal)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel opponent's destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerDuelEffect(action));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));

            actions.add(action);
        }
        return actions;
    }
}
