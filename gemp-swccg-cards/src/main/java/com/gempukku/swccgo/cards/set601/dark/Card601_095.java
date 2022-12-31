package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 4
 * Type: Starship
 * Subtype: Starfighter
 * Title: Rogue Shadow
 */
public class Card601_095 extends AbstractStarfighter {
    public Card601_095() {
        super(Side.DARK, 2, 3, 3, null, 4, 5, 6, Title.Rogue_Shadow, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("");
        setGameText("May add 2 pilots and 2 passengers. If Juno Eclipse piloting, adds one battle destiny. If it just took off, may use its hyperspeed and land. Immune to attrition < 7 if Galen on table, even while landed.");
        addIcons(Icon.DAGOBAH, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_4);
        addModelType(ModelType.TRANSPORT);
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Galen, Filters.Juno));
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsBattleDestinyModifier(self, new HasPilotingCondition(self, Filters.Juno), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OnTableCondition(self, Filters.Galen), 7));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // If it just took off, may use its hyperspeed and land.

        // Check condition(s)
        if (TriggerConditions.justTookOff(game, effectResult, self)
                && GameConditions.isAtLocation(game, self, Filters.system)
                && GameConditions.canUseForce(game, playerId, 2)) {
            //TODO this shouldn't just check "do you have 2 force" just in case the costs are modified but this is a weird mechanic

            //must be able to target another system within range and an exterior site related to that system where you are intending to land

            Collection<PhysicalCard> systemsWithinRange = Filters.filterTopLocationsOnTable(game, Filters.canMoveToUsingHyperspeed(playerId, self, false, false, 0));
            Collection<PhysicalCard> possibleSystems = new LinkedList<>();
            Collection<PhysicalCard> possibleDestinations = new LinkedList<>();
            for(PhysicalCard system: systemsWithinRange) {
                if(!game.getModifiersQuerying().isProhibitedFromTarget(game.getGameState(), self, system)) {
                    Collection<PhysicalCard> sites = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.relatedSite(system), Filters.exterior_site));
                    boolean addSystem = false;
                    for (PhysicalCard site : sites) {
                        //make sure it can exist there
                        if (!game.getModifiersQuerying().isProhibitedFromTarget(game.getGameState(), self, site)) {
                            possibleDestinations.add(site);
                            addSystem = true;
                        }
                    }
                    if (addSystem)
                        possibleSystems.add(system);
                }
            }

            if (!possibleDestinations.isEmpty()) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                action.setText("Use hyperspeed and land");
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose destination", Filters.in(possibleDestinations)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.allowResponses(new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard site = action.getPrimaryTargetCard(targetGroupId);
                                PhysicalCard system = Filters.findFirstFromTopLocationsOnTable(game, Filters.relatedSystem(site));
                                action.appendEffect(new MoveCardAsRegularMoveEffect(action, playerId, self, false, true, system));
                                action.appendEffect(new MoveCardAsRegularMoveEffect(action, playerId, self, false, true, site));
                            }
                        });
                    }
                });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
