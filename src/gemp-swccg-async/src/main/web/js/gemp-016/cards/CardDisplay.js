
/*
	A low-level class for displaying a card.  This handles the sizing and layout given maximum boundaries,
	and also overlays foiling and borders.  This is used by both the AutoZoomHandler and the CardInfoDialog.
*/
class CardDisplay {
	
	baseDiv = null;
	fullCardDiv = null;
	cardImage = null;

	foilDiv = null;
	testingTextDiv = null;
	borderDiv = null;

	populated = false;
	currentBP = null;
	inverted = false;

	reversible = false;
	frontside = null;
	backside = null;


	static FoilImage	= "https://res.starwarsccg.org/cards/holo.jpg";
	static PixelOverlay = "https://res.starwarsccg.org/gemp/pixel.png";
	
	static TargetLong  = 1039;
	static TargetShort = 745;

	static MaxTarget = 1039;
	static MinTarget = 497;

	static TargetHorizRatio = CardDisplay.TargetLong / CardDisplay.TargetShort;
	static TargetVertRatio  = CardDisplay.TargetShort / CardDisplay.TargetLong;
	
	constructor(card, maxWidth, maxHeight) {
		this.baseDiv = $('<div>').css({
			position: "relative"
		});
		
		this.fullCardDiv = $('<div>', {
			class: 'fullcard'
		}).css({
			position: "absolute"
		}).appendTo(this.baseDiv);

		// Actual card image		
		this.cardImage = $('<img>').appendTo(this.fullCardDiv)[0];

		// Optional foil layer
		this.foilDiv = $('<div>', {
			class: 'foilOverlay'
		}).css({
			position: "absolute",
			display: "none"
		}).appendTo(this.baseDiv);
		
		$('<img>', {
			src: CardDisplay.FoilImage
		}).appendTo(this.foilDiv);

		// Optional playtest placeholder text
		this.testingTextDiv = $('<div>', {
			class: 'testingTextOverlay'
		}).css({
			position: "absolute",
		}).appendTo(this.baseDiv);

		// Technically optional border
		this.borderDiv = $('<div>', {
			class: "borderOverlay"
		}).css({
			position: "absolute",
			borderWidth: "16px"
		}).appendTo(this.baseDiv);
		
		$('<img>', {
			class: "actionArea",
			src: CardDisplay.PixelOverlay,
			width: "100%",
			height: "100%"
		}).appendTo(this.borderDiv);

		if(card !== undefined && maxWidth !== undefined && maxHeight !== undefined) {
			this.reloadFromCard(card, maxWidth, maxHeight);
		}
	}

	clear() {
		this.baseDiv.css({
			display: "none"
		});
		this.cardImage.src = "";
		this.setInvert(false);

		this.populated = false;
		this.currentBP = null;

		this.reversible = false;
		this.frontside = null;
		this.backside = null;
	}

	reloadFromCard(card, maxWidth, maxHeight, noborder) {
		this.currentBP = card.blueprintId;
		this.frontside = card.imageUrl;
		if(!this.frontside) {
			this.frontside = Card.getImageUrl(this.currentBP);
		}
		
		let back = card.backSideImageUrl;
		if(back && !back.includes("darkcardback") && !back.includes("lightcardback")) {
			this.reversible = true;
			this.backside = back;
		}
		else {
			//Attempt to look it up instead by card ID
			back = Card.getBackSideBlueprintId(this.currentBP);
			//We don't care about the LS/DS card back
			if(back && !back.startsWith("-")) {
				this.reversible = true;
				this.backside = Card.getImageUrl(back);
			}
			else {
				this.reversible = false;
				this.backside = null;
			}
		}
		
		

		this.reload(maxWidth, maxHeight, card.imageUrl, 
			card.horizontal || card.effectivelyHorizontal(), card.foil, noborder, card.testingText);
	}

	reload(maxWidth, maxHeight, image, horizontal, foil, noborder, testingText) {
		this.cardImage.src = image;
		this.cardImage.style.transform = "rotate(0deg)";

		this.foilDiv.css({
			display: foil ? "initial" : "none"
		});

		this.testingTextDiv.css({
			display: testingText ? "initial" : "none"
		});

		this.baseDiv.css({
			display: "inline-block"
		});

		this.populated = true;

		if(maxWidth !== undefined && maxHeight !== undefined) {
			this.resize(horizontal, maxWidth, maxHeight, noborder);
		}
	}

	resize(horizontal, maxWidth, maxHeight, noBorder) {
		const maxLongSide  = Math.min(maxWidth, maxHeight, CardDisplay.TargetLong);
		const maxShortSide = maxLongSide * CardDisplay.TargetVertRatio;

		var widthSide, heightSide;
		if(horizontal) {
			widthSide  = Math.floor(Math.min(maxLongSide, maxWidth));
			heightSide = Math.floor(Math.min(maxShortSide, maxHeight));
		}
		else {
			widthSide  = Math.floor(Math.min(maxShortSide, maxWidth));
			heightSide = Math.floor(Math.min(maxLongSide, maxHeight));
		}

		//Calculating the various borders sizes proportionate to the longest side
		const borderSize = Math.floor(Math.max(widthSide, heightSide) / 30);
		const testBorderWidth = Math.floor(Math.max(widthSide, heightSide) / 24);
		const testBorderHeight = Math.floor(Math.max(widthSide, heightSide) / 27.6);

		let px = (int) => "" + int + "px";

		this.baseDiv.css({
			width: px(widthSide),
			height: px(heightSide)
		});

		this.cardImage.width = widthSide;
		this.cardImage.height = heightSide;

		this.foilDiv.css({
			width: px(widthSide),
			height: px(heightSide)
		});

		this.testingTextDiv.css({
			inset: px(testBorderHeight) + " " + px(testBorderWidth),
		});

		if(noBorder) {
			this.borderDiv.css({ borderWidth: "0px" });
			this.borderDiv.class = "borderOverlay noBorder";
		}
		else {
			this.borderDiv.css({ "border-width": px(borderSize) });
			this.borderDiv.class = "borderOverlay";
		}

		this.borderDiv.css({
			width: px(widthSide - (2 * borderSize) + 2), //Covering up white bordered cards a little better on the sides
			height: px(heightSide - (2 * borderSize) + 2)
		})
	}

	setInvert(invert) {
		this.inverted = invert;
		
		if(this.reversible) {
			if(!invert) {
				this.cardImage.src = this.frontside;
			}
			else {
				this.cardImage.src = this.backside;
			}
		}
		else {
			//If a card is already "naturally" inverted, such as a location
			// that is owned by your opponent, then we treat the incoming
			// shift button instruction as if it's the opposite of what it is.
			if(this.baseDiv.style && this.baseDiv.style.transform.includes("180") && invert) {
				invert = !invert;
			}
			
			if(!invert) {
				this.cardImage.style.transform = "rotate(0deg)";
			}
			else {
				this.cardImage.style.transform = "rotate(180deg)";
			}
		}
	}

	invert() {
		this.setInvert(!this.inverted);
	}

	appendTo(parent) {
		this.baseDiv.appendTo(parent);
	}

	width() {
		return this.baseDiv.width();
	}

	height() {
		return this.baseDiv.height();
	}

	addInvertClick() {
		var that = this;

		this.baseDiv.unbind('click');
		this.baseDiv.click(
			function(event) {
				that.invert();
				event.stopPropagation();
			});
	}
}
