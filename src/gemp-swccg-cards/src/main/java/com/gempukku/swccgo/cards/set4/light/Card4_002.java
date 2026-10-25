package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToDagobahLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateAttacksAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.TotalTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda
 */
public class Card4_002 extends AbstractJediMaster {
    public Card4_002() {
        super(Side.LIGHT, 1, 5, 2, 7, 9, "Yoda", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Help you I can, yes. For 800 years have I trained Jedi. Judge me by my size do you? Mm? And well you should not! For my ally is the Force... and a powerful ally it is.");
        setGameText("Deploy on Dagobah I must, but move elsewhere I may, yes. When the mentor, 1 to training destiny I add. Where present am I, battles and attacks happen not unless present a Dark Side character of ability > 3 there is. To attrition, immune am I.");
        addIcons(Icon.DAGOBAH);
        addPersona(Persona.YODA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Dagobah;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToDagobahLocationModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition unlessDarkSideCharacterOfAbilityMoreThanThreePresent = new UnlessCondition(new PresentCondition(self,
                Filters.and(Filters.Dark_Side, Filters.character, Filters.abilityMoreThan(3))));
        final int yodaPermId = self.getPermanentCardId();
        Condition gimerStickIgnoresRestriction = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard yoda = gameState.findCardByPermanentId(yodaPermId);
                return yoda != null && modifiersQuerying.hasGameTextModification(gameState, yoda,
                        ModifyGameTextType.YODAS_GIMER_STICK__IGNORE_DAGOBAH_YODA_BATTLE_AND_ATTACK_RESTRICTIONS);
            }
        };
        Filter wherePresent = Filters.wherePresent(self);
        Condition yodaBattleRestriction = new AndCondition(unlessDarkSideCharacterOfAbilityMoreThanThreePresent, new UnlessCondition(gimerStickIgnoresRestriction));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalTrainingDestinyModifier(self, Filters.jediTestTargetingMentor(Filters.sameCardId(self)), 1));
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, wherePresent, yodaBattleRestriction));
        modifiers.add(new MayNotInitiateAttacksAtLocationModifier(self, wherePresent, yodaBattleRestriction));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }
}
