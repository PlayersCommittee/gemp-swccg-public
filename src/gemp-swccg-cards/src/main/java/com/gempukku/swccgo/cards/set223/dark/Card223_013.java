package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Character
 * Subtype: Alien
 * Title: Gor Koresh
 */
public class Card223_013 extends AbstractAlien {
    public Card223_013() {
        super(Side.DARK, 3, 3, 3, 2, 5, "Gor Koresh", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Abyssin gambler and information broker");
        setGameText("Your mercenaries are power +1 here. Opponent may not cancel your battle destiny draws where you have a gangster or non-[Maintenance] bounty hunter. When deployed, may deploy a mercenary here from Reserve Deck; reshuffle.");
        addKeywords(Keyword.GAMBLER, Keyword.INFORMATION_BROKER);
        setSpecies(Species.ABYSSIN);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_23);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter affectFilter = Filters.and(Filters.your(self), Keyword.MERCENARY, Filters.atSameLocation(self));
        modifiers.add(new PowerModifier(self, affectFilter, 1));

        Filter nonMHunter = Filters.and(Filters.not(Filters.icon(Icon.MAINTENANCE)), Filters.bounty_hunter);
        Filter drainFilter = Filters.and(Filters.your(self), Filters.or(Filters.gangster, nonMHunter));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.sameLocationAs(self, drainFilter), playerId, opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GOR_KARESH__DOWNLOAD_GAMORREAN;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a mercenary ");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.characteristic(Keyword.MERCENARY), Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}