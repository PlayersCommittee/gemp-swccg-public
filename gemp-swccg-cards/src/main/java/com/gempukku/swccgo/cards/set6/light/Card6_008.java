package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: B'omarr Monk
 */
public class Card6_008 extends AbstractAlien {
    public Card6_008() {
        super(Side.LIGHT, 2, 3, 0, 4, 4, "B'omarr Monk", Uniqueness.RESTRICTED_3);
        setLore("Members of a mysterious religious sect. Resentful of their monastery being taken over by Jabba. Shed their bodies and have their brains encased in a walking automaton.");
        setGameText("While at a site, adds Force icons to equalize them for both sides. Cancels opponent's Jabba's Palace game text where present. Can not use vehicles, starships, weapons, or devices. Participates only in defensive battles. Lost if not on Tatooine.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtTargetModifier(self, Filters.or(Filters.vehicle, Filters.starship)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceIconsEqualizedModifier(self, Filters.sameSite(self)));
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.wherePresent(self),
                new PresentAtCondition(self, Filters.Jabbas_Palace), game.getOpponent(self.getOwner())));
        modifiers.add(new MayNotUseWeaponsModifier(self));
        modifiers.add(new MayNotUseDevicesModifier(self));
        modifiers.add(new MayNotParticipateInBattleInitiatedByOwnerModifier(self));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && !GameConditions.isOnSystem(game, self, Title.Tatooine)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + "lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
