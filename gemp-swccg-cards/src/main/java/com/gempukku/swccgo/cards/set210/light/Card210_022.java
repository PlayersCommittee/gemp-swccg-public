package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.*;

/*
 * Set: Set 10
 * Type: Character
 * Subtype: Alien
 * Title: Paploo (V)
 */
public class Card210_022 extends AbstractAlien {
    public Card210_022() {
        super(Side.LIGHT, 3, 2, 2, 2, 2, Title.Paploo, Uniqueness.UNIQUE);
        setLore("Ewok scout. Son of Warok. Brave, adventurous thief. Stole an Imperial speeder bike to create a diversion. 'Not bad for a little furball.'");
        setGameText("While on Endor, Perimeter Patrol is suspended. Once per game, if in battle with a speeder bike (or two scouts), may relocate Paploo and one opponent's character of ability < 4 present with him (unless either are ‘hit’) to an adjacent site.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.ENDOR);
        addKeywords(Keyword.SCOUT, Keyword.THIEF);
        setSpecies(Species.EWOK);
        addPersona(Persona.PAPLOO);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.Perimeter_Patrol, new OnCondition(self, Title.Endor)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        if(GameConditions.canSpot(game, self, Filters.title(Title.Paploo))) {
            final PhysicalCard paploo = Filters.findFirstActive(game, self, Filters.title(Title.Paploo));

            GameTextActionId gameTextActionId = GameTextActionId.PAPLOO__RELOCATE;
            final Filter validSitesToMoveTo = Filters.adjacentSite(self);
            int numValidSitesToMoveTo = Filters.countActive(game, self, validSitesToMoveTo);
            int numScoutsInBattle = Filters.countActive(game, self, Filters.and(Filters.scout, Filters.inBattleWith(self)));
            Filter validCharactersToMoveFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(4), Filters.presentWith(paploo), Filters.not(Filters.hit));
            Collection<PhysicalCard> validCharactersToMoveCollection = Filters.filterActive(game, self, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(4), Filters.presentWith(paploo), Filters.not(Filters.hit)));

            // Check condition(s)
            // so the conditions that need to be met are:
            // - At least one valid site to move to
            // - Two scouts or one speeder bike in battle with self
            // - Paploo isn't hit
            // - At least one valid opponent's character for you to move
            if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                    && (numValidSitesToMoveTo >= 1)
                    && ((numScoutsInBattle >= 2) || GameConditions.isInBattleWith(game, self, Filters.speeder_bike))
                    && !(paploo.isHit())
                    && !validCharactersToMoveCollection.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate characters to adjacent site");
                action.appendUsage(new OncePerGameEffect(action));
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", validCharactersToMoveFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard opponentsCharacterSelected) {
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(opponentsCharacterSelected) + " to", validSitesToMoveTo) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                action.allowResponses("Make " + GameUtils.getCardLink(paploo) + " and " + GameUtils.getCardLink(opponentsCharacterSelected) + " move to adjacent site", new RespondableEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        Collection<PhysicalCard> finalCharacters = Filters.filterActive(game, self, Filters.or(paploo, opponentsCharacterSelected));
                                                        action.appendEffect(new RelocateBetweenLocationsEffect(action, finalCharacters, siteSelected));
                                                    }
                                                });
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }
        return actions;
    }
}