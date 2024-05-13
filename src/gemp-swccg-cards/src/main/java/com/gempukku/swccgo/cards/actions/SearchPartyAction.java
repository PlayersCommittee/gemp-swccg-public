package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.FindMissingCharacterEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotJoinSearchPartyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.List;

/**
 * An action that forms a Search Party to search for a 'missing' character.
 */
public class SearchPartyAction extends AbstractTopLevelRuleAction {
    private String _playerId;
    private PhysicalCard _site;
    private boolean _searchPartyStarted;
    private Collection<PhysicalCard> _searchParty;
    private SearchPartyAction _that;

    /**
     * Creates an action that forms a Search Party to search for a 'missing' character.
     * @param playerId the player to form the Search Party
     * @param site the site
     */
    public SearchPartyAction(final String playerId, final PhysicalCard site) {
        super(site, playerId);
        _playerId = playerId;
        _site = site;
        _that = this;

        appendTargeting(
                new ChooseCardsOnTableEffect(_that, playerId, "Choose members of search party", 1, Integer.MAX_VALUE, Filters.canJoinSearchPartyAt(playerId, site)) {
                    @Override
                    protected void cardsSelected(Collection<PhysicalCard> cards) {
                        _searchParty = cards;

                        _that.appendEffect(
                                new PassthruEffect(_that) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        final ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
                                        Filter filter = Filters.in(_searchParty);

                                        // Cards participating in search party cannot move, search again, or battle for remainder of turn
                                        modifiersEnvironment.addUntilEndOfTurnModifier(
                                                new MayNotMoveModifier(null, filter));
                                        modifiersEnvironment.addUntilEndOfTurnModifier(
                                                new MayNotJoinSearchPartyModifier(null, filter));
                                        modifiersEnvironment.addUntilEndOfTurnModifier(
                                                new MayNotParticipateInBattleModifier(null, filter));

                                        _that.appendEffect(
                                                new DrawDestinyEffect(_that, playerId, 1, DestinyType.SEARCH_PARTY_DESTINY) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                            return;
                                                        }

                                                        gameState.sendMessage("Search party destiny total: " + GuiUtils.formatAsString(totalDestiny));
                                                        if (totalDestiny > 5) {
                                                            gameState.sendMessage("Result: Succeeded");

                                                            Collection<PhysicalCard> missingCharacters = Filters.filterActive(game, null,
                                                                    SpotOverride.INCLUDE_MISSING_AND_UNDERCOVER, Filters.and(Filters.owner(playerId), Filters.missing, Filters.character, Filters.at(_site)));
                                                            if (!missingCharacters.isEmpty()) {
                                                                PhysicalCard foundCharacter = GameUtils.getRandomCards(missingCharacters, 1).get(0);
                                                                _that.appendEffect(
                                                                        new FindMissingCharacterEffect(_that, foundCharacter, true));

                                                                // Card found by search party cannot move, search again, or battle for remainder of turn
                                                                modifiersEnvironment.addUntilEndOfTurnModifier(
                                                                        new MayNotMoveModifier(null, foundCharacter));
                                                                modifiersEnvironment.addUntilEndOfTurnModifier(
                                                                        new MayNotJoinSearchPartyModifier(null, foundCharacter));
                                                                modifiersEnvironment.addUntilEndOfTurnModifier(
                                                                        new MayNotParticipateInBattleModifier(null, foundCharacter));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
    }

    @Override
    public PhysicalCard getActionSource() {
        return null;
    }

    @Override
    public String getText() {
        return "Form search party";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_searchPartyStarted) {
                _searchPartyStarted = true;
                game.getGameState().activatedCard(_playerId, _site);
                game.getGameState().beginSearchParty(_searchParty, _site);
                game.getGameState().sendMessage(_playerId + " forms search party of " + GameUtils.getAppendedNames(_searchParty));
                game.getGameState().cardAffectsCards(getPerformingPlayer(), _site, _searchParty);
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        game.getGameState().finishSearchParty();
        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _searchPartyStarted;
    }
}
