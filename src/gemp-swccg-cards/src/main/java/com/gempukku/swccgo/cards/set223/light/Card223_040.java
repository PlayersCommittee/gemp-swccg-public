package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used
 * Title: Life Day
 */
public class Card223_040 extends AbstractUsedInterrupt {
    public Card223_040() {
        super(Side.LIGHT, 4, Title.Life_Day, Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);       
        setGameText("Destiny +2 if 3 Wookiees and/or Ewoks on table. Take a Ewok or Wookiee of power < 6 into hand from reserve deck; reshuffle. OR Deploy a Bowcaster or Ewok Weapon from your lost pile. OR Add one battle destiny at same site as Part of the Tribe.");
        addIcon(Icon.VIRTUAL_SET_23);
    }

     @Override
     protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.LIFE_DAY__UPLOAD_WOOKIE_OR_EWOK;
        GameTextActionId gameTextActionId2 = GameTextActionId.LIFE_DAY__DEPLOY_WEAPON_FROM_LOST_PILE;

        //Take a Ewok or Wookiee into hand from reserve deck
        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a a Ewok or Wookiee of power < 6 into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Filters.Ewok, Filters.powerLessThan(6)), Filters.and(Filters.Wookiee, Filters.powerLessThan(6))), true));
                        }
                    }
            );
            actions.add(action);
        }

        //Deploy a Ewok Weapon or Bowcaster from lost pile
        // Check condition(s)
        if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId2))   {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId2);
            action.setText("Deploy card from Lost Pile");
            action.setActionMsg("Deploy a Bowcaster or Ewok Weapon from your lost pile.");
            // Allow response(s)
            action.allowResponses("Deploy a Bowcaster or Ewok Weapon from your lost pile.",
                new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromLostPileEffect(action, Filters.or(Filters.bowcaster, Filters.Ewok_weapon), true));                                    
                        }
                }  
            );
            actions.add(action);     
        }

        //Add one battle destiny with Part of the Tribe
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Part_of_the_Tribe, Filters.at(Filters.site)))) {
            if (GameConditions.canAddBattleDestinyDraws(game, self)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                            }
                        }
                );
                actions.add(action);
            }
        }
 
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, self, new OnTableCondition(self, 3, Filters.or(Filters.Wookiee, Filters.Ewok)), 2));  

        return modifiers;
    }

}