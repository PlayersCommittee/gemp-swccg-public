package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Droid
 * Title: CZ-4
 */
public class Card6_100 extends AbstractDroid {
    public Card6_100() {
        super(Side.DARK, 4, 2, 1, 3, "CZ-4", Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Very common communications droid. Some have been modified to be defense drones. Programmed to warn their masters of an imminent attack.");
        setGameText("Opponent may not 'react' to or from same site. You may 'react' to a battle or Force drain at same or adjacent Jabba's Palace site by deploying (at normal use of the Force) one non-unique alien to that site from Reserve Deck; reshuffle.");
        addModelType(ModelType.COMMUNICATIONS);
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.sameSite(self), opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, Filters.sameSite(self), opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.CZ_4__DOWNLOAD_NON_UNIQUE_ALIEN_AS_REACT;
        final Filter validSites = Filters.and(Filters.sameOrAdjacentSite(self), Filters.Jabbas_Palace_site, Filters.or(Filters.battleLocation, Filters.forceDrainLocation));

        // Check condition(s)
        if((TriggerConditions.battleInitiatedAt(game, effectResult, opponent, validSites)
                || TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, validSites))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy non-unique alien from Reserve Deck");
            action.setActionMsg("Deploy non-unique alien as a 'react' from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.non_unique, Filters.alien), validSites, false, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
