class AutoZoom {
	showPreviewImage = true;
	previewImageBPID = 0;
	cookieName = null;
	
	isTouchDevice = false;
	
	autoZoomToggle = null;
	previewImageDiv = null;
	cardDisplay = null;
	
	previewImage = null;
	flipMessageDiv = null;
	//The actual card on the table, referenced so that we know
	// what the original rotation is.
	baseImageDiv = null;

	hoverTimer = null;
	hoverValid = false;
	cooldownTimer = null;

	static HoverDelay = 500;
	static HoverCooldown = 500;

	constructor(cookieName) {
		const that = this;
		this.cookieName = cookieName;
		this.isTouchDevice = onTouchDevice();

		//An unset cookie should default to true.
		this.showPreviewImage = loadFromCookie(this.cookieName, "true") === "true";
		
		if(!this.isTouchDevice) {
			this._setupToggleButton();
		}

		this.previewImageDiv = $('<div>', {
			id: 'previewImage',
			class: 'previewImage',
			style: ""
		}).appendTo('body');
		
		this.cardDisplay = new CardDisplay();
		this.cardDisplay.baseDiv.appendTo(this.previewImageDiv);
		this.cardDisplay.baseDiv.css({
			position: "absolute"
		});
		
		this.flipMessageDiv = $('<div>', {
			id: 'auto-zoom-message'
		}).appendTo(this.cardDisplay.baseDiv);
		
		this.previewImageDiv = this.previewImageDiv[0];
		
		this.hidePreviewImage();		
	}
	
	_setupToggleButton() {
		const that = this;
		const enabledIcon = "ui-icon-search";
		const disabledIcon = "ui-icon-circle-close";
		
		const startingIcon = this.showPreviewImage ? enabledIcon : disabledIcon;
		
		this.autoZoomToggle = $("<button id='auto-zoom-toggle'>Auto-zoom cards on hover</button>").button(
		{
			icons:{
				primary:startingIcon
			}, 
			text:false
		});
		
		this.autoZoomToggle.click(
			function () {
				if (that.showPreviewImage) {
					that.autoZoomToggle.button("option", "icons", {primary:disabledIcon});
					that.showPreviewImage = false;
					saveToCookie(that.cookieName, "" + that.showPreviewImage);
				} else {
					that.autoZoomToggle.button("option", "icons", {primary:enabledIcon});
					that.showPreviewImage = true;
					saveToCookie(that.cookieName, "" + that.showPreviewImage);
				}
			});
	}
	
	// make the preview image shown be the reference image that's hovered on:
	displayPreviewImage(card, refImageDiv) {
	
		const that = this;

		// get the size of the browser window:
		var windowWidth = window.innerWidth;
		var windowHeight = window.innerHeight;
		var windowRatio = windowWidth / windowHeight;
		
		//We want horizontal and vertical representations of cards to 
		// match the same size, else depending on the screen some cards will
		// be big and others small based entirely on their orientation.
		var maxLongSide  = windowHeight * 0.9;
		
		//If we are on a vertically-oriented browser window (not a phone, 
		// because this feature is disabled on mobile)
		if(windowRatio <= 1) {
			maxLongSide = windowWidth * 0.9;
		}
		
		var maxShortSide = maxLongSide * CardDisplay.TargetVertRatio;
		
		// Some cards are remastered at a higher resolution, but not all.  So we
		// will stretch the lower-res cards to the higher resolution, to avoid 
		// disparate sizes, while keeping them within the max window bounds.
		var targetLong = Math.min(maxLongSide, CardDisplay.TargetLong);
		var targetShort = Math.min(maxShortSide, CardDisplay.TargetShort);

		if(card.horizontal || card.effectivelyHorizontal()) {
			this.cardDisplay.reloadFromCard(card, targetLong, targetShort);
		}
		else {
			this.cardDisplay.reloadFromCard(card, targetShort, targetLong);
		}
		
		// get position and size of the reference image (or card hint):
		var rect = refImageDiv.getBoundingClientRect();
		const isHint = $(refImageDiv).hasClass("cardHint");
		if(isHint) {
			//For hints (i.e. the links in the chat log) we use the parent, which is
			// the whole chat line, which prevents us from covering up too much log
			// text with the hover preview.
			rect = $(refImageDiv).parent()[0].getBoundingClientRect();
		}

		var srcImageX = rect.left;
		var srcImageY = rect.top;
		var srcImageWidth = rect.right - rect.left;
		var srcImageHeight = rect.bottom - rect.top;

		var previewImageWidth = this.cardDisplay.baseDiv.width();
		var previewImageHeight = this.cardDisplay.baseDiv.height();

		var imageRatio = previewImageWidth / previewImageHeight;

		// set the horizontal position of the preview image:
		const rightEdge = srcImageX + srcImageWidth - 15;
		const leftEdge = srcImageX;
		const goesPastRightBound = rightEdge + previewImageWidth > windowWidth;
		const goesPastLeftBound = leftEdge - previewImageWidth < 0;
		var previewImageLeft = rightEdge;
		
		if (goesPastRightBound && goesPastLeftBound) {
			// if previewImage would extend past either left or right side
			// of screen, (i.e. it is the center location on a narrow display)
			// then we must find the best place to put it.
			
			//display the previewImage in the biggest space 
			// available and shrink to fit
			const rightSpace = windowWidth - (leftEdge + srcImageWidth);
			const leftSpace = leftEdge;
			
			if (rightSpace > leftSpace) {
				previewImageWidth = rightSpace;
				previewImageLeft = rightEdge;
			}
			else {
				previewImageWidth = leftSpace;
				previewImageLeft = leftEdge - previewImageWidth;
			}
			previewImageHeight = previewImageWidth / imageRatio;
		}
		else {
			if (goesPastRightBound) {
				previewImageLeft = leftEdge - previewImageWidth;
			}
			else if (goesPastLeftBound) {
				previewImageLeft = rightEdge;
			}
		}

		// set the vertical position of the preview image (and make sure it isn't extending over the edge of the window):
		var previewImageTop = (srcImageY + (srcImageHeight / 2)) - (previewImageHeight / 2);
		//console.log("previewImageTop: " + previewImageTop);
		if ((previewImageTop + previewImageHeight + 15) > windowHeight) {
			previewImageTop = windowHeight - previewImageHeight - 15;
		}
		else if (previewImageTop < 0) {
			previewImageTop = 0;
		}

		this.cardDisplay.baseDiv[0].style.left = previewImageLeft + "px";
		this.cardDisplay.baseDiv[0].style.top = previewImageTop + "px";
		
		previewImageTop = (srcImageY + (srcImageHeight / 2)) - (previewImageHeight / 2);;
	}

	hidePreviewImage() {
		this.cardDisplay.clear();

		this.hidePreviewMessage();
	}

	invertPreviewImage(shiftHeld) {
		const invertShift = !this.cardDisplay.reversible  
			&& this.baseImageDiv.style.transform.includes("180");
		//If the base image is already rotated (such as a location facing 
		// the player), then we act as if Shift is held, even if it's not.  
		// However if shift IS held AND it's rotated, we act like it's not.  
		// This is basically XOR; when they are the same they cancel out,
		// but when they are different they cause a rotation.
		this.cardDisplay.setInvert(shiftHeld != invertShift);
	}
	
	setPreviewMessage(reversible) {
		let message = "";
		
		let focus = document.hasFocus();
		
		if(reversible) {
			message = "Tap <b>[Shift]</b> to flip.";
		}
		else if(this.baseImageDiv.style.transform.includes("180")) {
			message = "Hold <b>[Shift]</b> to rotate.";
		}
		
		if(message) {
			
			if(!focus) {
				message = "Focus this window for key controls."
			}
			
			this.flipMessageDiv.html(message);
			this.flipMessageDiv[0].style.display = "block";
		}
		else {
			this.hidePreviewMessage();	
		}
	}
	
	hidePreviewMessage() {
		this.flipMessageDiv.html("");
		this.flipMessageDiv[0].style.display = "none";
	}

	triggerHover(target, shift) {
		const refCard = target.closest(".card");
		this.baseImageDiv = refCard[0];
		var card = refCard.data("card");

		// don't show preview image if card is animating
		if (!$(this.baseImageDiv).hasClass('card-animating')) {

			let bp = card.bareBlueprint;
			// don't show preview image if hovered card is the DS/LS card back art
			if (bp !== "-1_1" && bp !== "-1_2") {
				this.displayPreviewImage(card, this.baseImageDiv);
				this.invertPreviewImage(shift);
				this.setPreviewMessage(this.cardDisplay.reversible);
			}
		}
		else if (this.cardDisplay.populated) {
			this.hidePreviewImage();
		}
	}
	
	
	triggerHintHover(target, shift) {
		const blueprintId = target.attr("value");
		const testingText = target.attr("data-testingText");
		const backSideTestingText = target.attr("data-backSideTestingText");
		const card = new Card(blueprintId, testingText, backSideTestingText, "SPECIAL", "hint", "");

		this.baseImageDiv = target[0];
		this.displayPreviewImage(card, this.baseImageDiv);
		this.invertPreviewImage(shift);
		this.setPreviewMessage(this.cardDisplay.reversible);

		this.abortHoverTimer();
		this.hoverValid = true;
	}
	
	handleMouseOver(event, isDragging, infoDialogOpen) {
		const that = this;
		const target = $(event.target);
		const tarIsCard = target.hasClass("actionArea");
		const tarIsHint = target.hasClass("cardHint");
		
		// Reasons to cancel the popup: we're on a touch device,
		// auto zoom has been disabled, we're not hovering over a card,
		// we are currently click-dragging, the card preview box is open.
		if(this.isTouchDevice || !this.showPreviewImage || (!tarIsHint && !tarIsCard)
			 || isDragging || infoDialogOpen) {
			
			if (this.cardDisplay.populated) {
				this.hidePreviewImage();
				event.stopPropagation();
				return false;
			}
			
			return true;
		}

		this.abortCooldownTimer();

		if(this.hoverValid) {
			if(tarIsCard) {
				this.triggerHover(target, event.shiftKey);	
			}
			else if(tarIsHint) {
				this.triggerHintHover(target, event.shiftKey);
			}

			event.stopPropagation();
			return false;
		}
		else if(!this.hoverTimer) {
			this.startHoverTimer(function(){
				if(tarIsCard) {
					that.triggerHover(target, event.shiftKey);	
				}
				else if(tarIsHint) {
					that.triggerHintHover(target, event.shiftKey);
					event.stopPropagation();
				}
			});
		}

		return true;
	}

	startHoverTimer(callback) {
		var that = this;
		
		this.hoverTimer = setTimeout(function(){
			if(that.hoverTimer !== null) {
				//console.log("completing hoverTimer" + that.hoverTimer);
				that.hoverTimer = null;
				that.hoverValid = true;
	
				if(callback !== undefined) {
					callback();
				}
			}
		}, AutoZoom.HoverDelay);

		//console.log("beginning hoverTimer " + this.hoverTimer);
	}

	abortHoverTimer() {
		//console.log("aborting hoverTimer" + this.hoverTimer);
		clearTimeout(this.hoverTimer);
		this.hoverTimer = null;
	}

	startCooldownTimer() {
		var that = this;

		this.cooldownTimer = setTimeout(function(){
			if(that.cooldownTimer !== null) {
				//console.log("completing cooldownTimer" + that.cooldownTimer);
				that.cooldownTimer = null;
				that.hoverValid = false;
			}
		}, AutoZoom.HoverCooldown);

		//console.log("beginning cooldownTimer" + this.cooldownTimer);
	}

	abortCooldownTimer() {
		//console.log("aborting cooldownTimer" + this.cooldownTimer);
		clearTimeout(this.cooldownTimer);
		this.cooldownTimer = null;
	}

	handleMouseOut(event) {
		if(this.hoverTimer) {
			this.abortHoverTimer();

			event.stopPropagation();
			return false;
		}

		if(this.hoverValid && !this.cooldownTimer) {
			this.startCooldownTimer();

			event.stopPropagation();
			return false;
		}

		return true;
	}
	
	handleMouseDown(event) {
		this.abortHoverTimer();
		this.startCooldownTimer();
		if (this.cardDisplay.populated) {
			this.hidePreviewImage();
		}
	}
	
	handleKeyDown(event) {
		if (!event.repeat && this.showPreviewImage && !this.isTouchDevice 
				&& event.key === "Shift" && this.cardDisplay.populated) {
			
			if(this.cardDisplay.reversible) {
				this.cardDisplay.invert();
			}
			else {
				this.invertPreviewImage(true);
			}
			
		}
		
		return true;
	}
	
	handleKeyUp(event) {
		if (this.showPreviewImage && !this.isTouchDevice 
				&& event.key === "Shift" && this.cardDisplay.populated) {
			//This makes only presses work for reversibles
			if(!this.cardDisplay.reversible) {
				this.invertPreviewImage(false);
			}
		}
		
		return true;
	}
	
}
