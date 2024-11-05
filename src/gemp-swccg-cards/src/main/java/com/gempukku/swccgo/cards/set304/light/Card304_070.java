package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Device
 * Title: Locked Door
 */
public class Card304_070 extends AbstractDevice {
    public Card304_070() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Locked Door", Uniqueness.UNRESTRICTED, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("For most people with a Lightsaber a locked door is a minor inconvenience. For Komilia...it's a disaster waiting to happen.");
        setGameText("Deploy at any interior site. Cannot be moved. Komilia may not deploy to same site. Following the turn this device is deployed, if Komilia is still present she is lost at end of any turn.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.interior_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveModifier(self));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.Komilia, Filters.sameSite(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            GameState gameState = game.getGameState();
            String currentPlayer = gameState.getCurrentPlayerId();
            int turnNumber = gameState.getPlayersLatestTurnNumber(currentPlayer);
            String key = currentPlayer + "|" + turnNumber;
            if (GameConditions.cardHasWhileInPlayDataSet(self)) {
                if (!GameConditions.cardHasWhileInPlayDataEquals(self, key)) {
                    Collection<PhysicalCard> droidsPresent = Filters.filterAllOnTable(game, Filters.and(Filters.Komilia, Filters.present(self)));
                    if (!droidsPresent.isEmpty()) {

                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                        action.setText("Make droids present lost");
                        // Perform result(s)
                        action.appendEffect(
                                new LoseCardsFromTableEffect(action, droidsPresent, true));
                        return Collections.singletonList(action);
                    }
                }
            }
            else {
                self.setWhileInPlayData(new WhileInPlayData(key));
            }
        }
        return null;
    }
}