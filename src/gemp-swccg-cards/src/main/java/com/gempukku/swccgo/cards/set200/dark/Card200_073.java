package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.PerStarDestroyerEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedByModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Motti (V)
 */
public class Card200_073 extends AbstractImperial {
    public Card200_073() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Admiral Motti", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior Navy Commander of Death Star. Believes in technology. Ridiculed the Force. Ambitious leader. Promoted due to support of New Order, not military skills. Hates Vader.");
        setGameText("[Pilot] 2. Chiraneau may not modify your Force drains. Kuat Drive Yards is immune to Alter. Unless your Senate on table, unique (•) Star Destroyers are power +2 and immunity to attrition +1. Once per game, may [download] non-[Set 0] Kuat Drive Yards.");
        addPersona(Persona.MOTTI);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.LEADER, Keyword.ADMIRAL, Keyword.COMMANDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition condition = new UnlessCondition(new OnTableCondition(self, Filters.and(Filters.your(self), Filters.Galactic_Senate)));
        Filter uniqueStarDestroyers = Filters.and(Filters.unique, Filters.Star_Destroyer);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainsMayNotBeModifiedByModifier(self, Filters.Chiraneau, playerId));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Kuat_Drive_Yards, Title.Alter));
        modifiers.add(new PowerModifier(self, uniqueStarDestroyers, condition, new PerStarDestroyerEvaluator(2)));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, uniqueStarDestroyers, condition, new PerStarDestroyerEvaluator(1)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ADMIRAL_MOTTI__DOWNLOAD_KUAT_DRIVE_YARDS;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Kuat_Drive_Yards)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Kuat Drive Yards from Reserve Deck");
            action.setActionMsg("Deploy non-[Set 0] Kuat Drive Yards from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.not(Icon.VIRTUAL_SET_0), Filters.Kuat_Drive_Yards), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
