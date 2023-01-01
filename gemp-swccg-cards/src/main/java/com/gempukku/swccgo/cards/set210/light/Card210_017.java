package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Effect
 * Title: Jedi Business
 */
public class Card210_017 extends AbstractNormalEffect {
    public Card210_017() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Jedi Business", Uniqueness.UNIQUE, ExpansionSet.SET_10, Rarity.V);
        setLore("The peacekeepers of the galaxy are not to be taken lightly.");
        setGameText("Deploy on table. Non-[Episode I] Jedi are lost. Qui-Gon is deploy -1. Unless with Anakin or Vader, Mace is immune to attrition. Once per turn, may choose: [download] an [Episode I] lightsaber or once per game, [download] Malastare, Mos Espa, or Night Club. [Immune to Alter.]");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_SET_10, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Using the same GameTextActionId for both actions since they are mutually exclusive per turn.
        GameTextActionId gameTextActionId = GameTextActionId.JEDI_BUSINESS__DOWNLOAD_CARD;

        // Once per turn, may choose: \/ an [Episode I] lightsaber or once per game, \/ Malastare, Mos Espa, or Night Club.

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            // Once per turn, \/ an [Episode I] lightsaber
            Filter ep1Lightsaber = Filters.and(Icon.EPISODE_I, Filters.lightsaber);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a lightsaber from Reserve Deck");
            action.setActionMsg("Deploy an [Episode I] lightsaber from Reserve Deck");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, ep1Lightsaber, true));

            actions.add(action);
        }


        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            // Once per game, \/ Malastare, Mos Espa, or Night Club.
            Filter validLocations = Filters.or(Filters.Malastare, Filters.Mos_Espa, Filters.Nightclub);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy location from Reserve Deck");
            action.setActionMsg("Deploy Malastare, Mos Espa, or Night Club from Reserve Deck");

            // Update usage limit(s)
            // Note:  This case is a little unique because this action counts
            // towards Once-per-game AND Once-per-turn limits
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, validLocations, true));
            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter withVader = Filters.with(self, Filters.Vader);
        Filter withAnakin = Filters.with(self, Filters.Anakin);
        Filter withAnakinOrVader = Filters.or(withVader, withAnakin);
        Filter notWithAnakinOrVader = Filters.not(withAnakinOrVader);
        Filter maceNotWithAnakinOrVader = Filters.and(Filters.Mace, notWithAnakinOrVader);

        // Qui-Gon is deploy -1
        modifiers.add(new DeployCostModifier(self, Filters.QuiGon, -1));

        // Mace is immune to attrition when not with Vader or Anakin
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.Mace, new OnTableCondition(self, maceNotWithAnakinOrVader)));

        return modifiers;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        // Non-[Episode I] Jedi are lost.
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> nonEP1Jedi = Filters.filterActive(game, self, Filters.and(Filters.Jedi, Filters.not(Icon.EPISODE_I)));
            if (!nonEP1Jedi.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make non-[Episode I] Jedi lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(nonEP1Jedi) + " lost");

                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, nonEP1Jedi));
                actions.add(action);
            }
        }
        return actions;
    }
}