package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Blaster Rack (V) (Errata)
 */
public class Card501_021 extends AbstractNormalEffect {
    public Card501_021() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Blaster_Rack);
        setVirtualSuffix(true);
        setLore("Imperial facilities like the Death Star and garrison bases have blaster racks at key locations to equip soldiers with weapons like blaster rifles and thermal detonators.");
        setGameText("Deploy on table. Once per turn, may [download] a matching weapon on your unique character present at a site. (Immune to Alter.)");
        addIcons(Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("Blaster Rack (V) Errata");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BLASTER_RACK__DOWNLOAD_MATCHING_WEAPON;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose Character", Filters.and(Filters.unique, Filters.character)) {
                        @Override
                        protected void cardSelected(PhysicalCard character) {
                            action.setText("Deploy matching weapon on " + GameUtils.getFullName(character));
                            action.setActionMsg("Deploy matching weapon on " + GameUtils.getCardLink(character) + " from Reserve Deck");
                            // Update usage limit(s)
                            action.appendUsage(
                                    new OncePerPhaseEffect(action));
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.matchingWeaponForCharacter(character), Filters.sameCardId(character), true));
                        }
                    }
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}