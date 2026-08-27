package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used
 * Title: Sabotage
 */
public class Card2_056 extends AbstractUsedInterrupt {
	public Card2_056() {
		super(Side.LIGHT, 5, Title.Sabotage, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U1);
		setLore("Poorly equipped Rebels must rely on clandestine strikes against the massive Imperial military. Computer 'slicing' and system tampering have proven most effective.");
		setGameText("During your control phase, target one weapon, device or vehicle at same site as your Undercover spy. Draw destiny. If destiny > target's deploy cost, target is lost (may be stolen instead if spy also a thief). OR Cancel Informant.");
		addIcons(Icon.A_NEW_HOPE);
	}

	@Override
	protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
		List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

		final Filter yourUndercoverSpy = Filters.and(Filters.your(self), Filters.undercover_spy);
		Filter targetFilter = Filters.and(Filters.or(Filters.weapon, Filters.device, Filters.vehicle),
				Filters.at(Filters.sameSiteAs(self, SpotOverride.INCLUDE_UNDERCOVER, yourUndercoverSpy)));
		TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

		// Check condition(s)
		if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
				&& GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, yourUndercoverSpy)
				&& GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

			final PlayInterruptAction action = new PlayInterruptAction(game, self);
			action.setText("Target weapon, device or vehicle");
			// Choose target(s)
			action.appendTargeting(
					new TargetCardOnTableEffect(action, playerId, "Target weapon, device or vehicle", targetingReason, targetFilter) {
						@Override
						protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
							action.addAnimationGroup(targetedCard);
							// Allow response(s)
							action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
									new RespondablePlayCardEffect(action) {
										@Override
										protected void performActionResults(Action targetingAction) {
											// Get the targeted card(s) from the action using the targetGroupId.
											// This needs to be done in case the target(s) were changed during the responses.
											final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

											// Perform result(s)
											action.appendEffect(
													new DrawDestinyEffect(action, playerId) {
														@Override
														protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
															GameState gameState = game.getGameState();
															if (totalDestiny == null) {
																gameState.sendMessage("Result: Failed due to failed destiny draw");
																return;
															}

															float deployCost = getOnTableDeployCost(game, self, finalTarget);
															gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
															gameState.sendMessage("Deploy cost: " + GuiUtils.formatAsString(deployCost));

															if (totalDestiny > deployCost) {
																gameState.sendMessage("Result: Succeeded");
																PhysicalCard thiefSpy = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_UNDERCOVER,
																		Filters.and(Filters.your(self), Filters.undercover_spy, Filters.thief,
																				Filters.atSameSite(finalTarget), Filters.canStealAndCarry(finalTarget)));
																if (thiefSpy != null) {
																	final PhysicalCard spyToAttachTo = thiefSpy;
																	action.appendEffect(
																			new PlayoutDecisionEffect(action, playerId,
																					new YesNoDecision("Do you want to steal " + GameUtils.getCardLink(finalTarget) + " instead of making it lost?") {
																						@Override
																						protected void yes() {
																							action.appendEffect(
																									new StealCardAndAttachFromTableEffect(action, finalTarget, spyToAttachTo));
																						}
																						@Override
																						protected void no() {
																							action.appendEffect(
																									new LoseCardFromTableEffect(action, finalTarget));
																						}
																					}
																			)
																	);
																}
																else {
																	action.appendEffect(
																			new LoseCardFromTableEffect(action, finalTarget));
																}
															}
															else {
																gameState.sendMessage("Result: Failed");
															}
														}
													}
											);
										}
									}
							);
						}
					}
			);
			actions.add(action);
		}

		// Check condition(s)
		if (GameConditions.canTargetToCancel(game, self, Filters.title("Informant"))) {

			final PlayInterruptAction action = new PlayInterruptAction(game, self);
			// Build action using common utility
			CancelCardActionBuilder.buildCancelCardAction(action, Filters.title("Informant"), "Informant");
			actions.add(action);
		}
		return actions;
	}

	@Override
	protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
		// Check condition(s)
		if (TriggerConditions.isPlayingCard(game, effect, Filters.title("Informant"))
				&& GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

			final PlayInterruptAction action = new PlayInterruptAction(game, self);
			// Build action using common utility
			CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
			return Collections.singletonList(action);
		}
		return null;
	}

	/**
	 * On-table deploy cost for a weapon/device/vehicle already in play.
	 * Passes the current attachedTo as costTarget so X (e.g. Dark Jedi Lightsaber)
	 * and free-to-target (e.g. Chewbacca's Bowcaster) resolve the same way transfer does.
	 */
	static float getOnTableDeployCost(SwccgGame game, PhysicalCard self, PhysicalCard target) {
		PhysicalCard costTarget = target.getAttachedTo();
		return game.getModifiersQuerying().getDeployCost(game.getGameState(), self, target, costTarget,
				false, null, false, 0, null, false);
	}
}
