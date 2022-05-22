// https://stackoverflow.com/a/41545988
const ClipboardHelper = {
	copyElement: function ($element) {
		this.copyText($element.text())
	},
	copyText: function (text) // Linebreaks with \n
	{
		var $tempInput = $("<textarea>");
		$("body").append($tempInput);
		$tempInput.val(text).select();
		document.execCommand("copy");
		$tempInput.remove();
	}
};