package com.gempukku.swccgo.cards.set226.dark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractAlienImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Garindan, Imperial Spy
 */
public class Card226_004 extends AbstractAlienImperial {
    public Card226_004() {
        super(Side.DARK, 4, 2, 1, 1, 3, "Garindan, Imperial Spy", Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("Long-nosed, male Kubaz from Kubindi. Spy. Squealed on Obi-Wan and Luke outside Docking Bay 94. Works for Jabba the Hutt or the highest bidder. Not particularly brave.");
        setGameText("Imperials move to here for free using landspeed. If present at a site and not 'hit,' may place Garindan in Used Pile to cancel a just drawn weapon destiny targeting your other character here (or to make an Undercover spy here lost).");
        addIcons(Icon.VIRTUAL_SET_26);
        addKeywords(Keyword.SPY);
        addPersona(Persona.GARINDAN);
        setSpecies(Species.KUBAZ);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeToLocationUsingLandspeedModifier(self, Filters.Imperial, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter targetFilter = Filters.and(Filters.undercover_spy, Filters.here(self));
        TargetingReason targetReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.isPresentAt(game, self, Filters.site)
                && !GameConditions.isHit(game, self)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, targetReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Make Undercover spy lost");
            action.setActionMsg("Place Garindan in Used Pile to make an Undercover spy there lost");

            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Undercover spy", SpotOverride.INCLUDE_UNDERCOVER, targetReason, targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new PlaceCardInUsedPileFromTableEffect(action, self));
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, targetedCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.and(Filters.your(self), Filters.other(self), Filters.character, Filters.here(self)))
                && GameConditions.isPresentAt(game, self, Filters.site)
                && !GameConditions.isHit(game, self)
                && GameConditions.canCancelDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel weapon destiny");
            action.setActionMsg("Place Garindan in Used Pile to cancel a just drawn weapon destiny");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
    
}
