package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtScompLink;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11
 * Type: Device
 * Title: Cyborg Construct
 */
public class Card211_054 extends AbstractCharacterDevice {
    public Card211_054() {
        super(Side.LIGHT, 4, Title.Cyborg_Construct, Uniqueness.UNIQUE);
        setLore("Biotech's latest model, the Aj ^ g, boasts greater storage capacity than all other models combined. Advertised as, \"Artificial Intelligence worth shaving your head for.\"");
        setGameText("Deploy on an alien of ability < 3. While at a Scomp link, Computer Interface is a Used Interrupt, and once per turn may use 1 Force to [upload] Computer Interface. Once per turn, if on Lobot, may [download] a trooper here.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_11);
        setVirtualSuffix(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.alien, Filters.abilityLessThan(3));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.alien, Filters.abilityLessThan(3));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new UsedInterruptModifier(self, Filters.title(Title.Computer_Interface), new PresentAtScompLink(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId uploadComputerInterface = GameTextActionId.CYBORG_CONSTRUCT_UPLOAD_COMPUTER_INTERFACE;
        if(GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, uploadComputerInterface)
                && GameConditions.isAtScompLink(game, self)
                && GameConditions.hasReserveDeck(game, playerId)){
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, uploadComputerInterface);

            action.setText("Upload Computer Interface.");
            action.setActionMsg("[upload] Computer Interface.");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new UseForceEffect(action, playerId, 1)
            );
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Computer_Interface), true)
            );

            actions.add(action);
        }

        GameTextActionId downloadTrooper = GameTextActionId.CYBORG_CONSTRUCT_DOWNLOAD_TROOPER;
        if(GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, downloadTrooper)
                && GameConditions.isPhaseForPlayer(game, Phase.DEPLOY, playerId)
                && GameConditions.isAttachedTo(game, self, Filters.Lobot)
                && GameConditions.hasReserveDeck(game, playerId)){
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, downloadTrooper);

            action.setText("Download a trooper.");
            action.setActionMsg("Download a trooper.");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action,Filters.trooper, Filters.sameLocation(self), true)
            );

            actions.add(action);
        }

        return actions;
    }
}