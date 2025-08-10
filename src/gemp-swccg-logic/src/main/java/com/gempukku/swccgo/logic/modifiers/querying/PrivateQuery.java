package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.game.PhysicalCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Various private functions that need to be accessible to other Query functions, but aren't exposed
 * to the Gemp engine as a whole.  This interface is **NOT** implemented by ModifiersQuerying.
 */
public interface PrivateQuery extends BaseQuery {
	/**
	 * Generates a list of the card usage order permutations.
	 * @param cardList the cards
	 * @param maxUsableByCard the max amount of opponent's Force the card allows
	 * @return list of the card order permutations
	 */
	default List<List<PhysicalCard>> generateCardUsageOrderPermutations(List<PhysicalCard> cardList, Map<PhysicalCard, Integer> maxUsableByCard) {
		List<PhysicalCard> expandedCardList = new ArrayList<PhysicalCard>();
		for (PhysicalCard card : cardList) {
			for (int i=0; i<maxUsableByCard.get(card); ++i) {
				expandedCardList.add(card);
			}
		}
		List<List<PhysicalCard>> allPermutations = new ArrayList<List<PhysicalCard>>();
		//this next line breaks the server when expandedCardList.size() >= 10
		//generateCardListPermutations(allPermutations, new ArrayList<PhysicalCard>(), new ArrayList<PhysicalCard>(expandedCardList));
		//I added this next line as a temporary fix to only add the expandedCardList instead of recursively generating all permutations of the list
		allPermutations.add(expandedCardList);
		//I don't actually know what is lost by making this change. From my testing, Beggar and R'tic H'weei they work as expected

		return allPermutations;
	}

	/**
	 * Generates all the card permutations (recursively).
	 * @param allPermutations all the permutations
	 * @param prefixList prefix list
	 * @param suffixList suffix list
	 */
	default void generateCardListPermutations(List<List<PhysicalCard>> allPermutations, List<PhysicalCard> prefixList, List<PhysicalCard> suffixList) {
		int n = suffixList.size();
		if (n == 0) {
			allPermutations.add(prefixList);
		}
		else {
			for (int i = 0; i < n; ++i) {
				List<PhysicalCard> newPrefixList = new ArrayList<PhysicalCard>();
				newPrefixList.addAll(prefixList);
				newPrefixList.add(suffixList.get(i));
				List<PhysicalCard> newSuffixList = new ArrayList<PhysicalCard>();
				newSuffixList.addAll(suffixList.subList(0, i));
				newSuffixList.addAll(suffixList.subList(i+1, n));
				generateCardListPermutations(allPermutations, newPrefixList, newSuffixList);
			}
		}
	}

	/**
	 * Determine the cards that must be used to use opponent's Force first in order to use the specified amount of opponent's Force.
	 * @param forceToUse the amount of Force to attempt to use
	 * @param opponentsForcePileSize the size of opponents Force Pile
	 * @param cardUsageOrder the order of cards to use that allow using opponent's Force
	 * @param minForcePileRequiredByCard the minimum amount of Force in opponent's Force Pile required for the card to allow using opponent's Force
	 * @return the list containing a card that must be used first
	 */
	default int getMaxOpponentsForceFirstCardCanUseInUsageOrder(int forceToUse, int opponentsForcePileSize, List<PhysicalCard> cardUsageOrder, Map<PhysicalCard, Integer> minForcePileRequiredByCard) {
		if (cardUsageOrder.isEmpty() || (cardUsageOrder.size() < forceToUse)) {
			return 0;
		}

		int forceUsedByFirstCard = 0;
		boolean foundOtherCard = false;
		int opponentsForcePileSizeLeft = opponentsForcePileSize;
		for (int i=0; i<forceToUse; ++i) {
			PhysicalCard cardToUse = cardUsageOrder.get(i);
			if (opponentsForcePileSizeLeft < minForcePileRequiredByCard.get(cardToUse)) {
				return 0;
			}
			if (cardToUse.getCardId() != cardUsageOrder.get(0).getCardId()) {
				foundOtherCard = true;
			}
			if (!foundOtherCard) {
				forceUsedByFirstCard++;
			}
			opponentsForcePileSizeLeft--;
		}
		return forceUsedByFirstCard;
	}


}
