package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.MovingResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Maneuver Check
 */
public class Card2_123 extends AbstractNormalEffect {
    public Card2_123() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Maneuver Check", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setLore("'Luke, at that speed will you be able to pull out in time?'");
        setGameText("Deploy on Death Star:Trench. Opponent must make maneuver check for starfighters leaving the trench. For each starfighter, owner draws destiny. If destiny + maneuver < 5, starfighter is lost.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_Trench;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.Death_Star_Trench));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.movingFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.starfighter), Filters.Death_Star_Trench)) {
            String opponent = game.getOpponent(self.getOwner());
            final PhysicalCard starfighter = ((MovingResult) effectResult).getCardMoving();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Maneuver check for " + GameUtils.getFullName(starfighter));
            action.setActionMsg("Make " + opponent + " make maneuver check for " + GameUtils.getCardLink(starfighter));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, opponent) {
                        @Override
                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                            return Collections.singletonList(starfighter);
                        }
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                            gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                            float maneuver = modifiersQuerying.getManeuver(gameState, starfighter);
                            gameState.sendMessage("Maneuver: " + GuiUtils.formatAsString(maneuver));

                            if (((totalDestiny != null ? totalDestiny : 0) + maneuver) < 5) {
                                gameState.sendMessage("Result: Succeeded");
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, starfighter));
                            }
                            else {
                                gameState.sendMessage("Result: Failed");
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}