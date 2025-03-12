package com.gempukku.swccgo.cards.set305.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Dark Jedi Master / Imperial
 * Title: Darth Sarin, Grand Master
 */
public class Card305_022 extends AbstractDarkJediMasterImperial {
    public Card305_022() {
        super(Side.DARK, 6, 8, 6, 7, 9, "Darth Sarin, Grand Master", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setLore("Grand Master and leader, Darth Sarin spent his life in service of the Empire in Avenger squadron and Commodore of the Grey Wolf. Recognizing the challenges in the EH he defected with the Brotherhood.");
        setGameText("Deploys -4 to Arx. [Pilot] 3. When Sarin swings a lightsaber at a Jedi, each weapon destiny draw is +2. If Sarin hits a Jedi during battle, that Jedi is power -3 for remainder of battle. Immune to Clash Of Sabers and attrition.");
        addPersona(Persona.SARIN);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GRAND_MASTER, Keyword.DARK_COUNCILOR, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -4, Filters.Deploys_at_Arx));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 2, Filters.lightsaber, Filters.Jedi));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Clash_Of_Sabers));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.Jedi, self)
                && GameConditions.isDuringBattle(game)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce " + GameUtils.getFullName(cardHit) + "'s power by 3");
            action.setActionMsg("Reduce " + GameUtils.getCardLink(cardHit) + "'s power by 3");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfBattleEffect(action, cardHit, -3));
            return Collections.singletonList(action);
        }
        return null;
    }
}
