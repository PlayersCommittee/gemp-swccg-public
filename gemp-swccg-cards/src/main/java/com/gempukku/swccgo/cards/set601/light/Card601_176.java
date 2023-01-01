package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.IgnoresDeploymentRestrictionsFromCardWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsFromCardWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 5
 * Type: Effect
 * Title: We'll Take The Long Way
 */
public class Card601_176 extends AbstractNormalEffect {
    public Card601_176() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, "We'll Take The Long Way", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("Actions borne of the love of one's planet can heavily outweigh those generated from simple battle orders.");
        setGameText("Deploy on a site. Your Republic characters may deploy here regardless of Objective deployment restrictions. Once per turn, if you just deployed a Republic character to an interior Naboo site, retrieve a non-character card into hand. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsFromCardWhenDeployingToLocationModifier(self, Filters.and(Filters.your(self), Filters.Republic_character), Filters.Objective, Filters.hasAttached(self)));
        modifiers.add(new IgnoresDeploymentRestrictionsFromCardWhenDeployingToLocationModifier(self, Filters.and(Filters.your(self), Filters.Republic_character), null, self.getOwner(), Filters.Objective, Filters.hasAttached(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__WELL_TAKE_THE_LONG_WAY__RETRIEVE_CARD;

        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, playerId, Filters.Republic_character, Filters.interior_Naboo_site)
                && GameConditions.isOncePerTurn(game, self, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retreive a card into hand");
            action.setActionMsg("Retrieve a non-character card into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, Filters.not(Filters.character)));
            return Collections.singletonList(action);
        }
        return null;
    }
}