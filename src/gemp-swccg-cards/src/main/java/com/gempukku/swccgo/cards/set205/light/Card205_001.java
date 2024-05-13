package com.gempukku.swccgo.cards.set205.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 5
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Wedge Antilles (V)
 */
public class Card205_001 extends AbstractRebel {
    public Card205_001() {
        super(Side.LIGHT, 3, 3, 3, 2, 6, "Commander Wedge Antilles", Uniqueness.UNIQUE, ExpansionSet.SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Promoted to commander for his heroism in the Battle of Yavin. Leader in charge of training new pilots assigned to Echo Base. Piloted Rogue 3 in the battle of Hoth.");
        setGameText("[Pilot] 3. While piloting Rogue 3, it is maneuver +2 and your Force drains here are +1. While piloting a T-47, Wedge draws one battle destiny if unable to otherwise and, if opponent draws more than one battle destiny here, may cancel one.");
        addPersona(Persona.WEDGE);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER, Keyword.ROGUE_SQUADRON);
        setSpecies(Species.CORELLIAN);
        setMatchingVehicleFilter(Filters.Rogue_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition pilotingRogue3 = new PilotingCondition(self, Filters.Rogue_3);
        Condition pilotingT47 = new PilotingCondition(self, Filters.T_47);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.Rogue_3, pilotingRogue3, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), pilotingRogue3, 1, playerId));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingT47, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isPiloting(game, self, Filters.T_47)
                && GameConditions.canCancelDestiny(game, playerId)) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            if (destinyDrawnResult.getNumDestinyDrawnSoFar() > 1) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Cancel opponent's destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new CancelDestinyEffect(action));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}