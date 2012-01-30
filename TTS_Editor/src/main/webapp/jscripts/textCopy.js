function copyText(elementId, hiddenTFid)
{
	var element=document.getElementById(elementId);
	var start = element.selectionStart;
	var end = element.selectionEnd;
	var text = element.value.substring(start, end);
//	document.getElementById(hiddenTFid).value = text;
//	document.getElementById(hiddenTFid).value = end;
	var startEndPoints = start + '\t' + end;
//	alert(startEndPoints);
	document.getElementById(hiddenTFid).value = startEndPoints;
//	document.getElementById(hiddendTFid).value = startEndPoints;
}