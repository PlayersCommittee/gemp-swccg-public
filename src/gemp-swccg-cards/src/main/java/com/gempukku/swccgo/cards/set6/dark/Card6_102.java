package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: EV-9D9
 */
public class Card6_102 extends AbstractDroid {
    public Card6_102() {
        super(Side.DARK, 1, 3, 3, 2, "EV-9D9", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("MerenData EV supervisor droid. Particularly enjoys torturing power droids. A clumsy designer nearly broke this unit before it was shipped. She formerly worked at Cloud City.");
        setGameText("During your control phase, may search Reserve Deck, take one power droid or Torture into hand and reshuffle. May Force drain at Droid Workshop, Droid Junkheap or Incinerator (+1 for each other droid present, including captive droids).");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.FEMALE);
        addModelType(ModelType.SUPERVISOR);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.EV9D9__UPLOAD_POWER_DROID_OR_TORTURE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a power droid or Torture into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.power_droid, Filters.Torture), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atWorkshopJunkheapOrIncinerator = new AtCondition(self, Filters.or(Filters.Droid_Workshop, Filters.Droid_Junkheap, Filters.Incinerator));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayForceDrainModifier(self, atWorkshopJunkheapOrIncinerator));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), atWorkshopJunkheapOrIncinerator,
                new PresentEvaluator(self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.other(self), Filters.droid)),
                self.getOwner()));
        return modifiers;
    }
}
