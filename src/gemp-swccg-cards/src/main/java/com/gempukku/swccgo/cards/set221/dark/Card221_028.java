package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDefenseValueModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.MovedUsingLandspeedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Royal Escort (V)
 */
public class Card221_028 extends AbstractNormalEffect {
    public Card221_028() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Royal Escort", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("When away from the Imperial Palace on Coruscant, the Emperor is protected by legions of troops. Typically this force includes soldiers trained to fight in the local environment.");
        setGameText("Deploy on table. Your scouts piloting speeder bikes are defense value = 5 and immune to Clash Of Sabers. Once per game, your speeder bike piloted by a scout may follow an opponent's character that just moved from same site. [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter scoutsPilotingSpeederBikesFilter = Filters.and(Filters.your(self), Filters.scout, Filters.piloting(Filters.speeder_bike));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ResetDefenseValueModifier(self, scoutsPilotingSpeederBikesFilter, 5));
        modifiers.add(new ImmuneToTitleModifier(self, scoutsPilotingSpeederBikesFilter, Title.Clash_Of_Sabers));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ROYAL_ESCORT_V__FOLLOW;

        Filter speederBikeFilter = Filters.and(Filters.your(self), Filters.speeder_bike, Filters.hasPiloting(self, Filters.scout));

        // Check condition(s)
        if (TriggerConditions.movedFromOrThroughLocationToLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.sameSiteAs(self, speederBikeFilter), Filters.location)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            MovedResult movedResult = (MovedResult) effectResult;
            if (movedResult.isMoveComplete()) {
                final List<PhysicalCard> fromLocations = (effectResult.getType()==EffectResult.Type.MOVED_USING_LANDSPEED? ((MovedUsingLandspeedResult) movedResult).getLocationsAlongPath() : Collections.singletonList(movedResult.getMovedFrom()));
                final Filter toLocation = Filters.sameLocation(movedResult.getMovedTo());
                Filter movableFilter = Filters.and(speederBikeFilter, Filters.at(Filters.in(fromLocations)),
                        Filters.movableAsRegularMove(playerId, false, 0, false, toLocation));
                if (GameConditions.canTarget(game, self, movableFilter)) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Have speeder bike follow character");

                    action.appendUsage(
                            new OncePerGameEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose speeder bike", movableFilter) {
                                @Override
                                protected void cardSelected(final PhysicalCard speederBike) {
                                    action.addAnimationGroup(speederBike);
                                    action.setActionMsg("Have " + GameUtils.getCardLink(speederBike) + " move to follow character");
                                    // Perform result(s)
                                    action.appendEffect(
                                            new MoveCardAsRegularMoveEffect(action, playerId, speederBike, false, false, toLocation));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}