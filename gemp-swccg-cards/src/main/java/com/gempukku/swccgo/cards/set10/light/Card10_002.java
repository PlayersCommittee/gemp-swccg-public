package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.NoAbilityInBattleCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Droid
 * Title: Artoo & Threepio
 */
public class Card10_002 extends AbstractDroid {
    public Card10_002() {
        super(Side.LIGHT, 3, 3, 2, 6, "Artoo & Threepio", Uniqueness.UNIQUE);
        addComboCardTitles("Artoo", "Threepio");
        setLore("Threepio's extensive experience 'talking' to the Falcon's computer allowed him to pilot the freighter while on Coruscant. Artoo, an R-unit droid, provided moral support.");
        setGameText("May pilot only Falcon. All droids may be battled. Any player that has no ability in a battle takes no battle damage. Once per turn, may take on card with 'Bad Feeling' in title into hand from Reserve Deck; reshuffle. Bad Feeling Have I is suspended.");
        addPersonas(Persona.R2D2, Persona.C3PO);
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.NAV_COMPUTER);
        addModelTypes(ModelType.ASTROMECH, ModelType.PROTOCOL);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayOnlyPilotModifier(self, Filters.Falcon));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeBattledModifier(self, Filters.droid));
        modifiers.add(new NoBattleDamageModifier(self, new NoAbilityInBattleCondition(playerId), playerId));
        modifiers.add(new NoBattleDamageModifier(self, new NoAbilityInBattleCondition(opponent), opponent));
        modifiers.add(new SuspendsCardModifier(self, Filters.Bad_Feeling_Have_I));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ARTOO_AND_THREEPIO__UPLOAD_BAD_FEELING;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a card with 'Bad Feeling' in title into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.titleContains("Bad Feeling"), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
