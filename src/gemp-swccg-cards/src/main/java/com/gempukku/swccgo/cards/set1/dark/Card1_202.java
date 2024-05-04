package com.gempukku.swccgo.cards.set1.dark;

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
 * Set: Premiere
 * Type: Device
 * Title: Droid Detector
 */
public class Card1_202 extends AbstractDevice {
    public Card1_202() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Droid Detector", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("To keep out the mechanicals he so detested, Wuher installed an automatic droid detector at the entrance to the Mos Eisley Cantina.");
        setGameText("Deploy at any interior site. Cannot be moved. Droids may not deploy to same site. Following the turn this device is deployed, all droids present are lost at end of any turn.");
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
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.droid, Filters.sameSite(self)));
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
                    Collection<PhysicalCard> droidsPresent = Filters.filterAllOnTable(game, Filters.and(Filters.droid, Filters.present(self)));
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