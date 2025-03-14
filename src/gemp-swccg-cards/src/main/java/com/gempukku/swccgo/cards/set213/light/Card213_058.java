package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 13
 * Type: Starship
 * Subtype: Capital
 * Title: Leia's Resistance Transport
 */
public class Card213_058 extends AbstractCapitalStarship {
    public Card213_058() {
        super(Side.LIGHT, 3, 3, 3, 4, null, 3, 5, "Leia's Resistance Transport", Uniqueness.UNIQUE, ExpansionSet.SET_13, Rarity.V);
        setGameText("May add 2 pilots and 5 passengers. Permanent pilot provides ability of 2. Deploys and moves like a starfighter. When deployed, may [download] a female Resistance character aboard (deploy -2 if a leader).");
        setPilotCapacity(2);
        setPassengerCapacity(5);
        addIcons(Icon.RESISTANCE, Icon.NAV_COMPUTER, Icon.PILOT, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_13, Icon.EPISODE_VII);
        addModelType(ModelType.RESISTANCE_TRANSPORT);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {
        });
    }

    @Override
    public boolean isDeploysLikeStarfighter() {
        return true;
    }

    @Override
    public boolean isMovesLikeStarfighter() {
        return true;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersEvenIfUnpiloted(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEIAS_RESISTANCE_TRANSPORT__DOWNLOAD_RESISTANCE_FEMALE;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Resistance female from Reserve Deck");
            action.setActionMsg("Deploy a Resistance female aboard " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                new ChooseCardFromReserveDeckEffect(action, playerId, Filters.and(Filters.female, Filters.Resistance_character)) {
                    @Override
                    protected void cardSelected(SwccgGame game, final PhysicalCard femaleResistanceCharacter) {
                        action.setText("Deploy " + GameUtils.getCardLink(femaleResistanceCharacter));
                        action.setActionMsg("Deploy " + GameUtils.getCardLink(femaleResistanceCharacter));
                        if (Filters.leader.accepts(game, femaleResistanceCharacter)) {
                            action.appendEffect(
                                new DeployCardToTargetFromReserveDeckEffect(action, femaleResistanceCharacter, Filters.sameCardId(self), false, -2, true));
                        } else {
                            action.appendEffect(
                                new DeployCardToTargetFromReserveDeckEffect(action, femaleResistanceCharacter, Filters.sameCardId(self), false, 0, true));

                        }
                    }

                }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}