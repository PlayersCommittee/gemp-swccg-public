package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Character
 * Subtype: Alien
 * Title: Velken Tezeri (V)
 */
public class Card601_072 extends AbstractAlien {
    public Card601_072() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Velken Tezeri", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Assigned by Jabba to work with Hermi Odle. Former technician for the Empire. Developed a method to remotely control seekers. Plotting to kill Jabba.");
        setGameText("[Pilot] 2. Smuggler. May not be attacked. Velken may not target Luke with weapons. Once per game, when deployed to a battleground, opponent must use or lose 2 Force. While present at a pit, may add 1 to Force drains here, and Sarlacc is ferocity +1.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT, Icon.WARRIOR, Icon.BLOCK_7);
        addKeywords(Keyword.SMUGGLER);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotBeAttackedModifier(self));
        modifiers.add(new MayNotBeTargetedByWeaponUserModifier(self, Filters.Luke, self));
        modifiers.add(new FerocityModifier(self, Filters.Sarlacc, new PresentAtCondition(self, Filters.pit), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.wherePresent(self))
                && GameConditions.isPresentAt(game, self, Filters.pit)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to Force drain");
            // Perform result(s)
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__VELKEN_TEZERI__JUST_DEPLOYED;

        if (TriggerConditions.justDeployedTo(game, effectResult, self, Filters.battleground)
            && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.appendUsage(new OncePerGameEffect(action));

            if (!GameConditions.canUseForce(game, opponent, 2)) {
                action.appendEffect(new LoseForceEffect(action, opponent, 2));
            } else {
                action.appendEffect(new PlayoutDecisionEffect(action, game.getOpponent(self.getOwner()),
                        new MultipleChoiceAwaitingDecision("Use or lose 2 Force", new String[]{"Use 2 Force", "Lose 2 Force"}) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                if (index == 0) {
                                    game.getGameState().sendMessage(opponent + " chooses to use 2 Force");
                                    action.appendEffect(
                                            new UseForceEffect(action, opponent, 2));
                                } else if (index == 1) {
                                    game.getGameState().sendMessage(opponent + " chooses to lose 2 Force");
                                    action.appendEffect(
                                            new LoseForceEffect(action, opponent, 2));
                                }
                            }
                        }));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
