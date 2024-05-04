package com.gempukku.swccgo.cards.set7.light;

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
 * Title: Imperial Atrocity
 */
public class Card7_065 extends AbstractImmediateEffect {
    public Card7_065() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, Title.Imperial_Atrocity, Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("The Empire's ruthless tactics at times unintentionally create support for the cause of the Rebel Alliance.");
        setGameText("If you occupy at least two battlegrounds and just lost more than 2 Force to a Force drain at a location, deploy on that location (limit one per Force drain). Opponent loses 2 Force for each Imperial Atrocity on table.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_ATROCITY__DEPLOY_ON_LOCATION;

        // Check condition(s)
        if (TriggerConditions.justLostMoreThanXForceFromForceDrain(game, effectResult, playerId, 2)
                && GameConditions.occupies(game, playerId, 2, Filters.battleground)
                && GameConditions.isOncePerForceDrain(game, self, gameTextActionId)) {
            PhysicalCard location = game.getGameState().getForceDrainLocation();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(location), null);
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
            int forceToLose = 2 * Filters.countActive(game, self, Filters.Imperial_Atrocity);

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