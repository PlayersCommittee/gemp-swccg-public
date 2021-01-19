package com.gempukku.swccgo.cards.set6.light;
import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.CapturedOnlyCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Rebel
 * Title: Princess Leia Organa
 */
public class Card6_032 extends AbstractRebel {
    public Card6_032() {
        super(Side.LIGHT, 1, 0, 3, 4, 7, "Princess Leia Organa", Uniqueness.UNIQUE);
        setLore("Captured by Jabba. Princess Leia provided a distraction for his henchbeings. Waiting for the first chance to escape. Really made Jabba's tail wiggle.");
        setGameText("* Deploys free as an escorted captive of Jabba, or a bounty hunter (you may not initiate battle there on the same turn). While a captive, opponent's unique (*) aliens at same site are forfeit -2. If released, retrieve 5 Force. Immune to attrition < 3.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
        addPersona(Persona.LEIA);
        setDeploysAsCapturedPrisoner(true);
    }
    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeModifier(self));
        return modifiers;
    }
    @Override
    protected Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        Filter deployAsEscortedCaptiveOf = Filters.or(Filters.bounty_hunter,Filters.Jabba);
        if(game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.LEIA_JABBAS_PALACE__TARGET_WARRIOR_AT_AUDIENCE_CHAMBER_INSTEAD_OF_JABBA)) {
            deployAsEscortedCaptiveOf = Filters.or(Filters.bounty_hunter,Filters.Jabba,Filters.and(Filters.warrior,Filters.at(Title.Audience_Chamber)));
        }
        return deployAsEscortedCaptiveOf;
    }
    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter deployAsEscortedCaptiveOf = Filters.or(Filters.bounty_hunter,Filters.Jabba);
        if(game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.LEIA_JABBAS_PALACE__TARGET_WARRIOR_AT_AUDIENCE_CHAMBER_INSTEAD_OF_JABBA)) {
            deployAsEscortedCaptiveOf = Filters.or(Filters.bounty_hunter,Filters.Jabba,Filters.and(Filters.warrior,Filters.at(Title.Audience_Chamber)));
        }
        return Filters.canSpotFromAllOnTable(game, deployAsEscortedCaptiveOf);
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(self), Filters.alien, Filters.unique, Filters.atSameSite(self)), new CapturedOnlyCondition(self), -2));
        return modifiers;
    }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if(TriggerConditions.released(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.appendEffect(new RetrieveForceEffect(self, action, self.getOwner(), 5));
            return Collections.singletonList(action);
        }
        return null;
    }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if(TriggerConditions.justDeployed(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            PhysicalCard cardAttachedToAtLocation = self.getCardAttachedToAtLocation();
            PhysicalCard location = cardAttachedToAtLocation.getAtLocation();
            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                    new MayNotInitiateBattleAtLocationModifier(self, location, self.getOwner()),
                    self.getOwner() + " may not initiate battle at " + GameUtils.getCardLink(location)));
            return Collections.singletonList(action);
        }
        return null;
    }
}