package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, Rebel Scout
 */
public class Card10_010 extends AbstractRebel {
    public Card10_010() {
        super(Side.LIGHT, 1, 7, 6, 6, 8, "Luke Skywalker, Rebel Scout", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("Resourceful scout and member of the Rebel infiltration team assembled to destroy the shield generator. Surrendered to his father so that he would not endanger the mission.");
        setGameText("May cancel immunity to attrition of any vehicle here. If escorted by Vader and Take Your Father's Place on table, during your move phase may relocate Vader (with Luke) to Death Star II: Throne Room. Immune to Always Thinking With Your Stomach and attrition < 5.");
        addPersona(Persona.LUKE);
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR, Icon.DEATH_STAR_II);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Always_Thinking_With_Your_Stomach));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
    	Filter vehicleHereFilter = Filters.and(Filters.vehicle, Filters.here(self), Filters.hasAnyImmunityToAttrition);
    	if (GameConditions.canTarget(game, self, vehicleHereFilter)) {

    		final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
    		action.setText("Cancel immunity to attrition of a vehicle");
    		// Choose target(s)
    		action.appendTargeting(
    				new TargetCardOnTableEffect(action, playerId, "Choose vehicle", vehicleHereFilter) {
    					@Override
    					protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
    						action.addAnimationGroup(cardTargeted);
    						// Allow response(s)
    						action.allowResponses("Cancel " + GameUtils.getCardLink(cardTargeted) + "'s immunity to attrition",
    								new UnrespondableEffect(action) {
    							@Override
    							protected void performActionResults(Action targetingAction) {
    								// Perform result(s)
    								action.appendEffect(
    										new AddUntilEndOfGameModifierEffect (action,
    												new CancelImmunityToAttritionModifier(self, cardTargeted),
    												"Cancels " + GameUtils.getCardLink(cardTargeted) + "'s immunity to attrition")
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
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsWhenInactiveInPlay(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
    	if(!GameConditions.isOnlyCaptured(game, self))
    		return null;
    	
    	
    	GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_DEFAULT;
    	
    	final PhysicalCard escortedBy = Filters.findFirstActive(game, self, Filters.and(Filters.Vader,Filters.escort,Filters.escorting(self)));
    	final PhysicalCard dsIIThroneRoom = Filters.findFirstFromTopLocationsOnTable(game, Filters.title("Death Star II: Throne Room"));
    	
    	
    	if(escortedBy!=null
    			&& dsIIThroneRoom!=null
    			&& GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
    			&& GameConditions.canSpot(game, self, Filters.Take_Your_Fathers_Place)
    			&& GameConditions.canSpot(game, self, Filters.title("Death Star II: Throne Room"))
    			&& GameConditions.canTarget(game, self, escortedBy)
    			&& Filters.canBeRelocatedToLocation(dsIIThroneRoom, false, true, true, 0, false).accepts(game, escortedBy)
    			&& Filters.canBeRelocatedToLocation(dsIIThroneRoom, false, true, true, 0, false).accepts(game, self)
    			) {

    		final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
    		action.setText("Relocate Vader and Luke to Throne Room");
            action.allowResponses("Relocate " + GameUtils.getCardLink(escortedBy) + " to " + GameUtils.getCardLink(dsIIThroneRoom),
                    new UnrespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RelocateBetweenLocationsEffect(action, escortedBy, dsIIThroneRoom));
                        }
                    }
            );
   		
    		return Collections.singletonList(action);
    	}
    	return null;
    }
}
