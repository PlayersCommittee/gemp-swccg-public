package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Admiral's Order
 * Title: No Questions Asked
 */
public class Card10_014 extends AbstractAdmiralsOrder {
    public Card10_014() {
        super(Side.LIGHT, "No Questions Asked");
        setGameText("Force generation at sectors a player occupies is +1 for that player. Any [independent] starship with an alien pilot character aboard is immune to attrition <4 (or adds 2 to immunity). During your control phase, one of your smugglers at a site related to a system occupied by your freighter or [independent] starship may either fire a blaster or make a regular move using personal landspeed.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter sectorsOccupiedByPlayer = Filters.and(Filters.sector, Filters.occupies(playerId));
        Filter sectorsOccupiedByOpponent = Filters.and(Filters.sector, Filters.occupies(opponent));
        Filter independentStarshipWithAlienPilotCharacterAboard = Filters.and(Icon.INDEPENDENT, Filters.starship, Filters.hasAboard(self, Filters.and(Filters.character, Filters.alien, Filters.pilot)));
        Filter alreadyHasImmunity = Filters.alreadyHasImmunityToAttrition(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(independentStarshipWithAlienPilotCharacterAboard, Filters.not(alreadyHasImmunity)), 4));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(independentStarshipWithAlienPilotCharacterAboard, alreadyHasImmunity), 2));
        modifiers.add(new ForceGenerationModifier(self, sectorsOccupiedByPlayer, 1, playerId));
        modifiers.add(new ForceGenerationModifier(self, sectorsOccupiedByOpponent, 1, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter smugglerFilter = Filters.and(Filters.your(playerId), Filters.smuggler, Filters.at(Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupiesWith(playerId, self, Filters.or(Filters.freighter, Filters.and(Icon.INDEPENDENT, Filters.starship)))))));
        Filter blasterFilter = Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.blaster, Filters.attachedTo(smugglerFilter)),
                Filters.and(smugglerFilter, Filters.hasPermanentBlaster)), Filters.canBeFiredAt(self, Filters.canBeTargetedBy(self), 0));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)) {
            Collection<PhysicalCard> smugglersWhoCanUseLandspeed = Filters.filterActive(game, self, Filters.and(smugglerFilter, Filters.hasNotPerformedRegularMove, Filters.canMoveUsingLandspeed(playerId, false, false, false, 0)));
            if (!smugglersWhoCanUseLandspeed.isEmpty()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Move smuggler using personal landspeed");
                action.appendUsage(
                        new OncePerTurnEffect(action)
                );
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose smuggler to move", Filters.in(smugglersWhoCanUseLandspeed)) {
                            @Override
                            protected void cardSelected(PhysicalCard smuggler) {
                                action.addAnimationGroup(smuggler);
                                action.setActionMsg("Move " + GameUtils.getCardLink(smuggler) + " using personal landspeed.");
                                // Perform result(s)
                                action.appendEffect(
                                        new MoveCardAsRegularMoveEffect(action, playerId, smuggler, false, false, Filters.any));
                            }
                        }
                );
                actions.add(action);
            }

            Collection<PhysicalCard> blasters = Filters.filterActive(game, self, blasterFilter);
            if (!blasters.isEmpty()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Fire a blaster");
                action.appendUsage(
                        new OncePerTurnEffect(action)
                );
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose weapon to fire", blasterFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard weapon) {
                                action.addAnimationGroup(weapon);
                                action.setActionMsg("Fire " + GameUtils.getCardLink(weapon));
                                action.appendEffect(
                                        new FireWeaponEffect(action, weapon, false, Filters.canBeTargetedBy(self))
                                );
                            }
                        }
                );
                actions.add(action);

            }
        }
        return actions;
    }
}