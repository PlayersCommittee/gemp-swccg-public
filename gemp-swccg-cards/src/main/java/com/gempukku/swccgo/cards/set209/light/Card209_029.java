package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
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
import com.gempukku.swccgo.logic.modifiers.*;
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
        super(Side.LIGHT, 0, Title.They_Have_No_Idea_Were_coming);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Scarif system, Data Vault (with Stardust there), and Massassi War Room.{For} remainder of game, you may not deploy Jedi. Baze, Chirrut, and Rebel troopers are spies.{While} this side up, once per turn, may deploy a Rebel starship (except Home One or [Reflections III] Falcon) or a Scarif site from Reserve Deck; reshuffle. Each player's characters (except Imperials and spies) and vehicles deploy +2 to Scarif sites that player does not occupy.");
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
                        return "Choose Scarif Data Vault to deploy";
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
                        new MayNotDeployModifier(self, Filters.Jedi, playerId), null));

        // Baze, Chirrut and Rebel Troopers are Spies
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new KeywordModifier(self, bazeChirrutAndRebelTroopersFilter, Keyword.SPY), null));

        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        String player = self.getOwner();


        // Each player's characters (except Imperials and spies) and vehicles deploy +2 to Scarif sites that player does not occupy
        Filter characterOrVehicle = Filters.or(Filters.character, Filters.vehicle);
        Filter notImperialNotSpy = Filters.and(Filters.not(Filters.Imperial), Filters.not(Filters.spy));
        Filter affectedCards = Filters.and(characterOrVehicle, notImperialNotSpy);

        Filter yourAffectedCards = Filters.and(Filters.your(player), affectedCards);
        Filter opponentsAffectedCards = Filters.and(Filters.opponents(self), affectedCards);

        Filter notOccupiedByPlayer = Filters.not(Filters.occupies(player));
        Filter notOccupiedByOpponent = Filters.not(Filters.occupies(game.getOpponent(self.getOwner())));

        Filter scarifSite = Filters.and(Filters.Scarif_location, Filters.site);


        // Note:  Since this 'looks' like two separate modifiers coming from the same card, the system thinks it needs
        //        to enforce a cumulative rule. Rather than tinkering with that code and potentially breaking a lot of
        //        things, we can work around this issue by setting these modifiers to be cumulative. Note that this
        //        will have absolutely no game-effect since you can't have more than one of the same objective on table
        //        and therefore setting 'cumulative = true' just works around the bug nicely.
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourAffectedCards, 2, Filters.and(scarifSite, notOccupiedByPlayer), true));
        modifiers.add(new DeployCostToLocationModifier(self, opponentsAffectedCards,2, Filters.and(scarifSite, notOccupiedByOpponent), true));

        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.THEY_HAVE_NO_IDEA_WERE_COMING__DOWNLOAD_SITE_OR_STARSHIP;

        // Check condition(s) - Once per turn, deploy a Scarif location
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Scarif location from Reserve Deck");
            action.setActionMsg("Deploy a Scarif location from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Scarif_location, true));

            actions.add(action);
        }
        // Check condition(s) - Once per turn, deploy a Rebel starship (except RefIII Falcon and Home One)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter notRef3Falcon = Filters.not(Filters.and(Filters.Falcon, Icon.REFLECTIONS_III));
            Filter notHomeOne = Filters.not(Filters.Home_One);
            Filter notRef3FalconOrHomeOne = Filters.and(notRef3Falcon, notHomeOne);
            Filter rebelStarshipNotHomeOneOrRef3Falcon = Filters.and(Filters.Rebel_starship, notRef3FalconOrHomeOne);

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy rebel starship from Reserve Deck");
            action.setActionMsg("Deploy rebel starship from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, rebelStarshipNotHomeOneOrRef3Falcon, true));

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