package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Starship
 * Subtype: Starfighter
 * Title: Bo-Katan's Gauntlet Starfighter
 */
public class Card224_012 extends AbstractStarfighter {
    public Card224_012() {
        super(Side.LIGHT, 3, 4, 4, null, 6, 4, 6, "Bo-Katan's Gauntlet Starfighter", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setGameText("May add 2 pilots and 4 passengers. Permanent pilot provides ability of 2. While Bo-Katan piloting, power +3 and cancels Imperial Barrier. Once per game, may [download] a Mandalorian aboard. Immune to attrition < 4.");
        addIcons(Icon.SCOMP_LINK, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_24);
        addModelType(ModelType.KOMRK_CLASS_FIGHTER_TRANSPORT);
        setPilotCapacity(2);
        setPassengerCapacity(4);
        setMatchingPilotFilter(Filters.Bo_Katan);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition boKatanPiloting = new HasPilotingCondition(self, Filters.Bo_Katan);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, boKatanPiloting, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Imperial_Barrier)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.hasPiloting(game, self, Filters.Bo_Katan)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BO_KATANS_GAUNTLET_STARFIGHTER__DOWNLOAD_MANDALORIAN;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Mandalorian aboard from Reserve Deck");
            action.setActionMsg("Deploy a Mandalorian aboard from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new DeployCardAboardFromReserveDeckEffect(action, Filters.Mandalorian, Filters.sameCardId(self), true));

            return Collections.singletonList(action);
        }

        return null;
    }
}