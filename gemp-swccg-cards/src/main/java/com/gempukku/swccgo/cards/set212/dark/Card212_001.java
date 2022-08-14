package com.gempukku.swccgo.cards.set212.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
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
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 12
 * Type: Effect
 * Title: Evil Is Everywhere
 */
public class Card212_001 extends AbstractNormalEffect {
    public Card212_001() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Evil_Is_Everywhere, Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on table. Non-[Episode I] Dark Jedi are lost. Unless with Obi-Wan, Dooku is immune to attrition. Your game text on Jedi Council Chamber is canceled. Once per turn, may [download] a mobile hallway or [Episode I] lightsaber. [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_12);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.EVIL_IS_EVERYWHERE_DOWNLOAD_LIGHTSABER_OR_HALLWAY;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter ep1Lightsaber = Filters.and(Icon.EPISODE_I, Filters.lightsaber);
            Filter mobileHallway = Filters.and(Filters.mobile_site, Filters.titleContains("Hallway"));

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a mobile hallway or [Episode I] lightsaber from Reserve Deck");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(ep1Lightsaber, mobileHallway), true));

            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        modifiers.add(new ImmuneToAttritionModifier(self, Filters.and(Filters.Dooku, Filters.not(Filters.with(self, Filters.ObiWan)))));
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Jedi_Council_Chamber, self.getOwner()));

        return modifiers;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        // Non-[Episode I] Dark Jedi are lost.
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> nonEP1DarkJedi = Filters.filterActive(game, self, Filters.and(Filters.Dark_Jedi, Filters.not(Icon.EPISODE_I)));
            if (!nonEP1DarkJedi.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make non-[Episode I] Dark Jedi lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(nonEP1DarkJedi) + " lost");

                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, nonEP1DarkJedi));
                actions.add(action);
            }
        }
        return actions;
    }
}
