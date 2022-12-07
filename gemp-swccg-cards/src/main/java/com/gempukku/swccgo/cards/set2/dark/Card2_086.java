package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CommencePrimaryIgnitionTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Imperial
 * Title: Death Star Gunner
 */
public class Card2_086 extends AbstractImperial {
    public Card2_086() {
        super(Side.DARK, 2, 2, 1, 1, 2, Title.Death_Star_Gunner, Uniqueness.RESTRICTED_3, ExpansionSet.A_NEW_HOPE, Rarity.C1);
        setLore("Most gunners in the Imperial Navy once aspired to be TIE pilots, but lacked sufficient skills to fly starfighters. A few were assigned to the Death Star to man the main artillery.");
        setGameText("If on Death Star: Cumulatively adds 2 to total of Commence Primary Ignition. OR During your control phase, may send this gunner to your Used Pile and take Superlaser into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.GUNNER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CommencePrimaryIgnitionTotalModifier(self, new OnCondition(self, Title.Death_Star), 2, true));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DEATH_STAR_GUNNER__UPLOAD_SUPERLASER;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.isOnSystem(game, self, Title.Death_Star)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Superlaser into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Superlaser, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
