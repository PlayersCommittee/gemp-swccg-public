package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Location
 * Subtype: Site
 * Title: Jakku: Starship Graveyard
 */
public class Card204_030 extends AbstractSite {
    public Card204_030() {
        super(Side.LIGHT, Title.Starship_Graveyard, Title.Jakku, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLocationDarkSideGameText("Unless you occupy, your non-scavenger characters deploy and move to here for +1 Force.");
        setLocationLightSideGameText("If you just deployed a scavenger here, may retrieve a device, droid, or weapon.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_VII, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition unlessYouOccupy = new UnlessCondition(new OccupiesCondition(playerOnDarkSideOfLocation, self));
        Filter yourNonScavengerCharacters = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.character, Filters.not(Filters.scavenger));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourNonScavengerCharacters, unlessYouOccupy, 1, self));
        modifiers.add(new MoveCostToLocationModifier(self, yourNonScavengerCharacters, unlessYouOccupy, 1, self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.JAKKU_STARSHIP_GRAVEYARD__RETRIEVE_DEVICE_DROID_OR_WEAPON;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, playerOnLightSideOfLocation, Filters.scavenger, Filters.here(self))
                && GameConditions.canSearchLostPile(game, playerOnLightSideOfLocation, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a device, droid, or weapon");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerOnLightSideOfLocation, Filters.or(Filters.device, Filters.droid, Filters.weapon)));
            return Collections.singletonList(action);
        }
        return null;
    }
}