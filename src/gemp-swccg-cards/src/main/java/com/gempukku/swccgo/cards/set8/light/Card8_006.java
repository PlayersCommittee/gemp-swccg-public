package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.effects.ChangePlayedInterruptSubtypeEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.game.state.actions.PlayCardState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Corporal Janse
 */
public class Card8_006 extends AbstractRebel {
    public Card8_006() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Corporal Janse", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLore("Sharpshooter from Ukio. Recently joined the Alliance. Former BlasTech employee who evaluated new weapon designs. Served as a scout on hunting expeditions.");
        setGameText("When firing a character weapon, adds 1 to each weapon destiny draw. If Janse just targeted with an A280 Sharpshooter Rifle at a character at an adjacent site and is using Sorry About The Mess, it is a Used Interrupt.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
        addPersona(Persona.JANSE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.character_weapon, Filters.any));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.character, Filters.at(Filters.adjacentSite(self))), Filters.A280_Sharpshooter_Rifle)) {
            WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

            if (weaponFiringState != null
                    && weaponFiringState.getCardFiringWeapon() != null
                    && Filters.persona(Persona.JANSE).accepts(game, weaponFiringState.getCardFiringWeapon())) {

                PlayCardState playCardState = game.getGameState().getTopPlayCardState(self);
                if (playCardState != null
                        && Filters.Sorry_About_The_Mess.accepts(game, playCardState.getPlayCardAction().getPlayedCard())) {
                    PlayInterruptAction playInterruptAction = (PlayInterruptAction) playCardState.getPlayCardAction();

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Make "+ GameUtils.getCardLink(playCardState.getPlayCardAction().getPlayedCard()) + " a Used Interrupt");
                    action.appendEffect(
                            new ChangePlayedInterruptSubtypeEffect(action, playInterruptAction, CardSubtype.USED));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
