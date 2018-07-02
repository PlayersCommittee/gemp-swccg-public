package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Wuher
 */
public class Card1_198 extends AbstractAlien {
    public Card1_198() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Wuher", Uniqueness.UNIQUE);
        setLore("Gruff, surly, no-blasters-allowed bartender. Hates droids. 'We don't serve their kind here.' Wants to concoct the perfect drink for Jabba so he can work as his personal bartender.");
        setGameText("If at the beginning of your control phase any weapons or droids are present at same site, all of them are lost. If in the Cantina, all weapons and droids there are immediately lost.");
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, effectResult, Phase.CONTROL, self.getOwner())
                || (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.isAtLocation(game, self, Filters.Cantina))) {
            Collection<PhysicalCard> weaponsAndDroids = Filters.filterAllOnTable(game, Filters.and(Filters.or(Filters.weapon, Filters.droid), Filters.presentAt(Filters.sameSite(self))));
            if (!weaponsAndDroids.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make weapons and droids lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, weaponsAndDroids, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
