package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Return Of A Jedi (V)
 */
public class Card217_046 extends AbstractUsedOrLostInterrupt {
    public Card217_046() {
        super(Side.LIGHT, 3, Title.Return_Of_A_Jedi, Uniqueness.UNRESTRICTED, ExpansionSet.SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Where did you dig up that old fossil?' 'I don't think he exists anymore.' 'Surely he must be dead by now.' 'I can't believe he's gone.' 'Oh, he's not dead, not yet.' Obi's back!");
        setGameText("Unless a Jedi Master 'communing': USED: [upload] Obi-Wan's Hut, Obi-Wan's Journal or a card with 'mentor' in title. LOST: During battle, if non-[Episode I] Obi-Wan with a Dark Jedi (or 'communing'), add one battle destiny.");
        addIcon(Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.RETURN_OF_A_JEDI__UPLOAD_CARD_WITH_OBIWAN_IN_TITLE;

        // Check condition(s)
        if (!game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.Jedi_Master)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take a card into hand from Reserve Deck");
            action.setActionMsg("Take Obi-Wan's Hut, Obi-Wan's Journal, or a card with 'mentor' in title into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.title(Title.ObiWans_Hut), Filters.ObiWans_Journal,
                                            Filters.titleContains("mentor"), Filters.titleContains("mentors")), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (!game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.Jedi_Master)
                && GameConditions.isDuringBattle(game)
                && (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.not(Icon.EPISODE_I), Filters.ObiWan, Filters.with(self, Filters.Dark_Jedi)))
                || game.getModifiersQuerying().isCommuning(game.getGameState(), Filters.and(Filters.not(Icon.EPISODE_I), Filters.ObiWan)))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}