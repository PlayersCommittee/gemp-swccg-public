package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Aratech Corporation
 */
public class Card8_117 extends AbstractNormalEffect {
    public Card8_117() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Aratech Corporation", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Aratech Corporation sent support staff to various Imperial outposts and garrisons. Gave advanced briefings and training to biker scout personnel.");
        setGameText("Deploy on Carida system. Your speeder bikes piloted by biker scouts are power +1 and forfeit +1 and may follow (for free) an opponent's vehicle or character that just moved from same site (if within range). (Immune to Alter while you occupy Carida.)");
        addIcons(Icon.ENDOR);
        addKeyword(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Carida_system;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, new OccupiesCondition(playerId, Filters.Carida_system), Title.Alter));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.speeder_bike, Filters.hasPiloting(self, Filters.biker_scout));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, filter, 1));
        modifiers.add(new ForfeitModifier(self, filter, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter speederBikeFilter = Filters.and(Filters.your(self), Filters.speeder_bike, Filters.hasPiloting(self, Filters.biker_scout));

        // Check condition(s)
        if (TriggerConditions.movedFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.vehicle)), Filters.sameSiteAs(self, speederBikeFilter))) {

            MovedResult movedResult = (MovedResult) effectResult;
            final PhysicalCard fromLocation = movedResult.getMovedFrom();
            final Filter toLocation = Filters.sameLocation(movedResult.getMovedTo());
            Filter movableFilter = Filters.and(speederBikeFilter, Filters.at(fromLocation),
                    Filters.movableAsRegularMove(playerId, true, 0, false, toLocation));
            if (GameConditions.canTarget(game, self, movableFilter)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setRepeatableTrigger(true);
                action.setText("Have speeder bike follow");

                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose speeder bike", movableFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard speederBike) {
                                action.addAnimationGroup(speederBike);
                                action.setActionMsg("Have " + GameUtils.getCardLink(speederBike) + " move to follow opponent's card");
                                // Perform result(s)
                                action.appendEffect(
                                        new MoveCardAsRegularMoveEffect(action, playerId, speederBike, true, false, toLocation));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}