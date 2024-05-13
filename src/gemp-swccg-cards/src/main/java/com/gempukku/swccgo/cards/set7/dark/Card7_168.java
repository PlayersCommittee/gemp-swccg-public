package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Boelo
 */
public class Card7_168 extends AbstractAlien {
    public Card7_168() {
        super(Side.DARK, 1, 4, 3, 3, 3, Title.Boelo, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Naroon Cuthus' predecessor as Jabba's right hand man. Sees what he wants to see. Hears what he wants to hear. Has a pet womp rat named Worra.");
        setGameText("Deploys only on Tatooine or to same location as Jabba. When in a battle either at Audience Chamber or with your alien leader, may cancel one opponent's battle destiny just drawn.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_on_Tatooine, Filters.locationAndCardsAtLocation(Filters.sameLocationAs(self, Filters.Jabba)));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && (GameConditions.isInBattleAt(game, self, Filters.Audience_Chamber)
                    || GameConditions.isInBattleWith(game, self, Filters.and(Filters.your(self), Filters.alien_leader)))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel opponent's battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
