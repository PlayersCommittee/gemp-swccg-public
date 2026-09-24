package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyForfeitEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireRepeatedlyAtSameTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Lieutenant Greeve
 */
public class Card8_018 extends AbstractRebel {
    public Card8_018() {
        super(Side.LIGHT, 3, 2, 3, 2, 4, "Lieutenant Greeve", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Scout. Famous guide who was recruited for Madine's commando unit from the forests of Kashyyyk. Sharpshooter who uses one of the BlasTech rifles brought by Corporal Janse.");
        setGameText("Adds 1 to each of his character weapon destiny draws (characters he 'hits' are forfeit -3). When firing an A280 Sharpshooter Rifle, may fire repeatedly at same target for 2 Force each time.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.character_weapon));
        // A280 attached to Greeve may fire repeatedly at the same target for 2 Force each time.
        modifiers.add(new MayFireRepeatedlyAtSameTargetModifier(self,
                Filters.and(Filters.A280_Sharpshooter_Rifle, Filters.attachedTo(self)), 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Characters he 'hits' (with a character weapon) are forfeit -3.
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.character_weapon, self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardHit) + " forfeit -3");
            action.setActionMsg("Make " + GameUtils.getCardLink(cardHit) + " forfeit -3");
            action.appendEffect(
                    new ModifyForfeitEffect(action, cardHit, -3));
            return Collections.singletonList(action);
        }
        return null;
    }
}
