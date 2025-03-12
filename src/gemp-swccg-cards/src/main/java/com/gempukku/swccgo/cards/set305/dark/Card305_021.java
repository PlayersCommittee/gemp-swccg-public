package com.gempukku.swccgo.cards.set305.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterSith;
import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Dark Jedi Master/Sith
 * Title: Darth Pravus
 */
public class Card305_021 extends AbstractDarkJediMasterSith {
    public Card305_021() {
        super(Side.DARK, 6, 9, 6, 7, 10, "Darth Pravus", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.UR);
        setLore("Grand Master and leader of the Brotherhood, Darth Pravus is a controversial leader shrouded in mystery. He was responsible for restoring clans to the Brotherhood.");
        setGameText("Deploys -4 to Arx. [Pilot] 1. May use two weapons. Unless opponent's character of ability > 3 here, opponent's total ability here = 0. Once per turn, you may take Force Lightning into hand from Reserve Deck; reshuffle. May be targeted by Force Lightning. Immune to attrition.");
        addPersona(Persona.PRAVUS);
        addIcon(Icon.WARRIOR, 2);
        addIcons(Icon.PILOT, Icon.SITH);
        addKeywords(Keyword.GRAND_MASTER, Keyword.DARK_COUNCILOR, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -4, Filters.Deploys_at_Arx));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new MayBeTargetedByModifier(self, Title.Force_Lightning));
        modifiers.add(new ResetTotalAbilityModifier(self, Filters.here(self),
                new UnlessCondition(new HereCondition(self, Filters.and(Filters.opponents(self), Filters.character,
                        Filters.abilityMoreThan(3)))), 0, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DARTH_PRAVUS__UPLOAD_FORCE_LIGHTNING;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Force Lightning into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Force_Lightning, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
