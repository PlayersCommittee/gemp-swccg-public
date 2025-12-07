package com.gempukku.swccgo.cards.set226.light;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

/**
 * Set: Set 26
 * Type: Location
 * Subtype: Site
 * Title: Mapuzo: Safehouse
 */
public class Card226_022 extends AbstractSite {
    public Card226_022() {
        super(Side.LIGHT, Title.Safehouse, Title.Mapuzo, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLocationLightSideGameText("The first Jedi you deploy here each turn is immune to Imperial Barrier.");
        setLocationDarkSideGameText("Unless Third Sister here, Force drain -1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_26);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        
        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, playerOnLightSideOfLocation, Filters.Jedi, Filters.here(self))
                && GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)) {
            
            final PhysicalCard playedJedi = ((PlayCardResult) effectResult).getPlayedCard();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make " + GameUtils.getCardLink(playedJedi) + " immune to Imperial Barrier");
            action.setPerformingPlayer(playerOnLightSideOfLocation);

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new ImmuneToTitleModifier(self, playedJedi, Title.Imperial_Barrier),
                            "Immune to " + Title.Imperial_Barrier)
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Condition UnlessThirdSisterHere = new UnlessCondition(new HereCondition(self, Filters.Third_Sister));

        modifiers.add(new ForceDrainModifier(self, UnlessThirdSisterHere, -1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}
