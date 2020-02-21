package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.PresentWhereAffectedCardIsAtEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Effect
 * Title: They Will Be No Match For You (V) (Errata)
 */
public class Card501_020 extends AbstractNormalEffect {
    public Card501_020() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.They_Will_Be_No_Match_For_You, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'At last we will reveal ourselves to the Jedi.'");
        setGameText("Deploy on table. [Permanent Weapon] Maul is lost. Once per turn, may deploy Maul's Lightsaber from Reserve Deck; reshuffle (or lose 1 Force to deploy it from Lost Pile). Sith characters deploy -1 and, while alone, are power +1 for each opponent's character present. [Immune to Alter.]");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.VIRTUAL_SET_9);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("They Will Be No Match For You (V) (Errata)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.Sith, -1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Sith, Filters.alone), new PresentWhereAffectedCardIsAtEvaluator(self, Filters.and(Filters.opponents(self), Filters.character))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> permWeaponMaul = Filters.filterActive(game, self, Filters.and(Filters.Maul, Icon.PERMANENT_WEAPON));
            if (!permWeaponMaul.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make [Permanent Weapon] Maul lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(permWeaponMaul) + " lost");

                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, permWeaponMaul));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.THEY_WILL_BE_NO_MATCH_FOR_YOU__DOWNLOAD_MAULS_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.MAULS_DOUBLE_BLADED_LIGHTSABER)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Maul's Lightsaber from Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Mauls_Lightsaber, true));
                actions.add(action);
            }

            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.MAULS_DOUBLE_BLADED_LIGHTSABER)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Maul's Lightsaber from Lost Pile");
                action.setActionMsg("Deploy Maul's Lightsaber from Lost Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new LoseForceEffect(action, playerId, 1, true));
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Mauls_Lightsaber, false));
                actions.add(action);
            }
        }
        return actions;
    }
}