package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtRandomCardsInOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Wyron Serper
 */
public class Card3_026 extends AbstractRebel {
    public Card3_026() {
        super(Side.LIGHT, 2, 2, 1, 2, 3, "Wyron Serper", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.U2);
        setLore("Rebel spy. Served undercover as a sensor specialist aboard the Star Destroyer Avenger. Assigned to scan for Imperial ships through the meteor activity of the Hoth system.");
        setGameText("Once during each of your control phases, may peek at X cards randomly selected from opponent's hand, where X = number of [Dark Side Force] icons at same site. Also, when you are drawing destiny, adds 2 to the destiny of any card with 'scan' in the title.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasHand(game, opponent)
                && GameConditions.isAtLocation(game, self, Filters.site)) {
            int forceIcons = GameConditions.getNumForceIconsHere(game, self, true, false);
            if (forceIcons > 0
                    && GameConditions.numCardsInHand(game, opponent) >= forceIcons) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Peek at " + forceIcons + " random cards in opponents hand");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PeekAtRandomCardsInOpponentsHandEffect(action, playerId, forceIcons));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, Filters.and(Filters.your(self), Filters.titleContains("scan")), 2));
        return modifiers;
    }
}
