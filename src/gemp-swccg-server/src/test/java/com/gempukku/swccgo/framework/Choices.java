package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Decisions will always come with at least one choice, even if that single choice is "pass".  These functions will
 * let you inspect the choices available and offer shortcuts for e.g. selecting a physical card which you have
 * previously stored.
 */
public interface Choices extends Decisions {

	/**
	 * Determines whether the Dark Side player has any choices on the current decision whose description matches the
	 * provided search text.
	 * @param choice The search text the choice description must contain.
	 * @return True if the Dark Side player has an active decision with a choice description matching the given text.
	 */
	default Boolean DSChoiceAvailable(String choice) { return ChoiceAvailable(DS, choice); }
	/**
	 * Determines whether the Light Side player has any choices on the current decision whose description matches the
	 * provided search text.
	 * @param choice The search text the choice description must contain.
	 * @return True if the Light Side player has an active decision with a choice description matching the given text.
	 */
	default Boolean LSChoiceAvailable(String choice) { return ChoiceAvailable(LS, choice); }
	/**
	 * Determines whether the given player has any choices on the current decision whose description matches the
	 * provided search text.
	 * @param player The player which must have a currently active decision.
	 * @param choice The search text the choice description must contain.
	 * @return True if the given player has an active decision with a choice description matching the given text.
	 */
	default Boolean ChoiceAvailable(String player, String choice) {
		List<String> actions = GetADParamAsList(player, "results");
		if(actions == null)
			return false;
		String lowerChoice = choice.toLowerCase();
		return actions.stream().anyMatch(x -> x.toLowerCase().contains(lowerChoice));
	}

	/**
	 * Causes the Dark Side player to choose the given option.
	 * This is a catch-all that either selects the provided choice if part of a multiple choice decision, or else
	 * falls back on providing the provided choice as a top-level response to the current decision.
	 * @param choice The choice (or decision response)
	 */
	default void DSChoose(String choice) {
		if(DSGetChoiceCount() > 0) {
			DSChooseOption(choice);
		}
		else {
			PlayerDecided(DS, choice);
		}
	}

	/**
	 * Causes the Dark Side player to choose the given options.  This will automatically format the response to contain
	 * all the provided options in a comma-separated list.
	 * @param choices The choices the player wishes to make.
	 */
	default void DSChoose(String...choices) { PlayerDecided(DS, String.join(",", choices)); }
	/**
	 * Causes the Light Side player to choose the given option.
	 * This is a catch-all that either selects the provided choice if part of a multiple choice decision, or else
	 * falls back on providing the provided choice as a top-level response to the current decision.
	 * @param choice The choice (or decision response)
	 */
	default void LSChoose(String choice) {
		if(LSGetChoiceCount() > 0) {
			LSChooseOption(choice);
		}
		else {
			PlayerDecided(LS, choice);
		}
	}
	/**
	 * Causes the Light Side player to choose the given options.  This will automatically format the response to contain
	 * all the provided options in a comma-separated list.
	 * @param choices The choices the player wishes to make.
	 */
	default void LSChoose(String...choices) { PlayerDecided(LS, String.join(",", choices)); }


	/**
	 * Causes the Dark Side player to return a canned "Yes" response to a Yes or No question.
	 */
	default void DSChooseYes() { ChooseOption(DS, "Yes"); }
	/**
	 * Causes the Light Side player to return a canned "Yes" response to a Yes or No question.
	 */
	default void LSChooseYes() { ChooseOption(LS, "Yes"); }
	/**
	 * Causes the given player to return a canned "Yes" response to a Yes or No question.
	 * @param player The player to make the decision for
	 */
	default void PlayerChooseYes(String player) { ChooseOption(player, "Yes"); }
	/**
	 * Causes the Dark Side player to return a canned "No" response to a Yes or No question.
	 */
	default void DSChooseNo() { ChooseOption(DS, "No"); }
	/**
	 * Causes the Light Side player to return a canned "No" response to a Yes or No question.
	 */
	default void LSChooseNo() { ChooseOption(LS, "No"); }
	/**
	 * Causes the given player to return a canned "No" response to a Yes or No question.
	 * @param player The player to make the decision for
	 */
	default void PlayerChooseNo(String player) { ChooseOption(player, "No"); }

	/**
	 * Causes the Dark Side player to choose an option containing the given text.
	 * @param option The text to search for.
	 */
	default void DSChooseOption(String option) { ChooseOption(DS, option); }
	/**
	 * Causes the Light Side player to choose an option containing the given text.
	 * @param option The text to search for.
	 */
	default void LSChooseOption(String option) { ChooseOption(LS, option); }






	//The reason this is commented out is because I am unsure how rule timing resolution occurs in the SWCCG.
	//In LOTR, the Free Peoples player gets to pick the order in event of a tie, which is then followed here
//
//    default void DSChooseAny() {
//        if (GetChoiceCount(DSGetActionChoices()) > 0){
//            ChooseAction(DS, "actionId", DSGetActionChoices().getFirst());
//        }
//        else if(DSGetBPChoices().size() > 1) {
//            ChooseCardBPFromSelection(DS, DSGetBPChoices().getFirst());
//        }
//        else {
//            DSResolveRuleFirst();
//        }
//    }
//
//    default void DSResolveRuleFirst() { DSResolveActionOrder(GetADParamAsList(DS, "actionText").getFirst()); }
//    default void DSResolveActionOrder(String option) { ChooseAction(DS, "actionText", option); }
//

	/**
	 * @return Gets the min parameter on the current choice for the Dark Side player.  This may be a minimum
	 * number of responses, or the smallest acceptable numeric answer, depending on context.
	 */
	default int DSGetChoiceMin() { return Integer.parseInt(DSGetFirstADParam("min")); }
	/**
	 * @return Gets the max parameter on the current choice for the Dark Side player.  This may be a maximum
	 * number of responses, or the largest acceptable numeric answer, depending on context.
	 */
	default int DSGetChoiceMax() { return Integer.parseInt(DSGetFirstADParam("max")); }
	/**
	 * @return Gets the min parameter on the current choice for the Light Side player.  This may be a minimum
	 * number of responses, or the smallest acceptable numeric answer, depending on context.
	 */
	default int LSGetChoiceMin() { return Integer.parseInt(LSGetFirstADParam("min")); }
	/**
	 * @return Gets the max parameter on the current choice for the Light Side player.  This may be a maximum
	 * number of responses, or the largest acceptable numeric answer, depending on context.
	 */
	default int LSGetChoiceMax() { return Integer.parseInt(LSGetFirstADParam("max")); }

	/**
	 * @return Gets how many of the currently displayed cards are selectable for a Dark Side decision.
	 */
	default int DSGetSelectableCount() {
		return GetADParamEqualsCount(DS, "selectable", "true");
	}

	/**
	 * @return Gets how many of the currently displayed cards are selectable for a Light Side decision.
	 */
	default int LSGetSelectableCount() {
		return GetADParamEqualsCount(LS, "selectable", "true");
	}

	/**
	 * @return The blueprint choices offered to the Dark Side player (which happens in cases where you are choosing cards
	 * from the reserve deck or other cases that don't involve physical cards).
	 */
	default List<String> DSGetBPChoices() { return GetADParamAsList(DS, "blueprintId"); }
	/**
	 * @return The blueprint choices offered to the Light Side player (which happens in cases where you are choosing cards
	 * from the reserve deck or other cases that don't involve physical cards).
	 */
	default List<String> LSGetBPChoices() { return GetADParamAsList(LS, "blueprintId"); }

	/**
	 * @return The list of action IDs presented to the Dark Side player.
	 */
	default List<String> DSGetActionChoices() { return GetADParamAsList(DS, "actionId"); }
	/**
	 * @return The list of action IDs presented to the Light Side player.
	 */
	default List<String> LSGetActionChoices() { return GetADParamAsList(LS, "actionId"); }
	/**
	 * @return The list of options presented to the Dark Side player in a multiple-choice scenario.
	 */
	default List<String> DSGetMultipleChoices() { return GetADParamAsList(DS, "results"); }
	/**
	 * @return The list of options presented to the Light Side player in a multiple-choice scenario.
	 */
	default List<String> LSGetMultipleChoices() { return GetADParamAsList(LS, "results"); }
	/**
	 * @return The list of in-play cards presented to the Dark Side player in the current decision.
	 */
	default List<String> DSGetCardChoices() { return GetADParamAsList(DS, "cardId"); }
	/**
	 * @return The list of in-play cards presented to the Light Side player in the current decision.
	 */
	default List<String> LSGetCardChoices() { return GetADParamAsList(LS, "cardId"); }

	/**
	 * @return The number of choices available to the Dark Side player.
	 */
	default int DSGetChoiceCount() { return GetChoiceCount(DSGetMultipleChoices()); }
	/**
	 * @return The number of choices available to the Light Side player.
	 */
	default int LSGetChoiceCount() { return GetChoiceCount(LSGetMultipleChoices()); }

	/**
	 * Helper function used by the test rig, you don't need this in your tests.
	 * @param list List of options as provided by a call to GetADParamAsList
	 * @return The number of items in the list, 0 if it is null.
	 */
	default int GetChoiceCount(List<String> list) {
		if(list == null)
			return 0;
		return list.size();
	}


	/**
	 * Causes the Dark Side player to choose a card matching the provided physical card.
	 * @param card The card to pick.
	 */
	default void DSChooseCard(PhysicalCardImpl card) { DSChooseCards(card); }
	/**
	 * Causes the Light Side player to choose a card matching the provided physical card.
	 * @param card The card to pick.
	 */
	default void LSChooseCard(PhysicalCardImpl card) { LSChooseCards(card); }

	/**
	 * Causes the Dark Side player to choose the first available card option.  Used in cases where the choice doesn't
	 * matter for the purposes of the test.
	 */
	default void DSChooseAnyCard() { DSChoose(DSGetCardChoices().getFirst()); }
	/**
	 * Causes the Light Side player to choose the first available card option.  Used in cases where the choice doesn't
	 * matter for the purposes of the test.
	 */
	default void LSChooseAnyCard() { LSChoose(LSGetCardChoices().getFirst()); }

	/**
	 * Causes the Dark Side player to choose the given cards out of the options they have been presented with.
	 * @param cards Which cards to select
	 */
	default void DSChooseCards(PhysicalCardImpl...cards) {
		if(GetChoiceCount(DSGetBPChoices()) > 0) {
			ChooseCardBPFromSelection(DS, cards);
		}
		else {
			ChooseCards(DS, cards);
		}
	}
	/**
	 * Causes the Light Side player to choose the given cards out of the options they have been presented with.
	 * @param cards Which cards to select
	 */
	default void LSChooseCards(PhysicalCardImpl...cards) {
		if(GetChoiceCount(LSGetBPChoices()) > 0) {
			ChooseCardBPFromSelection(LS, cards);
		}
		else {
			ChooseCards(LS, cards);
		}
	}
	default void ChooseCards(String player, PhysicalCardImpl...cards) {
		String[] ids = new String[cards.length];

		for(int i = 0; i < cards.length; i++)
		{
			ids[i] = String.valueOf(cards[i].getCardId());
		}

		PlayerDecided(player, String.join(",", ids));
	}

	/**
	 * @return The number of cards being presented to the Dark Side player to choose from.
	 */
	default int DSGetCardChoiceCount() { return DSGetCardChoices().size(); }
	/**
	 * @return The number of cards being presented to the light Side player to choose from.
	 */
	default int LSGetCardChoiceCount() { return LSGetCardChoices().size(); }

	/**
	 * Causes the given player to issue a decision response composed of a comma-separated list of the provided card
	 * blueprint IDs. This will only succeed if being used to target currently out-of-play cards such as when selecting
	 * cards from the reserve deck; it will not work if being presented with a choice of in-play cards to target (such
	 * as when choosing active cards to target for a card effect).
	 * @param player The player to issue a decision for.
	 * @param cards The cards to include in the decision response.
	 */
	default void ChooseCardBPFromSelection(String player, PhysicalCardImpl...cards) {
		String[] choices = GetADParam(player,"blueprintId");
		ArrayList<String> bps = new ArrayList<>();
		ArrayList<PhysicalCardImpl> found = new ArrayList<>();

		for(int i = 0; i < choices.length; i++)
		{
			for(PhysicalCardImpl card : cards)
			{
				if(found.contains(card))
					continue;

				if(card.getBlueprintId(true).equals(choices[i]))
				{
					// I have no idea why the spacing is required, but the BP parser skips to the fourth position
					bps.add("    " + i);
					found.add(card);
					break;
				}
			}
		}

		PlayerDecided(player, String.join(",", bps));
		//ChooseCardBPFromSelection(player, Arrays.stream(cards).distinct().map(PhysicalCardImpl::getBlueprintId).toArray(String[]::new));
	}


	/**
	 * Checks whether the given card is one of the options being presented to the Dark Side player.  Will work as
	 * either a blueprint choice (for e.g. selecting from the reserve deck) or as a physical id choice (for selecting
	 * among cards on the table).
	 * @param card The card to search for.
	 * @return True if the given card is one of the options presented to the player, false otherwise.
	 */
	default boolean DSHasCardChoiceAvailable(PhysicalCardImpl card) { return HasCardChoiceAvailable(DS, card);}
	/**
	 * Checks whether the given card is one of the options being presented to the Light Side player.  Will work as
	 * either a blueprint choice (for e.g. selecting from the reserve deck) or as a physical id choice (for selecting
	 * among cards on the table).
	 * @param card The card to search for.
	 * @return True if the given card is one of the options presented to the player, false otherwise.
	 */
	default boolean LSHasCardChoiceAvailable(PhysicalCardImpl card) { return HasCardChoiceAvailable(LS, card);}

	/**
	 * Checks whether all the given cards are included in the options being presented to the Dark Side player.
	 * Will work as either a blueprint choice (for e.g. selecting from the reserve deck) or as a physical id
	 * choice (for selecting among cards on the table).
	 * @param cards One or more cards to search for.
	 * @return True if all the given cards are included as an option presented to the player, false if even 1 is not.
	 */
	default boolean DSHasCardChoicesAvailable(PhysicalCardImpl...cards) {
		for(var card : cards) {
			if(!HasCardChoiceAvailable(DS, card))
				return false;
		}
		return true;
	}
	/**
	 * Checks whether all the given cards are included in the options being presented to the Light Side player.
	 * Will work as either a blueprint choice (for e.g. selecting from the reserve deck) or as a physical id
	 * choice (for selecting among cards on the table).
	 * @param cards One or more cards to search for.
	 * @return True if all the given cards are included as an option presented to the player, false if even 1 is not.
	 */
	default boolean LSHasCardChoicesAvailable(PhysicalCardImpl...cards) {
		for(var card : cards) {
			if(!HasCardChoiceAvailable(LS, card))
				return false;
		}
		return true;
	}

	/**
	 * Checks whether the given card is NOT one of the options being presented to the Dark Side player.  Will work as
	 * either a blueprint choice (for e.g. selecting from the reserve deck) or as a physical id choice (for selecting
	 * among cards on the table).
	 * @param card The card to search for.
	 * @return True if the given card is absent from the options presented to the player, false otherwise.
	 */
	default boolean DSHasCardChoiceNotAvailable(PhysicalCardImpl card) { return !HasCardChoiceAvailable(DS, card);}

	/**
	 * Checks whether the given card is NOT one of the options being presented to the Light Side player.  Will work as
	 * either a blueprint choice (for e.g. selecting from the reserve deck) or as a physical id choice (for selecting
	 * among cards on the table).
	 * @param card The card to search for.
	 * @return True if the given card is absent from the options presented to the player, false otherwise.
	 */
	default boolean LSHasCardChoiceNotAvailable(PhysicalCardImpl card) { return !HasCardChoiceAvailable(LS, card);}

	/**
	 * Checks whether all the given cards are NOT included in the options being presented to the Dark Side player.
	 * Will work as either a blueprint choice (for e.g. selecting from the reserve deck) or as a physical id
	 * choice (for selecting among cards on the table).
	 * @param cards One or more cards to search for.
	 * @return True if all the given cards are absent from the options presented to the player, false if even 1 isn't.
	 */
	default boolean DSHasCardChoicesNotAvailable(PhysicalCardImpl...cards) {
		for(var card : cards) {
			if(HasCardChoiceAvailable(DS, card))
				return false;
		}
		return true;
	}
	/**
	 * Checks whether all the given cards are NOT included in the options being presented to the Light Side player.
	 * Will work as either a blueprint choice (for e.g. selecting from the reserve deck) or as a physical id
	 * choice (for selecting among cards on the table).
	 * @param cards One or more cards to search for.
	 * @return True if all the given cards are absent from the options presented to the player, false if even 1 isn't.
	 */
	default boolean LSHasCardChoicesNotAvailable(PhysicalCardImpl...cards) {
		for(var card : cards) {
			if(HasCardChoiceAvailable(LS, card))
				return false;
		}
		return true;
	}

	/**
	 * Checks whether the given card is one of the options being presented to the player.  Will work as either a blueprint
	 * choice (for e.g. selecting from the reserve deck) or as a physical id choice (for selecting among cards on the
	 * table).
	 * @param player The player currently presented with a decision
	 * @param card The card to search for
	 * @return True if the given card is one of the options presented to the player, false otherwise.
	 */
	default boolean HasCardChoiceAvailable(String player, PhysicalCardImpl card) {
		String[] choices = GetADParam(player,"blueprintId");
		if(choices != null) {
			for (String choice : choices) {
				if (card.getBlueprintId(true).equals(choice))
					return true;
			}
			return false;
		}

		choices = GetADParam(player,"cardId");
		if(choices != null) {
			for (String choice : choices) {
				if (card.getCardId() == Integer.parseInt(choice))
					return true;
			}
			return false;
		}


		return false;
	}

	/**
	 * Causes the Dark Side player to issue a decision response composed of a comma-separated list of the provided
	 * card IDs.  This is used when e.g. the player must choose one or more targets for an effect.  This will only
	 * succeed if being used to target currently live cards; it will not work if being presented with a choice of
	 * out-of-play cards (such as when choosing from the reserve deck).
	 * @param cards The cards to include in the decision response.
	 */
	default void DSChooseCardIDFromSelection(PhysicalCardImpl...cards) { ChooseCardIDFromSelection(DS, cards);}
	/**
	 * Causes the Light Side player to issue a decision response composed of a comma-separated list of the provided
	 * card IDs.  This is used when e.g. the player must choose one or more targets for an effect.  This will only
	 * succeed if being used to target currently live cards; it will not work if being presented with a choice of
	 * out-of-play cards (such as when choosing from the reserve deck).
	 * @param cards The cards to include in the decision response.
	 */
	default void LSChooseCardIDFromSelection(PhysicalCardImpl...cards) { ChooseCardIDFromSelection(LS, cards);}

	/**
	 * Causes the given player to issue a decision response composed of a comma-separated list of the provided card IDs.
	 * This will only succeed if being used to target currently live cards; it will not work if being presented with a
	 * choice of out-of-play cards (such as when choosing from the reserve deck).
	 * @param player The player to issue a decision for.
	 * @param cards The cards to include in the decision response.
	 */
	default void ChooseCardIDFromSelection(String player, PhysicalCardImpl...cards) {
		AwaitingDecision decision = userFeedback().getAwaitingDecision(player);
		//PlayerDecided(player, "" + card.getCardId());

		String[] choices = GetADParam(player,"cardId");
		ArrayList<String> ids = new ArrayList<>();
		ArrayList<PhysicalCardImpl> found = new ArrayList<>();

		for (String choice : choices) {
			for (PhysicalCardImpl card : cards) {
				if (found.contains(card))
					continue;

				if (("" + card.getCardId()).equals(choice)) {
					ids.add(choice);
					found.add(card);
					break;
				}
			}
		}

		PlayerDecided(player, String.join(",", ids));
	}
}
