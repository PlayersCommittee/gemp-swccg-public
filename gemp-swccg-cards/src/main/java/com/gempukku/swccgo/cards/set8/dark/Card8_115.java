package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Wallen
 */
public class Card8_115 extends AbstractImperial {
    public Card8_115() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Sergeant Wallen", Uniqueness.UNIQUE);
        setLore("Stormtrooper assigned to Colonel Dyer's command. His unit was recommended to Commander Igar by Governor Yount of the Wakeelmui garrison.");
        setGameText("May deploy for free to a battle you just initiated at a battleground site.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.STORMTROOPER);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.battleground_site)) {
            if (Filters.deployableToLocation(self, Filters.battleLocation, true, 0).accepts(game, self)) {

                PlayCardAction playCardAction = self.getBlueprint().getPlayCardAction(playerId, game, self, self, true, 0, null, null, null, null, null, false, 0, Filters.battleLocation, null);
                if (playCardAction != null) {
                    return Collections.singletonList(playCardAction);
                }
            }
        }
        return null;
    }
}
