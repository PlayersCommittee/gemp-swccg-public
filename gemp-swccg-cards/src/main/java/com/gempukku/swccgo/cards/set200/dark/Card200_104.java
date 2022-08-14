package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Blaster Rack (V)
 */
public class Card200_104 extends AbstractNormalEffect {
    public Card200_104() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Blaster_Rack);
        setVirtualSuffix(true);
        setLore("Imperial facilities like the Death Star and garrison bases have blaster racks at key locations to equip soldiers with weapons like blaster rifles and thermal detonators.");
        setGameText("Deploy on table. Once per turn, may [download] a matching weapon on your unique character present at a site. (Immune to Alter.)");
        addIcons(Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BLASTER_RACK__DOWNLOAD_MATCHING_WEAPON;

        Filter uniqueCharactersPresentAtSites = Filters.and(Filters.your(playerId), Filters.unique, Filters.character, Filters.presentAt(Filters.site));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, uniqueCharactersPresentAtSites)) {
            final Collection<PhysicalCard> characters = Filters.filterActive(game, self, uniqueCharactersPresentAtSites);
            final Collection<PhysicalCard> matchingWeapons = new LinkedList<>();
            final Collection<PhysicalCard> matchingCharacters = new LinkedList<>();
            for (PhysicalCard character : characters) {
                Collection<PhysicalCard> matchingWeaponsForCharacter = Filters.filter(game.getGameState().getReserveDeck(playerId), game, Filters.and(Filters.matchingWeaponForCharacter(character), Filters.deployable(self, null, false, 0)));
                if(!matchingWeaponsForCharacter.isEmpty()){
                    matchingCharacters.add(character);
                    for(PhysicalCard weapon: matchingWeaponsForCharacter){
                        if(!matchingWeapons.contains(weapon)){
                            matchingWeapons.add(weapon);
                        }
                    }
                }
            }

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("[Download] a matching weapon");
            action.setActionMsg("[Download] a matching weapon");
            action.appendTargeting(
                    new ChooseCardFromReserveDeckEffect(action, playerId, Filters.in(matchingWeapons)) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard weapon) {
                            action.setText("Deploy " + GameUtils.getCardLink(weapon));
                            action.setActionMsg("Deploy " + GameUtils.getCardLink(weapon));
                            // Update usage limit(s)
                            action.appendUsage(
                                    new OncePerPhaseEffect(action));
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToTargetFromReserveDeckEffect(action, weapon, Filters.in(matchingCharacters), false, false, true));
                        }
                    }
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}