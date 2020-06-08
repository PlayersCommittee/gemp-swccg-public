package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Card211_042 extends AbstractSite {
    public Card211_042() {
        super(Side.LIGHT, "Kamino: Clone Birthing Center", "Kamino");
        setLocationDarkSideGameText("If you initiate battle here, add one battle destiny.  Fetts may move to and from here for free");
        setLocationLightSideGameText("During your deploy phase, your clone here may move to a battleground you occupy for free.");
        // Aglets: "Treat it as saying 'your clone here may move to any site you occupy' when coding."
        addIcon(Icon.DARK_FORCE, 0);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.EPISODE_I, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        Filter otherSiteYouOccupy = Filters.and(Filters.other(self), Filters.site, Filters.occupies(playerOnLightSideOfLocation));

        if (GameConditions.canSpotLocation(game, otherSiteYouOccupy)
            && GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, Phase.DEPLOY)
            && GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.clone, self, otherSiteYouOccupy, true))
        {
            MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.clone, self, otherSiteYouOccupy, true);
            action.setText("Move a clone");
            action.appendUsage(new OncePerPhaseEffect(action));
            actions.add(action);
            return actions;
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerOnDarkSideOfLocation, self)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add one battle destiny");
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1, playerOnDarkSideOfLocation));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter here = Filters.here(self);
        Filter fetts = Filters.Fett;

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MovesFreeToLocationModifier(self, fetts, here));
        modifiers.add(new MovesFreeFromLocationModifier(self, fetts, here));
        return modifiers;
    }
}
