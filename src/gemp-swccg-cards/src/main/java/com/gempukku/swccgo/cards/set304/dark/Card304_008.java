package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.logic.modifiers.MayBeReplacedByOpponentModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * SubType: Alien
 * Title: Komilia Lap'lamiz
 */
public class Card304_008 extends AbstractAlien {
    public Card304_008() {
        super(Side.DARK, 1, 5, 5, 3, 3, Title.Komilia, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setArmor(5);
        setLore("The only daughter of Kamjin Lap'lamiz, Komilia has been spoiled rotten. She claims the title of Duchess, though her father is still alive, and generally believes she's better than you.");
        setGameText("Adds 2 to anything she pilots. When deployed, may deploy a weapon on Komilia. Blasters deploy and fire for free on Komilia. May be targeted by Thermal Detonator. Immune to attrition < 10 if with Kamjin.");
        addPersona(Persona.KOMILIA);
		addKeywords(Keyword.FEMALE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.CSP);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
		modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
		modifiers.add(new DeploysFreeToTargetModifier(self, Filters.blaster, Filters.persona(Persona.KOMILIA)));
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.blaster, Filters.attachedTo(Filters.persona(Persona.KOMILIA)))));
		modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(0, 10, new WithCondition(self, Filters.Kamjin))));
		modifiers.add(new MayBeReplacedByOpponentModifier(self, new PresentAtCondition(self, Filters.site)));
		modifiers.add(new MayUseWeaponModifier(self, Filters.Thermal_Detonator));
		modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Thermal_Detonator), self));
        return modifiers;
    }
	
	@Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KOMILIA__DOWNLOAD_WEAPON;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy weapon from Reserve Deck");
            action.setActionMsg("Deploy a weapon on Komilia from Reserve Deck");

            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.weapon, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
