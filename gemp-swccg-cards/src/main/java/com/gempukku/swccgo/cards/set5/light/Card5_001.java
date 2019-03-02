package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Han Solo
 */
public class Card5_001 extends AbstractRebel {
    public Card5_001() {
        super(Side.LIGHT, 1, 4, 4, 3, 7, "Captain Han Solo", Uniqueness.UNIQUE);
        setLore("Smuggler and scoundrel. His piloting abilities have become legend in the Rebellion. Intends to leave the Alliance to pay off Jabba the Hutt.");
        setGameText("Deploys only on Falcon, Hoth or Cloud City. Adds 3 to power of anything he pilots. When piloting Falcon, adds one battle destiny and 2 to maneuver. Once during each battle, may use 2 Force to cancel one opponent's destiny just drawn and cause a re-draw.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
        addPersona(Persona.HAN);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Falcon, Filters.Deploys_on_Hoth, Filters.Deploys_on_Cloud_City);
    }

    @Override
    protected Filter getGameTextValidStarshipOrVehicleSimultaneousDeployTargetFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Falcon;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingFalcon = new PilotingCondition(self, Filters.Falcon);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, pilotingFalcon, 1));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), pilotingFalcon, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if ((TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId)))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
