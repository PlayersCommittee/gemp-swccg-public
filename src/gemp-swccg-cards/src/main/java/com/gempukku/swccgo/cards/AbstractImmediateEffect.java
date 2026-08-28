package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/**
 * The abstract class providing the common implementation for Immediate Effects.
 */
public abstract class AbstractImmediateEffect extends AbstractEffect {
    private static final ThreadLocal<Boolean> FORCE_MAY_DEPLOY_FOR_FREE = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /**
     * Creates a blueprint for an Immediate Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractImmediateEffect(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, playCardZoneOption, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.IMMEDIATE);
    }

    /**
     * Determines if the card can be played during the current phase.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if card can be played during the current phase, otherwise false
     */
    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return false;
    }

    private boolean shouldOfferMayDeployForFree(SwccgGame game, PhysicalCard self) {
        return game.getModifiersQuerying().mayDeployForFree(game.getGameState(), self)
                && !game.getModifiersQuerying().grantedDeployForFree(game.getGameState(), self, null);
    }

    private void appendForFreeLabel(PlayCardAction freeAction) {
        String text = freeAction.getText();
        if (text != null && !text.toLowerCase().contains("for free")) {
            freeAction.setText(text + " for free");
        }
    }

    private boolean forceMayDeployForFree() {
        return Boolean.TRUE.equals(FORCE_MAY_DEPLOY_FOR_FREE.get());
    }

    @Override
    public List<PlayCardAction> getPlayCardActions(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        if (!forFree && forceMayDeployForFree()) {
            forFree = true;
        }
        return super.getPlayCardActions(playerId, game, self, sourceCard, forFree, changeInCost, deploymentOption, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deployTargetFilter, specialLocationConditions);
    }

    @Override
    public List<Action> getTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = super.getTopLevelActions(playerId, game, self);
        if (canPlayCardDuringCurrentPhase(playerId, game, self)
                && (self.getZone() != Zone.STACKED || game.getModifiersQuerying().mayDeployAsIfFromHand(game.getGameState(), self))) {
            boolean forFree = isCardTypeAlwaysPlayedForFree() || game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS;
            if (!forFree && shouldOfferMayDeployForFree(game, self)) {
                List<PlayCardAction> freePlayCardActions = getPlayCardActions(playerId, game, self, self, true, 0, null, null, null, null, null, false, 0, Filters.any, null);
                if (freePlayCardActions != null) {
                    for (PlayCardAction freeAction : freePlayCardActions) {
                        appendForFreeLabel(freeAction);
                        actions.add(freeAction);
                    }
                }
            }
        }
        return actions;
    }

    @Override
    public List<Action> getOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<Action> actions = super.getOptionalBeforeActions(playerId, game, effect, self);
        if (self.getZone() != Zone.STACKED || game.getModifiersQuerying().mayDeployAsIfFromHand(game.getGameState(), self)) {
            if (shouldOfferMayDeployForFree(game, self)) {
                FORCE_MAY_DEPLOY_FOR_FREE.set(Boolean.TRUE);
                try {
                    List<PlayCardAction> freeActions = getGameTextOptionalBeforeActions(playerId, game, effect, self, self.getCardId());
                    if (freeActions != null) {
                        for (PlayCardAction freeAction : freeActions) {
                            appendForFreeLabel(freeAction);
                            actions.add(freeAction);
                        }
                    }
                }
                finally {
                    FORCE_MAY_DEPLOY_FOR_FREE.set(Boolean.FALSE);
                }
            }
        }
        return actions;
    }

    @Override
    public List<Action> getOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<Action> actions = super.getOptionalAfterActions(playerId, game, effectResult, self);
        if (self.getZone() != Zone.STACKED || game.getModifiersQuerying().mayDeployAsIfFromHand(game.getGameState(), self)) {
            if (shouldOfferMayDeployForFree(game, self)) {
                FORCE_MAY_DEPLOY_FOR_FREE.set(Boolean.TRUE);
                try {
                    List<PlayCardAction> freeActions = getGameTextOptionalAfterActions(playerId, game, effectResult, self, self.getCardId());
                    if (freeActions != null) {
                        for (PlayCardAction freeAction : freeActions) {
                            appendForFreeLabel(freeAction);
                            actions.add(freeAction);
                        }
                    }
                }
                finally {
                    FORCE_MAY_DEPLOY_FOR_FREE.set(Boolean.FALSE);
                }
            }
        }
        return actions;
    }
}
