package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: The Dead Speak!
 */
public class Card501_065 extends AbstractNormalEffect {
    public Card501_065() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Dead Speak!", Uniqueness.UNIQUE);
        setGameText("Deploy on table if I Will Finish What You Started on table. Your [E1] characters are lost. Once per turn, may deploy Sith Throne from Reserve Deck; reshuffle. While Emperor on Exegol, your total attrition is +2 at same location as Rey, Kylo, or Pryde. [Immune to Alter.] [Episode VII]");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_13);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("The Dead Speak!");
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.I_Will_Finish_What_You_Started);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AttritionModifier(self, Filters.and(Filters.sameLocationAs(self, Filters.or(Filters.Rey, Filters.Kylo, Filters.Pryde))), new OnCondition(self, Filters.Emperor, Title.Exegol), 2, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> yourEP1Characters = Filters.filterActive(game, self, Filters.and(Filters.your(self.getOwner()), Filters.character, Icon.EPISODE_I));
            if (!yourEP1Characters.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make [Episode I] characters lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(yourEP1Characters) + " lost");

                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, yourEP1Characters));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_DEAD_SPEAK__DOWNLOAD_SITH_THRONE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Sith Throne from Reserve Deck");
            action.setActionMsg("Deploy Sith Throne from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Exegol_Sith_Throne), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}