package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
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
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Location
 * Subtype: System
 * Title: Geonosis
 */
public class Card227_009_BACK extends AbstractSystem {
    public Card227_009_BACK() {
        super(Side.DARK, Title.Geonosis, 7, ExpansionSet.SET_27, Rarity.V);
        setLocationDarkSideGameText("If you just initiated a Force drain (or won a battle) at a Geonosis site, 'conquer' (flip) Geonosis.");
        setLocationLightSideGameText("While you occupy with a [Republic] starship, your Jedi and clones at Geonosis sites are power +1.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CLONE_ARMY, Icon.EPISODE_I, Icon.PLANET, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.canBeFlipped(game, self)
            && (TriggerConditions.forceDrainInitiatedBy(game, effectResult,  playerOnDarkSideOfLocation, Filters.Geonosis_site)
            || TriggerConditions.wonBattleAt(game, effectResult, playerOnDarkSideOfLocation, Filters.Geonosis_site))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Conquer Geonosis");
            action.appendEffect(new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.Jedi, Filters.clone), Filters.at(Filters.Geonosis_site)), new OccupiesWithCondition(playerOnLightSideOfLocation, self, Filters.Republic_starship), 1));
        return modifiers;
    }
}
