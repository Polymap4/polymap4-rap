/**
 * Renders a progress bar.
 * 
 * @param {Element}
 *            el The target element.
 * @constructor
 */
function Progress(el) {
	this.el = el;
	this.loading = 0;
	this.loaded = 0;
}

/**
 * Increment the count of loading tiles.
 */
Progress.prototype.addLoading = function() {
	//console.log("addLoading: " + this.loading + ";" + this.loaded);
	if (this.loading === 0) {
		this.show();
	}
	++this.loading;
	this.update();
};

/**
 * Increment the count of loaded tiles.
 */
Progress.prototype.addLoaded = function() {
	//console.log("addLoaded: " + this.loading + ";" + this.loaded);
	var this_ = this;
	setTimeout(function() {
		++this_.loaded;
		this_.update();
	}, 100);
};

/**
 * Update the progress bar.
 */
Progress.prototype.update = function() {
	var width = this.loaded === 0 ? '5%' : (this.loaded / this.loading * 100).toFixed(1) + '%';
	//console.log("update: " + this.loading + ";" + this.loaded + ";" + width);
	this.el.style.width = width;
	if (this.loading === this.loaded) {
		this.loading = 0;
		this.loaded = 0;
		var this_ = this;
		setTimeout(function() {
			this_.hide();
		}, 500);
	}
};

/**
 * Show the progress bar.
 */
Progress.prototype.show = function() {
	this.el.style.visibility = 'visible';
};

/**
 * Hide the progress bar.
 */
Progress.prototype.hide = function() {
	//console.log("hide: " + this.loading + ";" + this.loaded);
	if (this.loading === this.loaded) {
		this.el.style.visibility = 'hidden';
		this.el.style.width = 0;
		this.loading = 0;
		//this.loaded = 0;
	}
};