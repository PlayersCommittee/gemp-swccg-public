package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Starship
 * Subtype: Capital
 * Title: Jabba's Space Cruiser (V)
 */

public class Card210_034 extends AbstractCapitalStarship {
    public Card210_034() {
        super(Side.DARK, 2, 4, 5, 5, null, 4, 6, Title.Jabbas_Space_Cruiser, Uniqueness.UNIQUE);
        setLore("Flying fortress of Jabba Desilijic Tiure. Reaches speeds of 800 kph in atmosphere. The crime lord installed hidden gunports as an unpleasant surprise for would-be pirates.");
        setGameText("May add 1 alien pilot and 6 passengers. Permanent pilot provides ability of 2. When deployed, you may [download] an alien leader aboard for free. Immune to attrition < 5.");
        addIcons(Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_10, Icon.SPECIAL_EDITION);
        addModelType(ModelType.UBRIKKIAN_LUXURY_SPACE_YACHT);
        addKeywords(Keyword.CRUISER);
        setPilotCapacity(1);
        setPassengerCapacity(6);
        setVirtualSuffix(true);
    }

    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        // Previous version was "true", this one does not have that text, so changing it to false, but leaving this
        //  section here so it'll be easy to find what needs to be changed if there's a rules clarification later.
        return false;
    }

    // Must be alien pilot
    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    // one permanent pilot of ability 2
    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    // Immune to < 5
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    // May download alien leader
    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersEvenIfUnpiloted(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.JABBAS_SPACE_CRUISER__DOWNLOAD_ALIEN_LEADER;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy an alien leader from Reserve Deck");
            action.setActionMsg("Deploy an alien leader aboard " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardAboardFromReserveDeckEffect(action, Filters.alien_leader, Filters.sameCardId(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}