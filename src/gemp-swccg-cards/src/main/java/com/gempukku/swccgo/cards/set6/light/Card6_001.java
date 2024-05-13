package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Droid
 * Title: 8D8
 */
public class Card6_001 extends AbstractDroid {
    public Card6_001() {
        super(Side.LIGHT, 5, 1, 1, 3, Title._8D8, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Starship maintenance droid. Sold into the service of Jabba. Sympathetic to the droids and aliens it is forced to torture. Hates EV-9D9.");
        setGameText("May cancel Torture, Aiiii! Aaa! Aggggggggggggg! or Sonic Bombardment targeting a character at same site. Once during each of your turns, if with any imprisoned captive, may draw destiny: if destiny > 3, randomly select one captive there to be released.");
        addModelType(ModelType.MAINTENANCE);
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.or(Filters.Torture, Filters.Aiiii_Aaa_Agggggggggg,
                Filters.Sonic_Bombardment), Filters.and(Filters.character, Filters.atSameSite(self)))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isWith(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.imprisoned)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Release a random captive");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                return;
                            }

                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny > 3) {
                                gameState.sendMessage("Result: Successful");
                                Collection<PhysicalCard> captives = Filters.filterActive(game, self,
                                        SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captive, Filters.here(self)));
                                if (!captives.isEmpty()) {
                                    PhysicalCard captive = GameUtils.getRandomCards(captives, 1).get(0);
                                    action.appendEffect(
                                            new ReleaseCaptiveEffect(action, captive));
                                }
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
