package com.gempukku.swccgo.cards.set14.dark;

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
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Nute Gunray, Neimoidian Viceroy
 */
public class Card14_081 extends AbstractRepublic {
    public Card14_081() {
        super(Side.DARK, 1, 3, 3, 4, 6, "Nute Gunray, Neimoidian Viceroy", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("Neimoidian leader. Viceroy to the Trade Federation, but primarily under the influence of Darth Sidious. Ordered to take control of Naboo, and force the Queen to sign a treaty.");
        setGameText("Your [Presence] droids and AATs are destiny +1 if drawn for battle or weapon destiny. Once per turn, may take Take Them Away into hand from Reserve Deck; reshuffle. Immune to attrition < X, where X = twice the number of [Presence] droids present.");
        addPersona(Persona.GUNRAY);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.NEIMOIDIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter presenceDroidsAndAATs = Filters.and(Filters.your(self), Filters.or(Filters.and(Icon.PRESENCE, Filters.droid), Filters.AAT));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, presenceDroidsAndAATs, 1));
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, presenceDroidsAndAATs, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new MultiplyEvaluator(2, new PresentEvaluator(self,
                Filters.and(Icon.PRESENCE, Filters.droid)))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.NUTE_GUNRAY_NEIMOIDIAN_VICEROY__UPLOAD_TAKE_THEM_AWAY;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Take Them Away into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Take_Them_Away, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
