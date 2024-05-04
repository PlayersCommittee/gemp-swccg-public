package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.MoveMobileSystemUsingHyperspeedAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * The abstract class providing the common implementation for mobile systems.
 */
public abstract class AbstractMobileSystem extends AbstractSystem {
    private Float _hyperspeed;

    /**
     * Creates a blueprint for a mobile system.
     * @param side the side of the Force
     * @param title the card title
     * @param parsec the parsec number
     * @param systemOrbiting the system this must deploy orbiting
     */
    protected AbstractMobileSystem(Side side, String title, int parsec, String systemOrbiting, ExpansionSet expansionSet, Rarity rarity) {
        super(side, title, parsec, systemOrbiting, expansionSet, rarity);
        _hyperspeed = 0f;
        addIcons(Icon.MOBILE);
    }

    /**
     * Creates a blueprint for a mobile system.
     * @param side the side of the Force
     * @param title the card title
     * @param parsec the parsec number
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractMobileSystem(Side side, String title, float hyperspeed, int parsec, ExpansionSet expansionSet, Rarity rarity) {
        super(side, title, parsec, null, expansionSet, rarity);
        _hyperspeed = hyperspeed;
        addIcons(Icon.MOBILE);
    }

    @Override
    public final boolean hasHyperspeedAttribute() {
        return true;
    }

    @Override
    public final Float getHyperspeed() {
        return _hyperspeed;
    }

    /**
     * Gets the move using hyperspeed action for the card if it can move using hyperspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can be move
     * @return the play card action
     */
    @Override
    public Action getMoveUsingHyperspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (playerId.equals(game.getDarkPlayer())
                && GameConditions.isPhaseForPlayer(game, Phase.MOVE, playerId)
                && !modifiersQuerying.hasPerformedRegularMoveThisTurn(self)
                && !modifiersQuerying.mayNotMoveUsingHyperspeed(game.getGameState(), self)
                && modifiersQuerying.getHyperspeed(game.getGameState(), self) >= 1
                && (forFree || (modifiersQuerying.getForceAvailableToUse(game.getGameState(), playerId)
                                >= modifiersQuerying.getMoveUsingHyperspeedCost(game.getGameState(), self, null, null, false, 0)))) {
            return new MoveMobileSystemUsingHyperspeedAction(playerId, game, self, forFree);
        }
        return null;
    }
}
