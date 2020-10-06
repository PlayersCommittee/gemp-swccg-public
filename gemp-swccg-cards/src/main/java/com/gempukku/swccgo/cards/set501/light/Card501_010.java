package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForceRetrievalFromCardModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: You've Got A Lot Of Guts Coming Here (V)
 */
public class Card501_010 extends AbstractNormalEffect {
    public Card501_010() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "You've Got A Lot Of Guts Coming Here", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("The Empire, Lando Calrissian, Jabba the Hutt. For Han Solo, it can be very hard to tell when your past is going to catch up with you.");
        setGameText("Text: Deploy on table. Once per game during your turn, may relocate Effect to Audience Chamber or your system. If Han or Lando here, opponent retrieves no Force from Scum And Villainy. Opponent's aliens and [Independent] starships are deploy +1 and power -1 here. [Immune to Alter.]");
        addIcons(Icon.CORUSCANT, Icon.VIRTUAL_SET_13);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("You've Got A Lot Of Guts Coming Here (V)");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter audienceChamberOrYourSystem = Filters.or(Filters.Audience_Chamber, Filters.and(Filters.your(playerId), Filters.system), Filters.canRelocateEffectTo(playerId, self));

        GameTextActionId gameTextActionId = GameTextActionId.YOUVE_GOT_A_LOT_OF_GUTS_COMING_HERE__RELOCATE;
        if (GameConditions.canSpot(game, self, audienceChamberOrYourSystem)
                && GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate this Effect");
            action.setActionMsg("Relocate this Effect");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose location", audienceChamberOrYourSystem) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(targetedCard),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AttachCardFromTableEffect(action, self, targetedCard)
                                            );
                                        }
                                    }
                            );

                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter here = Filters.hasAttached(self);
        Filter opponentsAliensAndIndShips = Filters.and(Filters.opponents(self.getOwner()), Filters.or(Filters.alien, Filters.and(Filters.starship, Filters.icon(Icon.INDEPENDENT))));
        HereCondition hanOrLandoHereCondition = new HereCondition(self, Filters.or(Filters.Han, Filters.Lando));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, opponentsAliensAndIndShips, 1, here));
        modifiers.add(new PowerModifier(self, Filters.and(opponentsAliensAndIndShips, here), -1));
        modifiers.add(new ResetForceRetrievalFromCardModifier(self, Filters.Scum_And_Villainy,
                hanOrLandoHereCondition, 0, opponent));
        return modifiers;
    }
}