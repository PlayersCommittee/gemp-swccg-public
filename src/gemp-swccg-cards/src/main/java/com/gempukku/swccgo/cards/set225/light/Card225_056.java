package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.OccupiesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.EpicEventCalculationTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Character
 * Subtype: Rebel
 * Title: Orrimaarko (V)
 */
public class Card225_056 extends AbstractRebel {
    public Card225_056() {
        super(Side.LIGHT, 1, 4, 4, 4, 6, Title.Orrimaarko, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Dresselian scout and resistance leader. Worked tirelessly to combat the subjugation of his homeworld before Bothans brought him into contact with the Alliance.");
        setGameText("If you just won a battle here (or at same site as your scout general), opponent loses 1 Force. While on Endor, adds 1 to your [Endor] Epic Event total for each Endor location you occupy and, once per game, may cancel a Force drain at a related battleground.");
        addIcons(Icon.ENDOR, Icon.WARRIOR, Icon.VIRTUAL_SET_25);
        setSpecies(Species.DRESSELIAN);
        addKeywords(Keyword.SCOUT, Keyword.LEADER);
        setVirtualSuffix(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        Filter locationFilter = Filters.or(Filters.here(self), Filters.sameSiteAs(self,Filters.and(Filters.your(playerId), Filters.scout, Filters.general)));
        if (TriggerConditions.wonBattleAt(game, effectResult, playerId, locationFilter)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            String opponent = game.getOpponent(playerId);
            action.setText("Make opponent lose 1 Force");
            action.appendEffect(
                new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override 
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition orrimaarkoOnEndor = new OnCondition(self, Title.Endor);
        Filter yourEndorEpicEvent = Filters.and(Filters.your(self), Filters.icon(Icon.ENDOR), Filters.Epic_Event);
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EpicEventCalculationTotalModifier(self, yourEndorEpicEvent, orrimaarkoOnEndor, new OccupiesEvaluator(playerId, Filters.Endor_location)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ORRIMAARKO__CANCEL_FORCE_DRAIN;
        Filter relatedBattleground = Filters.and(Filters.relatedLocationTo(self, Filters.sameLocation(self)), Filters.battleground);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, relatedBattleground)
                && GameConditions.isOnSystem(game, self, Title.Endor)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canCancelForceDrain(game, self)){
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Force drain");
            // Update usage limit(s
            action.appendUsage(
                new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
