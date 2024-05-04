package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByPermanentWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleResultDeterminedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 21
 * Type: Character
 * Subtype: Sith
 * Title: Asajj Ventress
 */
public class Card221_008 extends AbstractSith {
    public Card221_008() {
        super(Side.DARK, 1, 5, 5, 5, 7, "Asajj Ventress", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("Female Dathomirian assassin.");
        setGameText("Deploys -1 if Dooku on table. Whenever a player loses a battle here, that player loses 1 Force. While armed, opponent's weapon destiny draws targeting Ventress are -1. Opponent may not cancel battles here. Immune to [Permanent Weapon] weapons and attrition < 4.");
        addPersona(Persona.VENTRESS);
        addKeywords(Keyword.FEMALE, Keyword.ASSASSIN);
        setSpecies(Species.DATHOMIRIAN);
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.SEPARATIST, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, Filters.Dooku), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.opponents(self), Filters.weapon), new ArmedWithCondition(self, Filters.weapon), Filters.any, -1, Filters.Ventress));
        modifiers.add(new MayNotCancelBattleModifier(self, Filters.here(self), null, opponent));
        modifiers.add(new MayNotBeTargetedByPermanentWeaponsModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleResultDetermined(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))) {
            BattleResultDeterminedResult result = (BattleResultDeterminedResult) effectResult;
            String loser = result.getLoser();
            if (loser != null) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + loser + " lose 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, loser, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
