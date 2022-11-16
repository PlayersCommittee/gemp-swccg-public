package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Character
 * Subtype: Droid
 * Title: Threepio
 */
public class Card8_031 extends AbstractDroid {
    public Card8_031() {
        super(Side.LIGHT, 2, 4, 1, 5, "Threepio", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Against his programming to impersonate a deity. Worshiped by the Ewoks. 'Aaaahhhhh-uuuuhhhhh. Aaaahhhhh-uuuuhhhhh. Aaaahhhhhuuuuhhhhh.'");
        setGameText("Deploys -2 on Endor. Adds 1 to attrition against opponent for each Ewok in battle at same site. If just lost from table during battle, may go to Used Pile. At same and related sites, Cloud city icon, Jabba's Palace icon, and Endor icon characters are immune to Bad Feeling Have I.");
        addIcons(Icon.ENDOR);
        addPersona(Persona.C3PO);
        addModelType(ModelType.PROTOCOL);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_on_Endor));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, new InBattleAtCondition(self, Filters.site),
                new InBattleEvaluator(self, Filters.Ewok), game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.and(Filters.or(Icon.CLOUD_CITY,
                Icon.JABBAS_PALACE, Icon.ENDOR), Filters.character), Filters.Bad_Feeling_Have_I, Filters.sameOrRelatedSite(self)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)
                && GameConditions.isDuringBattle(game)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, self, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
