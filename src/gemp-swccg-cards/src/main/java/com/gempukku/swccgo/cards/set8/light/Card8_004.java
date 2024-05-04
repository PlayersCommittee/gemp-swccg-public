package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Corporal Beezer
 */
public class Card8_004 extends AbstractRebel {
    public Card8_004() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Corporal Beezer", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLore("Alderaanian slicer and technician. Trained by Brooks Carlson to serve as a Scout for Alliance commandos. She gets nervous when General Solo tries to hotwire something.");
        setGameText("Prevents opponent from 'reacting' to same site. Once during each of your deploy phases, may deploy one device to same location from Reserve Deck; reshuffle. Rebels of ability < 3 and [Endor] Rebels are immune to Scanning Crew.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.sameSite(self), game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.Rebel, Filters.or(Filters.abilityLessThan(3), Icon.ENDOR)), Title.Scanning_Crew));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CORPORAL_BEEZER__DOWNLOAD_DEVICE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a device from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.device, Filters.sameLocation(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
