package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Subtype: Immediate
 * Title: Imperial Propaganda
 */
public class Card7_230 extends AbstractImmediateEffect {
    public Card7_230() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, Title.Imperial_Propaganda, Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Imperial data transmissions depict Rebel incursions as terrorist acts. The Alliance is portrayed as a danger to civilians of the Empire.");
        setGameText("If you occupy at least two battlegrounds and just lost more than 2 Force to a Force drain at a location, deploy on that location (limit one per Force drain). Opponent loses 2 Force for each Imperial Propaganda on table.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_PROPAGANDA__DEPLOY_ON_LOCATION;

        // Check condition(s)
        if (TriggerConditions.justLostMoreThanXForceFromForceDrain(game, effectResult, playerId, 2)
                && GameConditions.occupies(game, playerId, 2, Filters.battleground)
                && GameConditions.isOncePerForceDrain(game, self, gameTextActionId)) {
            PhysicalCard location = game.getGameState().getForceDrainLocation();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameLocationId(location), null);
            if (action != null) {
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerForceDrainEffect(action, gameTextActionId));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            int forceToLose = 2 * Filters.countActive(game, self, Filters.Imperial_Propaganda);

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            action.setText("Make " + opponent + " lose " + forceToLose + " Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, forceToLose));
            return Collections.singletonList(action);
        }
        return null;
    }
}