package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
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
 * Title: Sai'torr Kal Fas (V)
 */
public class Card200_045 extends AbstractNormalEffect {
    public Card200_045() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Saitorr_Kal_Fas);
        setVirtualSuffix(true);
        setLore("Saurin female from planet Durkteel. Bodyguard of Hrchek, a Saurin droid trader. Sai'torr will teach battle skills to those who prove themselves worthy.");
        setGameText("Deploy on table. Once per turn, may [download] a matching weapon on your unique character present at a site. (Immune to Alter.)");
        addIcons(Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BLASTER_RACK__DOWNLOAD_MATCHING_WEAPON;

        final Collection<PhysicalCard> characters = Filters.filterActive(game, self, Filters.and(Filters.unique, Filters.character, Filters.presentAt(Filters.site)));
        Collection<PhysicalCard> matchingWeapons = new LinkedList<>();

        for (PhysicalCard character : characters) {
            Collection<PhysicalCard> matchingWeaponsForCharacter = Filters.filter(game.getGameState().getReserveDeck(playerId), game, Filters.matchingWeaponForCharacter(character));
            for (PhysicalCard weapon : matchingWeaponsForCharacter) {
                if (!matchingWeapons.contains(weapon)) {
                    matchingWeapons.add(weapon);
                }
            }
        }

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("[download] a matching weapon");
            action.setActionMsg("[download] a matching weapon");
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
                                    new DeployCardToTargetFromReserveDeckEffect(action, weapon, Filters.in(characters), false, false, true));
                        }
                    }
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}