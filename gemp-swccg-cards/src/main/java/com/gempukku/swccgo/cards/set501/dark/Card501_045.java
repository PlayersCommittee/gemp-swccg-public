package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Dryden Vos
 */
public class Card501_045 extends AbstractAlien {
    public Card501_045() {
        super(Side.DARK, 1, 6, 4, 3, 6, "Dryden Vos", Uniqueness.UNIQUE);
        setLore("Crimson Dawn leader. Gangster.");
        setGameText("Deploys -2 while Maul on table. When deployed, may deploy a weapon here from Reserve Deck; reshuffle. Once during your control phase, may use 2 Force to fire Kyuzo Petars. Immune to attrition < 5 (< 3 if Qi’ra here).");
        addIcons(Icon.WARRIOR, Icon.PILOT, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.CRIMSON_DAWN, Keyword.LEADER, Keyword.GANGSTER);
        setArmor(5);
        addPersona(Persona.VOS);
        setTestingText("Dryden Vos");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, Filters.or(Filters.Maul)), -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(5, 3, new WithCondition(self, Filters.persona(Persona.QIRA)))));
        return modifiers;
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DRYDEN__DOWNLOAD_WEAPON;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy weapon from Reserve Deck");
            action.setActionMsg("Deploy a weapon here from Reserve Deck");

            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.weapon, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter weaponFilter = Filters.and(Filters.title(Title.Kyuzo_Petars), Filters.attachedTo(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 2)
                && Filters.canUseWeapon(self).accepts(game, self)
                && GameConditions.canSpot(game, self, weaponFilter)) {

            PhysicalCard weapon = Filters.findFirstActive(game, self, weaponFilter);
            if (weapon == null) {
                return null;
            }
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Fire Kyuzo Petars");

            // Update usage limit(s)
            action.appendUsage(new OncePerPhaseEffect(action));

            // Pay Costs
            action.appendCost(new UseForceEffect(action, playerId, 2));

            // Perform results
            action.appendEffect(new FireWeaponEffect(action, weapon, false, Filters.any));

            return Collections.singletonList(action);
        }
        return null;
    }
}
