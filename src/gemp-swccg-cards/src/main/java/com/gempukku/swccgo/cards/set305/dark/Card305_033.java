package com.gempukku.swccgo.cards.set305.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Alien
 * Title: Duke Airron Uleich
 */
public class Card305_033 extends AbstractAlien {
    public Card305_033() {
        super(Side.DARK, 1, 3, 3, 4, 6, "Duke Airron Uleich", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setLore("An exiled aristocrat and leader from Kiast, Duke Airron Uleich ordered the Vauzem occupation of Quermia. With support from an unknown benefactor he has disrupted trade in the Nilgaard Sector.");
        setGameText("Your [Presence] droids and NR-N99s are destiny +1 if drawn for battle or weapon destiny. Once per turn, may take Get Them Out Of My Sight into hand from Reserve Deck; reshuffle. Immune to attrition < X, where X = twice the number of [Presence] droids present.");
        addPersona(Persona.AIRRON);
        addIcons(Icon.ABT);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.SEPHI);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter presenceDroidsAndNRN99s = Filters.and(Filters.your(self), Filters.or(Filters.and(Icon.PRESENCE, Filters.droid), Filters.NRN99));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, presenceDroidsAndNRN99s, 1));
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, presenceDroidsAndNRN99s, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new MultiplyEvaluator(2, new PresentEvaluator(self,
                Filters.and(Icon.PRESENCE, Filters.droid)))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DUKE_AIRRON_ULEICH__UPLOAD_GET_THEM_OUT_OF_MY_SIGHT;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Get Them Out Of My Sight into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Get_Them_Out_Of_My_Sight, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
