package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: Local Uprising / Liberation
 */
public class Card7_137_BACK extends AbstractObjective {
    public Card7_137_BACK() {
        super(Side.LIGHT, 7, Title.Liberation, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setGameText("While this side up, opponent's Force drains are -1. You may retrieve 1 Force whenever you deploy a matching operative to the Subjugated planet. Your matching operatives on that planet are each forfeit +2 and, when at the same site as an Imperial or AT-AT, are each power +2. When you have at least one matching operative in a battle on the Subjugated planet, you may add one battle destiny. Flip this card if you do not occupy at least two battleground sites related to the Subjugated planet.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        String planet = game.getGameState().getSubjugatedPlanet();
        if (planet != null) {
            return "Subjugated planet is " + planet;
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        String subjugatedPlanet = game.getGameState().getSubjugatedPlanet();
        Filter yourMatchingOperativesOnPlanet = Filters.and(Filters.your(self), Filters.matchingOperativeToSubjugatedPlanet, Filters.on(subjugatedPlanet));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.any, -1, opponent));
        modifiers.add(new ForfeitModifier(self, yourMatchingOperativesOnPlanet, 2));
        modifiers.add(new PowerModifier(self, Filters.and(yourMatchingOperativesOnPlanet,
                Filters.at(Filters.sameSiteAs(self, Filters.or(Filters.Imperial, Filters.AT_AT)))), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, playerId, Filters.matchingOperativeToSubjugatedPlanet, Filters.Subjugated_planet_location)) {
            final PhysicalCard cardPlayed = ((PlayCardResult) effectResult).getPlayedCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1) {
                        @Override
                        public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                            return Collections.singletonList(cardPlayed);
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String subjugatedPlanet = game.getGameState().getSubjugatedPlanet();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, Filters.on(subjugatedPlanet))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.matchingOperativeToSubjugatedPlanet))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.occupies(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.battleground_site, Filters.Subjugated_planet_location))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}