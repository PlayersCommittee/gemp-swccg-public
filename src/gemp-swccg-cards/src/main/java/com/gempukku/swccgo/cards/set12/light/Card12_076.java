package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Jedi Council Chamber
 */
public class Card12_076 extends AbstractSite {
    public Card12_076() {
        super(Side.LIGHT, Title.Jedi_Council_Chamber, Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLocationDarkSideGameText("Non-unique lightsaber Force drain bonuses everywhere are canceled.");
        setLocationLightSideGameText("[Episode I] Yoda may deploy here from Reserve Deck; reshuffle. Immune to Revolution.");
        addIcon(Icon.LIGHT_FORCE, 3);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelForceDrainBonusesFromCardModifier(self, Filters.and(Filters.non_unique, Filters.lightsaber), new NotCondition(new GameTextModificationCondition(self, ModifyGameTextType.LEGACY__CORUSCANT_ICON_SITES__MAY_NOT_CANCEL_FORCE_DRAIN_BONUSES))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.JEDI_COUNCIL_CHAMBER__DOWNLOAD_YODA;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId, Persona.YODA)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Yoda from Reserve Deck");
            action.setActionMsg("Deploy [Episode I] Yoda from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Icon.EPISODE_I, Filters.Yoda), Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }
}