package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateForceDrainCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Objective
 * Title: Rescue The Princess / Sometimes I Amaze Even Myself (V)
 */
public class Card215_017_BACK extends AbstractObjective {
    public Card215_017_BACK() {
        super(Side.LIGHT, 7, Title.Sometimes_I_Amaze_Even_Myself, ExpansionSet.SET_15, Rarity.V);
        setVirtualSuffix(true);
        setGameText("For remainder of game, I Can't Believe He's Gone may only add power in battles involving Leia or Luke." +
                "While this side up, for opponent to initiate a Force drain, opponent must use +1 Force. Whenever you 'hit' a character with a blaster, opponent loses 1 Force. May place Obi-Wan out of play from a Death Star site to cancel a battle just initiated anywhere on Death Star. During opponent's draw phase, if opponent did not initiate a battle this turn, may retrieve 1 Force." +
                "Flip this card if Leia is not at a Death Star site.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourDeathStarSites = Filters.and(Filters.your(playerId), Filters.Death_Star_site);
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ForceGenerationModifier(self, yourDeathStarSites, 1, self.getOwner()));
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.and(Filters.Luke, Filters.abilityMoreThan(4)), Filters.and(Filters.Jedi, Filters.or(Filters.icon(Icon.EPISODE_I), Filters.icon(Icon.EPISODE_VII)))), playerId));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Set_Your_Course_For_Alderaan, ModifyGameTextType.SET_YOUR_COURSE_FOR_ALDERAAN__ONLY_AFFECTS_DARK_SIDE_DEATH_STAR_SITES));
        modifiers.add(new InitiateForceDrainCostModifier(self, 1, game.getOpponent(self.getOwner())));
        modifiers.add(new MayNotBeConvertedModifier(self, Filters.Death_Star_site));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.DRAW)
                && !GameConditions.hasInitiatedBattleThisTurn(game, opponent)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Path_Of_Least_Resistance))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.canSpot(game, self, Filters.and(Filters.ObiWan, Filters.at(Filters.Death_Star_site)))
                && TriggerConditions.battleInitiatedAt(game, effectResult, Filters.on(Title.Death_Star))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel battle");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, Filters.findFirstActive(game, self, Filters.ObiWan)));
            // Perform result(s)
            action.appendEffect(
                    new CancelBattleEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.SOMETIMES_I_AMAZE_EVEN_MYSELF__FLIP_ACTION;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && TriggerConditions.cardFlipped(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action,
                            new ModifyGameTextModifier(self, Filters.I_Cant_Believe_Hes_Gone, ModifyGameTextType.I_CANT_BELIEVE_HES_GONE__ONLY_EFFECTS_BATTLES_WITH_LUKE_OR_LEIA),
                            "I Can't Believe He's Gone may only add power in battles involving Luke or Leia")
            );
            actions.add(action);
        }

        if (GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Leia)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.appendEffect(
                    new FlipCardEffect(action, self)
            );
            actions.add(action);
        }

        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.and(Filters.your(self.getOwner()), Filters.blaster))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1)
            );
            actions.add(action);
        }

        return actions;
    }
}
