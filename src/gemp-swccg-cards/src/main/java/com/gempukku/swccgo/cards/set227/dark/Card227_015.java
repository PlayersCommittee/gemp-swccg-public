package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Location
 * Subtype: System
 * Title: Utapau
 */
public class Card227_015 extends AbstractSystem {
    public Card227_015() {
        super(Side.DARK, Title.Utapau, 4, ExpansionSet.SET_27, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setLocationDarkSideGameText("If you just initiated a Force drain (or won a battle) at an Utapau site, 'conquer' (flip) Utapau.");
        setLocationLightSideGameText("If you control, Jedi deploy -1 to Utapau sites.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CLONE_ARMY, Icon.EPISODE_I, Icon.PLANET, Icon.VIRTUAL_SET_27);
        setMayNotBePlacedInReserveDeck(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.canBeFlipped(game, self)
            && (TriggerConditions.forceDrainInitiatedBy(game, effectResult,  playerOnDarkSideOfLocation, Filters.Utapau_site)
            || TriggerConditions.wonBattleAt(game, effectResult, playerOnDarkSideOfLocation, Filters.Utapau_site))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Conquer Utapau");
            action.appendEffect(new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Jedi, new ControlsCondition(playerOnLightSideOfLocation, self), -1, Filters.Utapau_site));
        return modifiers;
    }
}
