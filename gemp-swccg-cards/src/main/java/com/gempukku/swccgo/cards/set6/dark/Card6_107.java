package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayPlayToCancelCardModifier;
import com.gempukku.swccgo.logic.modifiers.MayPlayToCancelForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Herat
 */
public class Card6_107 extends AbstractAlien {
    public Card6_107() {
        super(Side.DARK, 3, 3, 1, 3, 2, "Herat", Uniqueness.UNIQUE);
        setLore("Female Jawa shaman. Advised Wittin during his plotting with Jabba. Agreed to fan the Hutt in return for his cooperation to help usurp Kalit.");
        setGameText("Deploys only on Tatooine. During your control phase, may search Reserve Deck, take one Utinni! or Jawa into hand and reshuffle. If you have 3 or more Jawas on table, may play Utinni! to cancel a Force drain at a Tatooine site or to cancel Control.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.JAWA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HERAT__UPLOAD_UTINNI_OR_JAWA;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Utinni! or Jawa into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Utinni, Filters.Jawa), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition threeOrMoreJawasOnTable = new OnTableCondition(self, 3, Filters.and(Filters.your(self), Filters.Jawa));
        Filter yourUtinni = Filters.and(Filters.your(self), Filters.Utinni);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayPlayToCancelForceDrainModifier(self, yourUtinni, threeOrMoreJawasOnTable, Filters.Tatooine_site));
        modifiers.add(new MayPlayToCancelCardModifier(self, yourUtinni, threeOrMoreJawasOnTable, Filters.Control));
        return modifiers;
    }
}
