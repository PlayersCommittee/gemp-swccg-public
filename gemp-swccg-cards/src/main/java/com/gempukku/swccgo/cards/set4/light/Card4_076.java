package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractJediTest;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CompleteJediTestEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Jedi Test
 * Title: Domain Of Evil
 */
public class Card4_076 extends AbstractJediTest {
    public Card4_076() {
        super(Side.LIGHT, 3, "Domain Of Evil", ExpansionSet.DAGOBAH, Rarity.U);
        setGameText("Deploy on Dagobah: Jungle or Dagobah: Cave. Target a mentor on Dagobah and an apprentice who has completed Jedi Test #2. Attempt when apprentice is present at the end of your turn and none of your cards participated in battles, Force drains or Jedi Tests during that turn. Draw training destiny. If destiny + apprentice's ability > 14, test completed. Place on apprentice. You may subtract 1 from each of opponent's destiny draws.");
        addIcons(Icon.DAGOBAH);
        addKeyword(Keyword.JEDI_TEST_3);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Dagobah_Jungle, Filters.Dagobah_Cave);
    }

    @Override
    protected Filter getGameTextValidMentorFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        return Filters.on(Title.Dagobah);
    }

    @Override
    protected Filter getGameTextValidApprenticeFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        return Filters.apprenticeTargetedByJediTest(Filters.and(Filters.completed_Jedi_Test, Filters.Jedi_Test_2));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, self)
                && !GameConditions.isJediTestCompleted(game, self)) {
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            if (modifiersQuerying.getNumBattlesInitiatedThisTurn(playerId) == 0
                    && modifiersQuerying.getNumForceDrainsInitiatedThisTurn() == 0
                    && !modifiersQuerying.hasAttemptedJediTests()) {
                final PhysicalCard apprentice = Filters.findFirstActive(game, self, Filters.and(Filters.mayAttemptJediTest(self), Filters.present(self)));
                if (apprentice != null) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Attempt Jedi Test #3");
                    // Update usage limit(s)
                    action.appendUsage(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    game.getModifiersQuerying().attemptedJediTest(self, apprentice);
                                }
                            });
                    // Perform result(s)
                    action.appendEffect(
                            new DrawDestinyEffect(action, playerId, 1, DestinyType.TRAINING_DESTINY) {
                                @Override
                                protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                    return Collections.singletonList(apprentice);
                                }
                                @Override
                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                    GameState gameState = game.getGameState();
                                    ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                    if (totalDestiny == null) {
                                        gameState.sendMessage("Result: Failed due to failed destiny draw");
                                        return;
                                    }

                                    gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                    float ability = modifiersQuerying.getAbility(gameState, apprentice);
                                    gameState.sendMessage("Apprentice's ability: " + GuiUtils.formatAsString(ability));
                                    float total = totalDestiny + ability;
                                    gameState.sendMessage("Total: " + GuiUtils.formatAsString(total));

                                    if (total > 14) {
                                        gameState.sendMessage("Result: Succeeded");
                                        action.appendEffect(
                                                new CompleteJediTestEffect(action, self));
                                    } else {
                                        gameState.sendMessage("Result: Failed");
                                    }
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isJediTestCompleted(game, self)
                && TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract 1 from destiny");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            return Collections.singletonList(action);
        }

        return null;
    }
}