package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 9
 * Type: Objective
 * Title: They Have No Idea We're Coming / Until We Win, Or The Chances Are Spent
 */
public class Card209_029 extends AbstractObjective {
    public Card209_029() {
        super(Side.LIGHT, 0, Title.They_Have_No_Idea_Were_Coming);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Scarif system, Data Vault (with Stardust there), and Massassi War Room. " +
                "For remainder of game, Baze, Chirrut, and Rebel troopers are spies. You may not deploy Taking Them With Us or Jedi. " +
                "While this side up, once per turn, may [download] Rogue One, a corvette, or a Scarif site. " +
                "Flip this card if you control two Scarif locations.");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_9);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Scarif_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Scarif system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.DataVault, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Data Vault to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Stardust, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Stardust to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Massassi_War_Room, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Massassi War Room to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {

        // Baze, Chirrut and Rebel Troopers are spies
        Filter bazeOrChirrutFilter = Filters.or(Filters.Chirrut, Filters.Baze);
        Filter rebelTroopersFilter = Filters.and(Filters.Rebel, Filters.trooper);
        Filter bazeChirrutAndRebelTroopersFilter = Filters.or(bazeOrChirrutFilter, rebelTroopersFilter);

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);

        // Cannot deploy Jedi
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.or(Filters.Jedi, Filters.title(Title.Taking_Them_With_Us)), playerId), null));

        // Baze, Chirrut and Rebel Troopers are Spies
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new KeywordModifier(self, bazeChirrutAndRebelTroopersFilter, Keyword.SPY), null));

        return action;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.THEY_HAVE_NO_IDEA_WERE_COMING__DOWNLOAD_SITE_OR_STARSHIP;

        // Check condition(s)

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter starshipOrSite = Filters.or(Filters.Rogue_One, Filters.corvette, Filters.Scarif_site);

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy starship or location from Reserve Deck");
            action.setActionMsg("Deploy Rogue One, a corvette, or a Scarif site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, starshipOrSite, true));

            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s) - Flip if you control two Scarif locations
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controls(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Scarif_location))
        {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}