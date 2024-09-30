class CardInfoDialog {
	infoDialog = null;

	outerDiv = null;
	zoomSlider = null;
	imageDiv = null;
	detailsDiv = null;
	hasDetails = false;

	cardDisplay = null;

	maxHeight = null;
	maxWidth = null;

	card = null;

	sliding = false;
	currentLongSetting = CardDisplay.MinTarget;
	curretnShortSetting = CardDisplay.MinTarget * CardDisplay.TargetVertRatio;

	static DetailsWidth = 400;
	static DialogPadding = 25;
	//Any narrower than this and the title wraps
	static MinWidth = 175;

	static SliderCookieName = "card-info-dialog-slider-last-value";

	constructor(maxWidth, maxHeight) {
		var that = this;

		this.infoDialog = $("<div></div>")
			.dialog({
				autoOpen: false,
				closeOnEscape: true,
				resizable: false,
				title: "Card Information"
			});

		this.outerDiv = $("<div>", {
			id: "card-info-outer"
		}).css({
			display: "flex",
			"flex-direction": "column",
			position: "relative",
			width: "100%",
			height: "100%"
		}).appendTo(this.infoDialog);

		// Defaults to 0.5 (i.e. half of the remastered size, just a hair over the default image size)
		let sliderCookie = loadFromCookie(CardInfoDialog.SliderCookieName, 0.5);

		this.zoomSlider = $("<div>", {
			id: "card-info-slider",
			title: "Card Size"
		}).css({
			"flex-grow": "0"
		}).slider({
			value: sliderCookie,
			min: 0.4,
			max: 1,
			range: "min",
			animate: true,
			step: 0.1,
			disabled:false,
			slide:function (event, ui) {
				that.updateCurrentZoom(ui.value, true);
			},
			start:function(event, ui) {
				that.sliding = true;
			},
			stop:function(event, ui) {
				setTimeout(function() {
					that.sliding = false;
				  }, 1);
				
			}
		}).appendTo(this.outerDiv);

		if(onTouchDevice()) {
			this.zoomSlider.css({
				display: "none"
			});
		}

		this.updateCurrentZoom(sliderCookie);

		var inner = $("<div>", {
			id: "card-info-inner"
		}).css({
			display: "flex",
			"flex-direction": "row",
			"flex-grow": "1"
		}).appendTo(this.outerDiv);

		this.imageDiv = $("<div>", {
			id: "card-image-content"
		}).css({
			"flex-grow": "0"
		}).appendTo(inner);

		this.cardDisplay = new CardDisplay();
		this.cardDisplay.appendTo(this.imageDiv);

		this.detailsDiv = $("<div>", {
			id: "cardEffects"
		}).css({
			"flex-grow": "1",
			padding: 20
		}).appendTo(inner);

		var swipeOptions = {
			threshold: 20,
			swipeUp: function (event) {
				that.infoDialog.prop({ scrollTop: that.infoDialog.prop("scrollHeight") });
				return false;
			},
			swipeDown: function (event) {
				that.infoDialog.prop({ scrollTop: 0 });
				return false;
			}
		};
		this.infoDialog.swipe(swipeOptions);

		this.updateMaxBoundaries(maxWidth, maxHeight);
	}

	updateCurrentZoom(long, triggerResize) {
		if(!long) {
			this.zoomSlider.trigger("slidechange");
			return;
		}
		// long should be a value between 0.1 and 1.0
		//console.log(long);
		saveToCookie(CardInfoDialog.SliderCookieName, "" + long);
		this.currentLongSetting = long * CardDisplay.MaxTarget;
		this.currentShortSetting = this.currentLongSetting * CardDisplay.TargetVertRatio;
		if(triggerResize) {
			this.resize();
		}
	}

	updateMaxBoundaries(width, height) {
		this.maxWidth = width - CardInfoDialog.DetailsWidth - CardInfoDialog.DialogPadding; 

		this.maxHeight = height - (CardInfoDialog.DialogPadding * 3);

		this.resize();
	}

	resize() {
		if(!this.card)
			return;

		let maxLong = Math.min(this.maxHeight, this.maxWidth, this.currentLongSetting);
		let maxShort = Math.min(this.maxHeight, this.maxWidth, this.currentShortSetting);

		//We have to compensate for horizontal cards vertically rotated on the lost pile
		let horizontal = this.card.horizontal || this.card.effectivelyHorizontal();

		if(horizontal) {
			this.cardDisplay.resize(horizontal, maxLong, maxLong);
		}
		else {
			this.cardDisplay.resize(horizontal, maxLong, maxLong);
		}

		let width = this.cardDisplay.width() + CardInfoDialog.DialogPadding + (this.hasDetails ? CardInfoDialog.DetailsWidth : 0)
		width = Math.max(width, CardInfoDialog.MinWidth);

		let height = this.cardDisplay.height() + (CardInfoDialog.DialogPadding * 2);
		//height = Math.min(height, maxLong);

		this.infoDialog.dialog({ 
			width: width, 
			height: height  
		});

		let rect = this.infoDialog[0].getBoundingClientRect();

		//The dialog open event does some black magic to keep the dialog
		// on-screen, and who knows how to replicate it.  We'll just
		// toggle the dialog closed/open to invoke that magic if our edges
		// are out of bounds.
		if(rect.top + height + 10 > this.maxHeight ||
			rect.left + width + 10 > this.maxWidth
		) {
			this.infoDialog.dialog("close");
			this.open();
		}
	}

	setDetails(html) {
		this.detailsDiv.html(html);
		this.detailsDiv.css({
			padding: 20
		});
		this.hasDetails = true;
	}

	isOpen() {
		return this.infoDialog.dialog("isOpen");
	}

	open() {
		this.infoDialog.dialog("open");
	}

	mouseUp() {
		if(!this.sliding) {
			this.close();
		}
	}

	close() {
		this.infoDialog.dialog("close");
		this.clearImage();
		this.clearDetails();
	}

	clearImage() {
		this.cardDisplay.clear();
	}

	clearDetails() {
		this.setDetails("");
		this.detailsDiv.css({
			padding: 0
		});
		this.hasDetails = false;
	}

	setCard(card) {
		this.card = card;

		this.cardDisplay.reloadFromCard(card, this.maxWidth, this.maxHeight);

		if (card.inverted == true) {
			this.cardDisplay.setInvert(true);
		}

		this.cardDisplay.addInvertClick();

		this.updateCurrentZoom();
		this.resize();
	}

	showCard(card, details) {
		if(details) {
			this.setDetails(details);
		}
		else {
			this.clearDetails();
		}
		this.setCard(card);
		
		this.open();
	}
}