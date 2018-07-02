package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Droid
 * Title: LE-BO2D9 (Leebo)
 */
public class Card10_009 extends AbstractDroid {
    public Card10_009() {
        super(Side.LIGHT, 3, 2, 2, 4, Title.Leebo, Uniqueness.UNIQUE);
        setArmor(4);
        setLore("Smuggler. Heavily modified Cybot Galactica LE-series repair droid. Information broker. Provides information gained through underworld channels to the Alliance.");
        setGameText("May be battled. While aboard any freighter adds 2 to hyperspeed and may draw one battle destiny if not able to otherwise. If you have completed Rycar's Run or Kessel Run, opponent loses 1 Force (2 if both) during each of your control phases.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER, Keyword.INFORMATION_BROKER);
        addModelTypes(ModelType.MAINTENANCE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeBattledModifier(self));
        modifiers.add(new HyperspeedModifier(self, Filters.and(Filters.freighter, Filters.hasAboard(self)), 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AboardCondition(self, Filters.freighter), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            boolean completedRycarsRun = GameConditions.hasCompletedUtinniEffect(game, playerId, Filters.Rycars_Run);
            boolean completedKesselRun = GameConditions.hasCompletedUtinniEffect(game, playerId, Filters.Kessel_Run);
            int forceToLose = (completedRycarsRun || completedKesselRun) ? ((completedRycarsRun && completedKesselRun) ? 2 : 1) : 0;
            if (forceToLose > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose " + forceToLose + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), forceToLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        // Check if reached end of owner's control phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {

            boolean completedRycarsRun = GameConditions.hasCompletedUtinniEffect(game, playerId, Filters.Rycars_Run);
            boolean completedKesselRun = GameConditions.hasCompletedUtinniEffect(game, playerId, Filters.Kessel_Run);
            int forceToLose = (completedRycarsRun || completedKesselRun) ? ((completedRycarsRun && completedKesselRun) ? 2 : 1) : 0;
            if (forceToLose > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose " + forceToLose + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), forceToLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
