package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ExchangeCardInHandWithTopCardOfLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitToForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Ketwol
 */
public class Card7_025 extends AbstractAlien {
    public Card7_025() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, Title.Ketwol, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Pacithhip scout. From an unknown system on the Outer Rim. Spends most of his time talking to pilots and travelers at local docking bays.");
        setGameText("Adds 2 to power of anything he pilots. Once per turn, may exchange a docking bay from hand with top card of Lost Pile. Your docking bay transit is free when moving to or from same site.");
        setSpecies(Species.PACITHHIP);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter sameSite = Filters.sameSite(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, sameSite, playerId));
        modifiers.add(new DockingBayTransitToForFreeModifier(self, sameSite, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final GameTextActionId oncePerGameActionId = GameTextActionId.KETWOL__EXCHANGE_DOCKING_BAY;
        final GameTextActionId oncePerTurnActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.hasInHand(game, playerId, Filters.docking_bay)
                && GameConditions.hasLostPile(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, oncePerTurnActionId)) {
            boolean isOncePerGame = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.KETWOL__MAY_EXCHANGE_DOCKING_BAY_ONCE_PER_GAME);
            if (!isOncePerGame || GameConditions.isOncePerGame(game, self, oncePerGameActionId)) {
                final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, isOncePerGame ? oncePerGameActionId : oncePerTurnActionId);
                action.setText("Exchange docking bay with top card of Lost Pile");
                action.setActionMsg("Exchange a docking bay in hand with top card of Lost Pile");
                // Update usage limit(s)
                if (isOncePerGame) {
                    action.appendUsage(
                            new OncePerGameEffect(action));
                    action.appendUsage(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    modifiersQuerying.getUntilEndOfTurnLimitCounter(self, playerId, gameTextSourceCardId, oncePerTurnActionId).incrementToLimit(1, 1);
                                }
                            }
                    );
                }
                else {
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    action.appendUsage(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    modifiersQuerying.getUntilEndOfGameLimitCounter(self.getTitle(), oncePerGameActionId).incrementToLimit(1, 1);
                                }
                            }
                    );
                }
                // Perform result(s)
                action.appendEffect(
                        new ExchangeCardInHandWithTopCardOfLostPileEffect(action, playerId, Filters.docking_bay));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
